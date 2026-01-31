package br.ufpe.cin.residencia.loja.catalogo;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import br.ufpe.cin.residencia.loja.checkout.ExcecaoSemEstoque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoArquivoCSVTest {

    @TempDir
    Path tempDir;

    private Path arquivo;
    private CatalogoArquivoCSV catalogo;

    @BeforeEach
    void setup() throws IOException {
        arquivo = tempDir.resolve("catalogo.csv");
        catalogo = CatalogoArquivoCSV.carregar(arquivo);
    }


    @Test
    void deveCriarArquivoPadraoSeNaoExistir() {
        assertTrue(Files.exists(arquivo));
        assertFalse(catalogo.itens().isEmpty());
    }

    @Test
    void deveObterItemPorSku() {
        Map<String, Item> itens = catalogo.itens();
        Item item = itens.values().iterator().next();
        assertEquals(item, catalogo.obterPorSku(item.getSku()));
    }

    @Test
    void deveBaixarEstoque() {
        Item item = catalogo.itens().values().iterator().next();
        int estoqueAntes = catalogo.estoquePara(item.getSku());
        catalogo.baixarEstoque(item.getSku(), 1);
        assertEquals(estoqueAntes - 1, catalogo.estoquePara(item.getSku()));
    }



    @Test
    void deveLancarExcecaoAoBaixarEstoqueInsuficiente() {
        Item item = catalogo.itens().values().iterator().next();
        int estoque = catalogo.estoquePara(item.getSku());
        ExcecaoSemEstoque ex = assertThrows(ExcecaoSemEstoque.class,
                () -> catalogo.baixarEstoque(item.getSku(), estoque + 1));
        assertTrue(ex.getMessage().contains("Sem estoque para SKU"));
    }

    @Test
    void deveLancarExcecaoParaSkuNuloOuEmBranco() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque(null, 1));
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque("   ", 1));
    }

    @Test
    void deveLancarExcecaoParaQuantidadeInvalida() {
        Item item = catalogo.itens().values().iterator().next();
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque(item.getSku(), 0));
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.reporEstoque(item.getSku(), -1));
    }

    @Test
    void deveLancarExcecaoParaSkuInexistente() {
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.baixarEstoque("SKU_INEXISTENTE", 1));
        assertThrows(ExcecaoSemEstoque.class, () -> catalogo.reporEstoque("SKU_INEXISTENTE", 1));
    }

    @Test
    void deveReporEstoque() {
        Item item = catalogo.itens().values().iterator().next();
        int estoqueAntes = catalogo.estoquePara(item.getSku());
        catalogo.reporEstoque(item.getSku(), 2);
        assertEquals(estoqueAntes + 2, catalogo.estoquePara(item.getSku()));
    }

    @Test
    void deveLancarExcecaoSeLinhaInvalidaNoArquivo() throws IOException {
        Files.writeString(arquivo, "sku;nome;preco;pesoEmGramas;estoque\nINVALID;1;2\n");
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> CatalogoArquivoCSV.carregar(arquivo));
        assertTrue(ex.getMessage().contains("Linha invalida no catalogo"));
    }

    @Test
    void deveLancarExcecaoSeSeparadorInvalido() throws IOException {
        Path arquivo = Files.createTempFile("catalogo_invalido", ".csv");
        arquivo.toFile().deleteOnExit();

        Files.writeString(arquivo, "LINHAINVALIDA\nsku;nome;preco;pesoEmGramas;estoque");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                CatalogoArquivoCSV.carregar(arquivo));

        assertTrue(ex.getMessage().contains("Separador invalido no catalogo"));
    }



    @Test
    void deveSalvarCatalogo() throws IOException {
        Path arquivoTeste = tempDir.resolve("catalogo_teste.csv");
        CatalogoArquivoCSV c = CatalogoArquivoCSV.carregar(arquivoTeste);

        String skuExistente = c.itens().keySet().iterator().next();
        c.reporEstoque(skuExistente, 5); // força salvar no CSV

        String conteudo = Files.readString(arquivoTeste);
        assertTrue(conteudo.contains(skuExistente));
    }

}
