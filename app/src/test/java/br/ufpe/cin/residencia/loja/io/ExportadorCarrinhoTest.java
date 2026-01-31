package br.ufpe.cin.residencia.loja.io;

import br.ufpe.cin.residencia.loja.dominio.Carrinho;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExportadorCarrinhoTest {


    @Test
    void carrinhoNuloLancaExcecao() {
        ExportadorCarrinho exportador = new ExportadorCarrinho();

        assertThrows(
                IllegalArgumentException.class,
                () -> exportador.exportar(null, Path.of("carrinho.csv"))
        );
    }

    @Test
    void carrinhoVazioLancaExcecao() {
        ExportadorCarrinho exportador = new ExportadorCarrinho();

        Carrinho carrinho = new Carrinho();

        assertThrows(
                IllegalArgumentException.class,
                () -> exportador.exportar(carrinho, Path.of("carrinho.csv"))
        );
    }

    @Test
    void arquivoNuloLancaExcecao() {
        ExportadorCarrinho exportador = new ExportadorCarrinho();

        Carrinho carrinho = new Carrinho();
        Item item = new Item("A", "Produto", Dinheiro.of("10.00"), 100);
        carrinho.adicionar(item, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> exportador.exportar(carrinho, null)
        );
    }

    @TempDir
    Path tempDir;

    @Test
    void exportaCarrinhoComSucesso() throws Exception {
        ExportadorCarrinho exportador = new ExportadorCarrinho();

        Carrinho carrinho = new Carrinho();
        Item item = new Item("A", "Produto", Dinheiro.of("10.00"), 100);
        carrinho.adicionar(item, 2);

        Path arquivo = tempDir.resolve("carrinho.csv");

        exportador.exportar(carrinho, arquivo);

        String conteudo = Files.readString(arquivo);

        assertEquals(
                "SKU;QTD" + System.lineSeparator() +
                        "A;2" + System.lineSeparator(),
                conteudo
        );
    }


    @Test
    void exportaSomandoQuantidadesDoMesmoSku() throws Exception {
        ExportadorCarrinho exportador = new ExportadorCarrinho();

        Carrinho carrinho = new Carrinho();
        Item item = new Item("A", "Produto", Dinheiro.of("10.00"), 100);

        carrinho.adicionar(item, 1);
        carrinho.adicionar(item, 3);

        Path arquivo = tempDir.resolve("saida/carrinho.csv");

        exportador.exportar(carrinho, arquivo);

        String conteudo = Files.readString(arquivo);

        assertTrue(conteudo.contains("A;4"));
    }





}
