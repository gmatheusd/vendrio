package com.vendrio.estoque;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    @GetMapping("/{produtoId}/saldo")
    public int saldo(@PathVariable Long produtoId) {
        return service.saldo(produtoId);
    }

    @GetMapping("/{produtoId}/historico")
    public List<MovimentacaoEstoque> historico(@PathVariable Long produtoId) {
        return service.historico(produtoId);
    }

    /*
      Entrada avulsa, útil para testar enquanto a Etapa 2 (entradas com fornecedor) não fica pronta.
     */
    @PostMapping("/entradas-avulsas")
    @ResponseStatus(HttpStatus.CREATED)
    public void entradaAvulsa(@RequestBody @Valid EntradaAvulsaRequest req) {
        service.registrarEntrada(req.produtoId(), req.quantidade(), "AJUSTE", null);
    }

    public record EntradaAvulsaRequest(@NotNull Long produtoId, @NotNull @Positive Integer quantidade) {
    }
}
