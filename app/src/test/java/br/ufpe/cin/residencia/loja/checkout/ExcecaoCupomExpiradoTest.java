package br.ufpe.cin.residencia.loja.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoCupomExpiradoTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Cupom expirado: PORCENTO10";

        ExcecaoCupomExpirado excecao = new ExcecaoCupomExpirado(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    void deveSerRuntimeException() {
        ExcecaoCupomExpirado excecao =
                new ExcecaoCupomExpirado("Cupom expirado");

        assertTrue(excecao instanceof RuntimeException);
    }
}