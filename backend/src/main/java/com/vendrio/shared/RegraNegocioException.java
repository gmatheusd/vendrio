package com.vendrio.shared;

/** Erro de regra de negócio, ex.: estoque insuficiente, SKU duplicado. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
