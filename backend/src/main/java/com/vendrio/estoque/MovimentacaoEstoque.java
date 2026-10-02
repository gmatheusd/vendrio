package com.vendrio.estoque;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Cada linha é um fato imutável: "entraram 10 unidades", "saíram 2 unidades".
 * O estoque atual de um produto é a soma das quantidades (saídas são negativas).
 */
@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "origem_tipo", length = 20)
    private String origemTipo;

    @Column(name = "origem_id")
    private Long origemId;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    protected MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(Long produtoId, TipoMovimentacao tipo, int quantidade, String origemTipo, Long origemId) {
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.origemTipo = origemTipo;
        this.origemId = origemId;
    }

    public Long getId() { return id; }
    public Long getProdutoId() { return produtoId; }
    public TipoMovimentacao getTipo() { return tipo; }
    public Integer getQuantidade() { return quantidade; }
    public String getOrigemTipo() { return origemTipo; }
    public Long getOrigemId() { return origemId; }
    public LocalDateTime getData() { return data; }

}
