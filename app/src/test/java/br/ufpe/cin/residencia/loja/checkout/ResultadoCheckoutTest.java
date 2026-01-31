package br.ufpe.cin.residencia.loja.checkout;

import br.ufpe.cin.residencia.loja.precificacao.Recibo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultadoCheckoutTest {

    @Test
    void deveCriarResultadoCheckoutComDadosValidos() {
        Recibo recibo = new Recibo(null, null, null, null, null, null);
        String idPedido = "PED-123";

        ResultadoCheckout resultado = new ResultadoCheckout(recibo, idPedido);

        assertEquals(recibo, resultado.getRecibo());
        assertEquals(idPedido, resultado.getIdPedido());
    }

    @Test
    void naoDevePermitirReciboNulo() {
        IllegalArgumentException excecao =
                assertThrows(IllegalArgumentException.class,
                        () -> new ResultadoCheckout(null, "PED-1"));

        assertEquals("Recibo e id do pedido são obrigatórios.", excecao.getMessage());
    }

    @Test
    void naoDevePermitirIdPedidoNulo() {
        Recibo recibo = new Recibo(null, null, null, null, null, null);

        IllegalArgumentException excecao =
                assertThrows(IllegalArgumentException.class,
                        () -> new ResultadoCheckout(recibo, null));

        assertEquals("Recibo e id do pedido são obrigatórios.", excecao.getMessage());
    }

    @Test
    void naoDevePermitirIdPedidoEmBranco() {
        Recibo recibo = new Recibo(null, null, null, null, null, null);

        IllegalArgumentException excecao =
                assertThrows(IllegalArgumentException.class,
                        () -> new ResultadoCheckout(recibo, "   "));

        assertEquals("Recibo e id do pedido são obrigatórios.", excecao.getMessage());
    }
}