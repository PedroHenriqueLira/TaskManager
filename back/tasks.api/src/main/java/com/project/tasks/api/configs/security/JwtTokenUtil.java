package com.project.tasks.api.configs.security;

import com.project.tasks.api.model.Usuario;
import com.project.tasks.api.repository.UsuarioRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.fixed-token}")
    private String fixedToken;

    private Key signingKey;

    private static final long JWT_TOKEN_VALIDITY = 3600000; // 1 hora

    public JwtTokenUtil(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Gera token com validade padrão de 1 hora
     */
    public String generateToken(String username, String permissions) {
        Claims claims = Jwts.claims().setSubject(username);
        List<String> permissionsList = Arrays.asList(permissions.split(","));
        claims.put("permissions", permissionsList);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Gera token com tempo de expiração customizado (em minutos)
     */
    public String generateTokenWithExpiration(String email, int expirationMinutes) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMinutes * 60 * 1000);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isFixedToken(String token) {
        return token != null && token.equals(fixedToken);
    }

    public boolean validateToken(String token) {
        if (isFixedToken(token)) return true;

        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return isFixedToken(token) ? "TOKEN_FIXO" : getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getTokenFromRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) return null;

        String header = attr.getRequest().getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    public Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) return null;

        Object principal = authentication.getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        return usuarioRepository.getUsuariosByEmail(username);
    }

    public boolean isAdmin(Usuario usuario) {
        return usuario != null && "ADMIN".equalsIgnoreCase(usuario.getTipoUser());
    }
}
