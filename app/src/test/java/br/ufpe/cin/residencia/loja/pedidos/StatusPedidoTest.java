package br.ufpe.cin.residencia.loja.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatusPedidoTest {

    @Test
    void deveConterOsDoisStatusEsperados() {
        StatusPedido[] valores = StatusPedido.values();

        assertEquals(2, valores.length);
        assertEquals(StatusPedido.PAGO, valores[0]);
        assertEquals(StatusPedido.REEMBOLSADO, valores[1]);
    }

    @Test
    void valueOfDeveRetornarPago() {
        StatusPedido status = StatusPedido.valueOf("PAGO");

        assertEquals(StatusPedido.PAGO, status);
    }

    @Test
    void valueOfDeveRetornarReembolsado() {
        StatusPedido status = StatusPedido.valueOf("REEMBOLSADO");

        assertEquals(StatusPedido.REEMBOLSADO, status);
    }

    @Test
    void valueOfComValorInvalidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> StatusPedido.valueOf("CANCELADO"));
    }

    @Test
    void ordemDosEnumsDeveSerConsistente() {
        assertTrue(StatusPedido.PAGO.ordinal() < StatusPedido.REEMBOLSADO.ordinal());
    }
}
