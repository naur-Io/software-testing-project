package br.ufpe.cin.residencia.loja.catalogo;

import br.ufpe.cin.residencia.loja.checkout.ExcecaoSemEstoque;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoEmMemoriaTest {

    private CatalogoEmMemoria catalogo;
    private Item itemTeclado;

    @BeforeEach
    void setUp() {
        catalogo = new CatalogoEmMemoria();
        itemTeclado = new Item("KB", "Teclado", Dinheiro.of("120.00"), 800);
        catalogo.addItem(itemTeclado, 10);
    }

    @Test
    void deveBaixarEstoqueDoMapa() throws Exception {
        Path tempArquivo = Files.createTempFile("catalogo", ".csv");
        tempArquivo.toFile().deleteOnExit();

        String conteudo = "sku;nome;preco;pesoEmGramas;estoque\n" +
                "KB;Teclado;120.00;800;10\n" +
                "MS;Mouse;80.00;200;5";
        Files.writeString(tempArquivo, conteudo);

        CatalogoArquivoCSV catalogo = CatalogoArquivoCSV.carregar(tempArquivo);

        Map<String, Integer> venda = Map.of(
                "KB", 3,
                "MS", 2
        );

        catalogo.baixarEstoque(venda);

        assertEquals(7, catalogo.estoquePara("KB"));
        assertEquals(3, catalogo.estoquePara("MS"));
    }


    @Test
    void deveLancarExcecaoSeSkuInvalidoNoMapa() throws Exception {
        Path tempArquivo = Files.createTempFile("catalogo", ".csv");
        tempArquivo.toFile().deleteOnExit();

        String conteudo = "sku;nome;preco;pesoEmGramas;estoque\n" +
                "KB;Teclado;120.00;800;10\n" +
                "MS;Mouse;80.00;200;5";

        Files.writeString(tempArquivo, conteudo);

        CatalogoArquivoCSV catalogo = CatalogoArquivoCSV.carregar(tempArquivo);

        Map<String, Integer> venda = Map.of("INVALIDO", 1);

        ExcecaoSemEstoque e = assertThrows(ExcecaoSemEstoque.class,
                () -> catalogo.baixarEstoque(venda));
        assertTrue(e.getMessage().contains("SKU inexistente no catálogo"));
    }

    @Test
    void deveAdicionarItemAoCatalogo() {
        assertEquals(itemTeclado, catalogo.obterPorSku("KB"));
        assertEquals(10, catalogo.estoquePara("KB"));
    }

    @Test
    void deveRetornarZeroParaSkuInexistente() {
        assertNull(catalogo.obterPorSku("INEXISTENTE"));
        assertEquals(0, catalogo.estoquePara("INEXISTENTE"));
    }

    @Test
    void deveBaixarEstoqueCorretamente() {
        catalogo.baixarEstoque("KB", 5);
        assertEquals(5, catalogo.estoquePara("KB"));
    }

    @Test
    void baixarEstoqueComQuantidadeMaiorQueDisponivelDeveLancar() {
        ExcecaoSemEstoque ex = assertThrows(ExcecaoSemEstoque.class,
                () -> catalogo.baixarEstoque("KB", 15));
        assertTrue(ex.getMessage().contains("Sem estoque"));
    }

    @Test
    void baixarEstoqueComSkuNuloOuVazioDeveLancar() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque(null, 1));
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque("   ", 1));
    }

    @Test
    void baixarEstoqueComQuantidadeInvalidaDeveLancar() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque("KB", 0));
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque("KB", -5));
    }

    @Test
    void baixarEstoqueParaSkuInexistenteDeveLancar() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque("XYZ", 1));
    }

    @Test
    void reporEstoqueCorretamente() {
        catalogo.baixarEstoque("KB", 3);
        catalogo.reporEstoque("KB", 5);
        assertEquals(12, catalogo.estoquePara("KB"));
    }

    @Test
    void reporEstoqueComSkuNuloOuVazioDeveLancar() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.reporEstoque(null, 1));
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.reporEstoque("   ", 1));
    }

    @Test
    void reporEstoqueComQuantidadeInvalidaDeveLancar() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.reporEstoque("KB", 0));
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.reporEstoque("KB", -5));
    }

    @Test
    void reporEstoqueParaSkuInexistenteDeveLancar() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.reporEstoque("XYZ", 1));
    }

    @Test
    void deveRetornarMapaNaoModificavel() {
        Map<String, Item> itens = catalogo.itens();
        Map<String, Integer> estoque = catalogo.estoque();
        assertThrows(UnsupportedOperationException.class, () -> itens.put("X", itemTeclado));
        assertThrows(UnsupportedOperationException.class, () -> estoque.put("X", 1));
    }

    @Test
    void catalogoPadraoDeveTerItens() {
        CatalogoEmMemoria padrao = CatalogoEmMemoria.catalogoPadrao();
        assertEquals(5, padrao.itens().size());
        assertEquals(10, padrao.estoquePara("KB"));
        assertEquals(2, padrao.estoquePara("MN"));
    }
}

