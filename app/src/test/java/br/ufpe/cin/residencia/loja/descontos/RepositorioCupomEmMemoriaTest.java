package br.ufpe.cin.residencia.loja.descontos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioCupomEmMemoriaTest {

    @Test
    void obterRetornaCupomExistente() {
        RepositorioCupomEmMemoria repo = new RepositorioCupomEmMemoria();

        Cupom cupom = repo.obter(CodigoCupom.DESCONTO5);

        assertNotNull(cupom);
        assertEquals(CodigoCupom.DESCONTO5, cupom.getCodigo());
    }

    @Test
    void obterCupomInexistenteRetornaNull() {
        RepositorioCupomEmMemoria repo = new RepositorioCupomEmMemoria();

        assertNull(repo.obter(null));
    }

    @Test
    void marcarComoUsadoSoFuncionaParaUsoUnico() {
        RepositorioCupomEmMemoria repo = new RepositorioCupomEmMemoria();

        Cupom cupom = repo.obter(CodigoCupom.FRETE_GRATIS);
        assertFalse(cupom.isUsado());

        repo.marcarComoUsado(CodigoCupom.FRETE_GRATIS);

        assertTrue(cupom.isUsado());
    }

    @Test
    void marcarComoUsadoNaoAfetaCupomNaoUnico() {
        RepositorioCupomEmMemoria repo = new RepositorioCupomEmMemoria();

        Cupom cupom = repo.obter(CodigoCupom.DESCONTO5);

        repo.marcarComoUsado(CodigoCupom.DESCONTO5);

        assertFalse(cupom.isUsado());
    }
}
