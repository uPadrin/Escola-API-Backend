package com.escola.api.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    public static final int MAX_SIZE = 100;

    public static final int DEFAULT_SIZE = 10;

    private PageableUtils() {}

    /**
     * Monta um Pageable seguro a partir dos query params da request.
     *
     * @param page      número da página (começa em 0)
     * @param size      itens por página (limitado a MAX_SIZE)
     * @param sort      campo para ordenação (ex: "nome", "criadoEm")
     * @param direction "asc" ou "desc"
     */
    public static Pageable of(int page, int size, String sort, String direction) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_SIZE);

        Sort.Direction dir = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(safePage, safeSize, Sort.by(dir, sort));
    }
}
