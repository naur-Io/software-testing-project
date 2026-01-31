package br.ufpe.cin.residencia.loja.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoSemEstoqueTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Estoque insuficiente para o item.";

        ExcecaoSemEstoque excecao = new ExcecaoSemEstoque(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    void deveSerRuntimeException() {
        ExcecaoSemEstoque excecao =
                new ExcecaoSemEstoque("Sem estoque");

        assertTrue(excecao instanceof RuntimeException);
    }
}