package br.ufpe.cin.residencia.loja.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoTotalInvalidoTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Total do pedido inválido.";

        ExcecaoTotalInvalido excecao = new ExcecaoTotalInvalido(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    void deveSerRuntimeException() {
        ExcecaoTotalInvalido excecao =
                new ExcecaoTotalInvalido("Erro no total");

        assertTrue(excecao instanceof RuntimeException);
    }
}