package com.vendrio.produto;

import com.vendrio.produto.dto.ProdutoRequest;
import com.vendrio.produto.dto.ProdutoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProdutoResponse> listar(@RequestParam(required = false) String busca) {
        return service.listar(busca);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    // Usado pelo PDV quando o leitor de código de barras lê um produto.
    @GetMapping("/codigo-barras/{codigo}")
    public ProdutoResponse buscarPorCodigoBarras(@PathVariable String codigo) {
        return service.buscarPorCodigoBarras(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@RequestBody @Valid ProdutoRequest req) {
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        service.desativar(id);
    }
}
