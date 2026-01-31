package br.ufpe.cin.residencia.loja.pedidos;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ExcecaoPedidoNaoEncontradoTest {

    @Test
    void construtorDefineMensagemCorretamente() {
        ExcecaoPedidoNaoEncontrado excecao =
                new ExcecaoPedidoNaoEncontrado("Pedido não encontrado");

        assertEquals("Pedido não encontrado", excecao.getMessage());
        assertNull(excecao.getCause());
    }
}
