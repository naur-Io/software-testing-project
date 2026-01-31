package br.ufpe.cin.residencia.loja.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoCupomInvalidoTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Cupom inválido.";

        ExcecaoCupomInvalido excecao = new ExcecaoCupomInvalido(mensagem);

        assertEquals(mensagem, excecao.getMessage());
    }

    @Test
    void deveSerRuntimeException() {
        ExcecaoCupomInvalido excecao =
                new ExcecaoCupomInvalido("Cupom inválido");

        assertTrue(excecao instanceof RuntimeException);
    }
}
