package br.ufpe.cin.residencia.loja.descontos;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CupomTest {

    @Test
    void gettersRetornamValoresCorretos() {
        CodigoCupom codigo = CodigoCupom.DESCONTO5;
        LocalDate validade = LocalDate.now().plusDays(10);

        Cupom cupom = new Cupom(codigo, validade, true, false);

        assertEquals(codigo, cupom.getCodigo());
        assertEquals(validade, cupom.getValidoAte());
        assertTrue(cupom.isUsoUnico());
        assertFalse(cupom.isUsado());
    }

    @Test
    void marcarComoUsadoAtualizaEstado() {
        Cupom cupom = new Cupom(
                CodigoCupom.FRETE_GRATIS,
                LocalDate.now().plusDays(1),
                true,
                false
        );

        cupom.marcarComoUsado();

        assertTrue(cupom.isUsado());
    }
}
