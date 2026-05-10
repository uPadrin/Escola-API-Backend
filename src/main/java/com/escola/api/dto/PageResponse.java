package com.escola.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;

    private int paginaAtual;

    private int totalPaginas;

    private long totalItens;

    private int tamanhoPagina;

    private boolean primeira;
    private boolean ultima;

    public static <T> PageResponse<T> de(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .paginaAtual(page.getNumber())
                .totalPaginas(page.getTotalPages())
                .totalItens(page.getTotalElements())
                .tamanhoPagina(page.getSize())
                .primeira(page.isFirst())
                .ultima(page.isLast())
                .build();
    }
}
