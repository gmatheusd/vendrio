package com.vendrio.estoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    @Query("select coalesce(sum(m.quantidade), 0) from MovimentacaoEstoque m where m.produtoId = :produtoId")
    long somarPorProduto(Long produtoId);

    List<MovimentacaoEstoque> findByProdutoIdOrderByDataDesc(Long produtoId);
}
