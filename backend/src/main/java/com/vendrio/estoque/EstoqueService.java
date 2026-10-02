package com.vendrio.estoque;

import com.vendrio.produto.ProdutoRepository;
import com.vendrio.shared.RecursoNaoEncontradoException;
import com.vendrio.shared.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstoqueService {

    private final MovimentacaoEstoqueRepository movimentacoes;
    private final ProdutoRepository produtos;

    public EstoqueService(MovimentacaoEstoqueRepository movimentacoes, ProdutoRepository produtos) {
        this.movimentacoes = movimentacoes;
        this.produtos = produtos;
    }

    @Transactional(readOnly = true)
    public int saldo(Long produtoId) {
        return Math.toIntExact(movimentacoes.somarPorProduto(produtoId));
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> historico(Long produtoId) {
        return movimentacoes.findByProdutoIdOrderByDataDesc(produtoId);
    }

    // Chamado ao registrar uma entrada de mercadorias.
    @Transactional
    public void registrarEntrada(Long produtoId, int quantidade, String origemTipo, Long origemId) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("Quantidade de entrada deve ser maior que zero");
        }
        garantirProdutoExiste(produtoId);
        movimentacoes.save(new MovimentacaoEstoque(produtoId, TipoMovimentacao.ENTRADA, quantidade, origemTipo, origemId));
    }

    /**
     * Chamado pela venda. Precisa rodar DENTRO da transação da venda
     * (MANDATORY): se a venda falhar, a baixa é desfeita junto.
     * O lock no produto impede dois caixas de venderem a última unidade ao mesmo tempo.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void registrarSaida(Long produtoId, int quantidade, String origemTipo, Long origemId) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("Quantidade de saída deve ser maior que zero");
        }
        produtos.findByIdParaAtualizar(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + produtoId));

        int disponivel = saldo(produtoId);
        if (disponivel < quantidade) {
            throw new RegraNegocioException(
                    "Estoque insuficiente para o produto " + produtoId + ": disponível " + disponivel + ", pedido " + quantidade);
        }
        movimentacoes.save(new MovimentacaoEstoque(produtoId, TipoMovimentacao.SAIDA, - quantidade, origemTipo, origemId));
    }

    private void garantirProdutoExiste(Long produtoId) {
        if (!produtos.existsById(produtoId)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado: " + produtoId);
        }
    }
}
