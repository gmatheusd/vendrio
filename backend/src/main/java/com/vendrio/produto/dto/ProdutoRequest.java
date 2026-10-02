package com.vendrio.produto.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank @Size(max = 40) String sku,
    @Size(max = 20) String codigoBarras,
    @NotBlank @Size(max = 160) String nome,
    @NotNull @PositiveOrZero BigDecimal precoCusto,
    @NotNull @Positive BigDecimal precoVenda,
    @NotNull @PositiveOrZero Integer estoqueMinimo) {
}
