package br.ufpe.cin.residencia.loja.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoCupomJaUsadoTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Cupom já usado.";

        ExcecaoCupomJaUsado excecao = new ExcecaoCupomJaUsado(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    void deveSerRuntimeException() {
        ExcecaoCupomJaUsado excecao =
                new ExcecaoCupomJaUsado("Cupom já usado");

        assertTrue(excecao instanceof RuntimeException);
    }
}