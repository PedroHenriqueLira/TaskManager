package com.project.tasks.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoPageDTO<T> {
    private int pageNumber;
    private int pageSize;
    private long totalRecords;
    private int totalPages;
    private boolean hasPreviousPage;
    private boolean hasNextPage;

    public InfoPageDTO(Page<T> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalRecords = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasPreviousPage = page.hasPrevious();
        this.hasNextPage = page.hasNext();
    }
}

