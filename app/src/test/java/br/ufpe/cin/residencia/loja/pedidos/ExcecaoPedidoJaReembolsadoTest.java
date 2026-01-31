package br.ufpe.cin.residencia.loja.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcecaoPedidoJaReembolsadoTest {

    @Test
    void construtorDefineMensagemCorretamente() {
        ExcecaoPedidoJaReembolsado excecao =
                new ExcecaoPedidoJaReembolsado("Pedido já foi reembolsado");

        assertEquals("Pedido já foi reembolsado", excecao.getMessage());
    }
}
