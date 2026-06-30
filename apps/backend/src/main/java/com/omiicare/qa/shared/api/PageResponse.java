package com.omiicare.qa.shared.api;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Serialization-friendly pagination envelope. Decouples the API contract from
 * Spring Data's {@link Page} implementation so the wire format is stable.
 *
 * @param <T> the element type
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
