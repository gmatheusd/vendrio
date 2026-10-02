package com.vendrio.produto;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface  ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsBySku(String sku);

    boolean existsByCodigoBarras(String codigoBarras);

    Optional<Produto> findByCodigoBarras(String codigoBarras);

    List<Produto> findByAtivoTrueAndNomeContainingIgnoreCaseOrderByNome(String nome);

    // Trava a linha do produto até o fim da transacao (usado na baixa de estoque).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produto p where p.id = :id")
    Optional<Produto> findByIdParaAtualizar(Long id);
}
