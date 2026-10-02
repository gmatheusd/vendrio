package com.vendrio.estoque;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.vendrio.shared.RegraNegocioException;
import org.junit.jupiter.api.Test;

import com.vendrio.produto.Produto;
import com.vendrio.produto.ProdutoRepository;

class EstoqueServiceTest {

    private final MovimentacaoEstoqueRepository movimentacoes = mock(MovimentacaoEstoqueRepository.class);
    private final ProdutoRepository produtos = mock(ProdutoRepository.class);
    private final EstoqueService service = new EstoqueService(movimentacoes, produtos);

    @Test
    void naoPermiteVenderMaisDoQueOEstoque() {
        when(produtos.findByIdParaAtualizar(1L)).thenReturn(Optional.of(new Produto()));
        when(movimentacoes.somarPorProduto(1L)).thenReturn(2L);

        assertThatThrownBy(() -> service.registrarSaida(1L, 3, "VENDA", 10L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Estoque insuficiente");

        verify(movimentacoes, never()).save(any());
    }

    @Test
    void registraSaidaComQuantidadeNegativa() {
        when(produtos.findByIdParaAtualizar(1L)).thenReturn(Optional.of(new Produto()));
        when(movimentacoes.somarPorProduto(1L)).thenReturn(5L);

        service.registrarSaida(1L, 3, "VENDA", 10L);

        verify(movimentacoes).save(argThat(m -> m.getQuantidade() == -3 && m.getTipo() == TipoMovimentacao.SAIDA));
    }


}
