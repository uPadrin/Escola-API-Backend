package com.escola.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

// =====================================================
// PageResponse.java
// DTO genérico que encapsula qualquer Page<?> do Spring
// em um formato JSON limpo e legível para o frontend.
//
// Uso:  return PageResponse.de(page.map(this::toResponse));
// =====================================================

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    // Lista de itens desta página
    private List<T> content;

    // Página atual — começa em 0
    private int paginaAtual;

    // Total de páginas disponíveis
    private int totalPaginas;

    // Total de registros no banco (sem paginação)
    private long totalItens;

    // Quantos itens por página foram solicitados
    private int tamanhoPagina;

    // Atalhos de navegação
    private boolean primeira;
    private boolean ultima;

    /**
     * Converte um Page<T> do Spring em PageResponse<T>.
     * Use após um .map() para já receber o tipo correto.
     *
     * Exemplo:
     *   Page<Aluno> page = repo.findAll(pageable);
     *   return PageResponse.de(page.map(this::toResponse));
     */
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
