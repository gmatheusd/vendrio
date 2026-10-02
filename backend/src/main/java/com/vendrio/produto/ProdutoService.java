package com.vendrio.produto;

import com.vendrio.estoque.EstoqueService;
import com.vendrio.produto.dto.ProdutoRequest;
import com.vendrio.produto.dto.ProdutoResponse;
import com.vendrio.shared.RecursoNaoEncontradoException;
import com.vendrio.shared.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final EstoqueService estoqueService;

    public ProdutoService(ProdutoRepository repository, EstoqueService estoqueService) {
        this.repository = repository;
        this.estoqueService = estoqueService;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar(String busca) {
        return repository.findByAtivoTrueAndNomeContainingIgnoreCaseOrderByNome(busca == null ? "" : busca)
                .stream()
                .map(p -> ProdutoResponse.de(p, estoqueService.saldo(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        Produto p = buscarEntidade(id);
        return ProdutoResponse.de(p, estoqueService.saldo(id));
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorCodigoBarras(String codigo) {
        Produto p = repository.findByCodigoBarras(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Código de barras não cadastrado: " + codigo));
        return ProdutoResponse.de(p, estoqueService.saldo(p.getId()));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest req) {
        if (repository.existsBySku(req.sku())) {
            throw new RegraNegocioException("Já existe um produto com o SKU " + req.sku());
        }
        if (req.codigoBarras() != null && repository.existsByCodigoBarras(req.codigoBarras())) {
            throw new RegraNegocioException("Já existe um produto com o código de barras " + req.codigoBarras());
        }
        Produto p = new Produto();
        aplicar(p, req);
        return ProdutoResponse.de(repository.save(p), 0);
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
        Produto p = buscarEntidade(id);
        aplicar(p, req);
        return ProdutoResponse.de(p, estoqueService.saldo(id));
    }

    // Não apaga do banco: produtos com histórico de vendas precisam continuar existindo.
    @Transactional
    public void desativar(Long id) {
        buscarEntidade(id).setAtivo(false);
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    private void aplicar(Produto p, ProdutoRequest req) {
        p.setSku(req.sku());
        p.setCodigoBarras(req.codigoBarras());
        p.setNome(req.nome());
        p.setPrecoCusto(req.precoCusto());
        p.setPrecoVenda(req.precoVenda());
        p.setEstoqueMinimo(req.estoqueMinimo());
    }

}
