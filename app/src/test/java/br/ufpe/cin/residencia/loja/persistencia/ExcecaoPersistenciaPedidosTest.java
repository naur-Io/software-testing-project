package br.ufpe.cin.residencia.loja.persistencia;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoPersistenciaPedidosTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        ExcecaoPersistenciaPedidos excecao =
                new ExcecaoPersistenciaPedidos("Erro ao persistir pedido");

        assertEquals("Erro ao persistir pedido", excecao.getMessage());
        assertNull(excecao.getCause());
    }

    @Test
    void deveCriarExcecaoComMensagemECausa() {
        RuntimeException causaOriginal = new RuntimeException("Falha no banco");

        ExcecaoPersistenciaPedidos excecao =
                new ExcecaoPersistenciaPedidos("Erro ao persistir pedido", causaOriginal);

        assertEquals("Erro ao persistir pedido", excecao.getMessage());
        assertEquals(causaOriginal, excecao.getCause());
    }
}
