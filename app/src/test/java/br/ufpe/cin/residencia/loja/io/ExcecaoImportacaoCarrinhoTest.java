package br.ufpe.cin.residencia.loja.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExcecaoImportacaoCarrinhoTest {

    @Test
    void construtorComMensagemDefineMensagemCorretamente(){
        ExcecaoImportacaoCarrinho execao = new ExcecaoImportacaoCarrinho("Mensagem de erro");
        assertEquals("Mensagem de erro", execao.getMessage());

    }

    @Test
    void construtorComMensagemECausaDefineCorretamente(){
        RuntimeException causa = new RuntimeException("Causa do erro");
        ExcecaoImportacaoCarrinho execao = new ExcecaoImportacaoCarrinho("Mensagem de erro", causa);
        assertEquals("Mensagem de erro", execao.getMessage());
        assertEquals(causa, execao.getCause());
    }
}
