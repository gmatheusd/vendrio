package com.vendrio.produto.dto;

import com.vendrio.produto.Produto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String sku,
        String codigoBarras,
        String nome,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        Integer estoqueMinimo,
        int estoqueAtual) {

    public static ProdutoResponse de(Produto p, int estoqueAtual) {
        return new ProdutoResponse(p.getId(), p.getSku(), p.getCodigoBarras(), p.getNome(), p.getPrecoCusto(), p.getPrecoVenda(), p.getEstoqueMinimo(), estoqueAtual);
    }
}
