package com.project.tasks.api.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedKeys = new ConcurrentHashMap<>();

    private static final int REQ_PER_SECOND = 20;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(REQ_PER_SECOND, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(REQ_PER_SECOND, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String userId = extractUserEmailFromRequest(request);

        String key = (userId != null && !userId.isEmpty()) ? clientIp + ":" + userId : clientIp;

        Instant blockedUntil = blockedKeys.get(key);
        if (blockedUntil != null) {
            if (Instant.now().isBefore(blockedUntil)) {
                writeErrorResponse(response, "Usuário/IP temporariamente bloqueado devido a muitas requisições. Tente novamente mais tarde.");
                return;
            } else {
                blockedKeys.remove(key);
                buckets.remove(key);
            }
        }

        Bucket bucket = getBucket(key);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            blockedKeys.put(key, Instant.now().plus(BLOCK_DURATION));
            writeErrorResponse(response, "Usuário/IP temporariamente bloqueado devido a muitas requisições. Tente novamente mais tarde.");
        }
    }

    private String extractUserEmailFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                return jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statusCode", 429);
        body.put("error", "Too Many Requests");
        body.put("timestamp", Instant.now().toString());
        body.put("message", message);

        String json = new ObjectMapper().writeValueAsString(body);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
