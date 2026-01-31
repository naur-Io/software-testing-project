package br.ufpe.cin.residencia.loja.io;

import br.ufpe.cin.residencia.loja.catalogo.Catalogo;
import br.ufpe.cin.residencia.loja.dominio.Carrinho;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


public class ImportadorCarrinhoTest {


    @TempDir
    Path tempDir;

    // ====== Fake de Catalogo ======
    static class CatalogoFake implements Catalogo {

        private final Item item;

        CatalogoFake(Item item) {
            this.item = item;
        }

        @Override
        public Item obterPorSku(String sku) {
            if (item != null && item.getSku().equals(sku)) {
                return item;
            }
            return null;
        }

        @Override
        public int estoquePara(String sku) {
            return 0;
        }

        @Override
        public void baixarEstoque(String sku, int quantidade) {

        }

        @Override
        public void reporEstoque(String sku, int quantidade) {

        }
    }

    // ====== Testes ======

    @Test
    void arquivoNuloLancaExcecao() {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        ExcecaoImportacaoCarrinho ex = assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(null, new CatalogoFake(null))
        );

        assertTrue(ex.getMessage().contains("Arquivo de importação"));
    }

    @Test
    void catalogoNuloLancaExcecao() {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        ExcecaoImportacaoCarrinho ex = assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(Path.of("qualquer.csv"), null)
        );

        assertTrue(ex.getMessage().contains("Catálogo"));
    }

    @Test
    void arquivoInexistenteLancaExcecao() {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Path arquivo = tempDir.resolve("nao-existe.csv");

        ExcecaoImportacaoCarrinho ex = assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(arquivo, new CatalogoFake(null))
        );

        assertTrue(ex.getMessage().contains("Arquivo não encontrado"));
    }

    @Test
    void arquivoApenasComCabecalhoLancaExcecao() throws Exception {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Path arquivo = tempDir.resolve("carrinho.csv");
        Files.writeString(arquivo, "SKU;QTD\n");

        ExcecaoImportacaoCarrinho ex = assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(arquivo, new CatalogoFake(null))
        );

        assertTrue(ex.getMessage().contains("Carrinho importado está vazio"));
    }

    @Test
    void linhaInvalidaSemSeparadorLancaExcecao() throws Exception {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Path arquivo = tempDir.resolve("carrinho.csv");
        Files.writeString(arquivo,
                "SKU;QTD\n" +
                        "ABC123\n"
        );

        assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(arquivo, new CatalogoFake(null))
        );
    }

    @Test
    void quantidadeNaoNumericaLancaExcecao() throws Exception {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Path arquivo = tempDir.resolve("carrinho.csv");
        Files.writeString(arquivo,
                "SKU;QTD\n" +
                        "ABC;xyz\n"
        );

        assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(arquivo, new CatalogoFake(null))
        );
    }

    @Test
    void quantidadeZeroOuNegativaLancaExcecao() throws Exception {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Path arquivo = tempDir.resolve("carrinho.csv");
        Files.writeString(arquivo,
                "SKU;QTD\n" +
                        "ABC;0\n"
        );

        assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(arquivo, new CatalogoFake(null))
        );
    }

    @Test
    void skuInexistenteNoCatalogoLancaExcecao() throws Exception {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Path arquivo = tempDir.resolve("carrinho.csv");
        Files.writeString(arquivo,
                "SKU;QTD\n" +
                        "ABC;2\n"
        );

        assertThrows(
                ExcecaoImportacaoCarrinho.class,
                () -> importador.importar(arquivo, new CatalogoFake(null))
        );
    }

    @Test
    void importaCarrinhoComSucesso() throws Exception {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Item item = new Item(
                "ABC",
                "Produto Teste",
                Dinheiro.of("10.00"),
                100
        );

        Catalogo catalogo = new CatalogoFake(item);

        Path arquivo = tempDir.resolve("carrinho.csv");
        Files.writeString(arquivo,
                "SKU;QTD\n" +
                        "ABC;2\n"
        );

        Carrinho carrinho = importador.importar(arquivo, catalogo);

        assertFalse(carrinho.vazio());
        assertEquals(1, carrinho.linhas().size());
        assertEquals(2, carrinho.linhas().get(0).getQuantidade());
    }

    @Test
    void somaQuantidadesDeSkusRepetidos() throws Exception {
        ImportadorCarrinho importador = new ImportadorCarrinho();

        Item item = new Item(
                "ABC",
                "Produto Teste",
                Dinheiro.of("10.00"),
                100
        );

        Catalogo catalogo = new CatalogoFake(item);

        Path arquivo = tempDir.resolve("carrinho.csv");
        Files.writeString(arquivo,
                "SKU;QTD\n" +
                        "ABC;1\n" +
                        "ABC;3\n"
        );

        Carrinho carrinho = importador.importar(arquivo, catalogo);

        assertEquals(1, carrinho.linhas().size());
        assertEquals(4, carrinho.linhas().get(0).getQuantidade());
    }



}


