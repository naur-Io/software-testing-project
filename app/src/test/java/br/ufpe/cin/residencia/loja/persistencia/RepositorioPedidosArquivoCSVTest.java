package br.ufpe.cin.residencia.loja.persistencia;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.pedidos.ExcecaoPedidoNaoEncontrado;
import br.ufpe.cin.residencia.loja.pedidos.ItemPedido;
import br.ufpe.cin.residencia.loja.pedidos.Pedido;
import br.ufpe.cin.residencia.loja.pedidos.StatusPedido;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;
import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioPedidosArquivoCSVTest {


    @TempDir
    Path tempDir;

    @Test
    void deveAtivarConstrutorDefault() {
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV();
        assertNotNull(repo);
    }



    @Test
    void deveIgnorarLinhasVaziasECabecalho() throws IOException {
        Path arquivo = tempDir.resolve("pedidos.csv");
        Files.writeString(arquivo, "id;criadoEm;reembolsadoEm;status;metodoFrete;metodoPagamento;codigoCupom;subtotal;descontoCupom;descontoPagamento;imposto;frete;total;itens\n\n");

        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV(arquivo);
        assertTrue(repo.listarTodos().isEmpty());
    }

    @Test
    void deveCobrirCatchDoParsePedido() throws IOException {
        Path arquivo = tempDir.resolve("pedidos.csv");

        // Linha CSV com STATUS inválido para forçar exceção
        String linhaInvalida = "1;2026-01-31T10:00:00;;INVALID_STATUS;PADRAO;CARTAO;;10;0;0;0;0;0;SKU1,Item,10.00,100,1";

        Files.writeString(arquivo, linhaInvalida + "\n");

        // A exceção esperada é ExcecaoPersistenciaPedidos, pois o try vai lançar RuntimeException
        assertThrows(ExcecaoPersistenciaPedidos.class, () -> {
            new RepositorioPedidosArquivoCSV(arquivo);
        });
    }

    @Test
    void deveLancarExcecaoSeTextoVazio() throws Exception {
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV();

        Method parseItens = RepositorioPedidosArquivoCSV.class.getDeclaredMethod("parseItens", String.class);
        parseItens.setAccessible(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () ->
                parseItens.invoke(repo, "")
        );
        // Aqui pegamos a exceção real lançada dentro do método
        assertTrue(ex.getCause() instanceof ExcecaoPersistenciaPedidos);
        assertEquals("Pedido sem itens não é válido.", ex.getCause().getMessage());
    }

    @Test
    void deveRetornarPedidoExistente() {
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV();
        // Cria um pedido fake
        Pedido pedido = new Pedido(
                "123",
                LocalDateTime.now(),
                null,
                List.of(new ItemPedido("SKU1", "Item1", Dinheiro.of("10.00"), 100, 1)),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        );

        repo.salvar(pedido);

        Pedido obtido = repo.obterPorId("123");
        assertNotNull(obtido);
        assertEquals("123", obtido.getId());
    }

    @Test
    void deveAtualizarPedidoExistente() {
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV();

        Pedido pedido = new Pedido(
                "123",
                LocalDateTime.now(),
                null,
                List.of(new ItemPedido("SKU1", "Item1", Dinheiro.of("10.00"), 100, 1)),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        );

        repo.salvar(pedido);

        // altera algo no pedido
        Pedido pedidoAtualizado = new Pedido(
                "123",
                pedido.getCriadoEm(),
                pedido.getReembolsadoEm(),
                List.of(new ItemPedido("SKU1", "Item1", Dinheiro.of("20.00"), 100, 1)), // preço alterado
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("20.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("20.00"),
                StatusPedido.PAGO
        );

        repo.atualizar(pedidoAtualizado);

        Pedido obtido = repo.obterPorId("123");
        assertEquals(Dinheiro.of("20.00"), obtido.getSubtotal());
    }

    @Test
    void deveLancarExcecaoAoCriarArquivoVazioComDiretorioComoArquivo() throws Exception {
        Path tempDir = Files.createTempDirectory("tempDir");

        assertThrows(
                ExcecaoPersistenciaPedidos.class,
                () -> new RepositorioPedidosArquivoCSV(tempDir)
        );
    }



    @Test
    void deveCobrirCatchDeCriarArquivoVazio() throws Exception {
        Path tempDir = Files.createTempDirectory("repositorio_test");
        Path caminhoArquivoComoDir = tempDir.resolve("pedidos.csv");

        Files.createDirectory(caminhoArquivoComoDir);

        assertThrows(
                ExcecaoPersistenciaPedidos.class,
                () -> new RepositorioPedidosArquivoCSV(caminhoArquivoComoDir)
        );
    }

    @Test
    void deveLancarExcecaoAoCriarArquivoVazio() throws Exception {
        Path tempDirComoArquivo = Files.createTempDirectory("arquivoComoDir");

        assertThrows(
                ExcecaoPersistenciaPedidos.class,
                () -> new RepositorioPedidosArquivoCSV(tempDirComoArquivo)
        );
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNulo() {
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> repo.atualizar(null)
        );
        assertEquals("Pedido não pode ser nulo.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExiste() {
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV();

        ExcecaoPedidoNaoEncontrado ex = assertThrows(
                ExcecaoPedidoNaoEncontrado.class,
                () -> repo.obterPorId("inexistente")
        );
        assertEquals("Pedido não encontrado: inexistente", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSeItemInvalido() throws Exception {
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV();

        Method parseItens = RepositorioPedidosArquivoCSV.class.getDeclaredMethod("parseItens", String.class);
        parseItens.setAccessible(true);

        String itemInvalido = "SKU1,Item,10.00"; // só 3 campos

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () ->
                parseItens.invoke(repo, itemInvalido)
        );
        assertTrue(ex.getCause() instanceof ExcecaoPersistenciaPedidos);
        assertTrue(ex.getCause().getMessage().contains("Item inválido em pedido"));
    }



    @Test
    void deveFalharAoSalvarPedidoComErro() throws IOException {
        Path arquivo = tempDir.resolve("pedidos.csv");
        RepositorioPedidosArquivoCSV repo = new RepositorioPedidosArquivoCSV(arquivo);

        Pedido pedido = pedidoValido();
        // Tornar arquivo somente leitura para disparar IOException
        arquivo.toFile().setReadOnly();
        assertThrows(Exception.class, () -> repo.salvar(pedido));
    }

    @Test
    void deveFalharParsePedidoComCamposInvalidos() throws IOException {
        Path arquivo = tempDir.resolve("pedidos.csv");
        Files.writeString(arquivo, "invalido;linha;com;menos;campos\n");

        assertThrows(ExcecaoPersistenciaPedidos.class,
                () -> new RepositorioPedidosArquivoCSV(arquivo));
    }

    @Test
    void deveFalharParseEnumsEDinheiro() throws IOException {
        Path arquivo = tempDir.resolve("pedidos.csv");
        String linha = "1;2026-01-31T10:00:00;;INVALID_STATUS;PADRAO;CARTAO;;10;0;0;0;0;0;sku,nome,10.00,100,1";
        Files.writeString(arquivo, linha + "\n");

        assertThrows(ExcecaoPersistenciaPedidos.class,
                () -> new RepositorioPedidosArquivoCSV(arquivo));
    }

    @Test
    void deveFalharParseItensEmPedido() throws IOException {
        Path arquivo = tempDir.resolve("pedidos.csv");
        String linha = "1;2026-01-31T10:00:00;;;PADRAO;CARTAO;;10;0;0;0;0;0;"; // sem itens
        Files.writeString(arquivo, linha + "\n");

        assertThrows(ExcecaoPersistenciaPedidos.class,
                () -> new RepositorioPedidosArquivoCSV(arquivo));
    }

    private Pedido pedidoValido() {
        ItemPedido item = new ItemPedido("SKU1", "Item 1", Dinheiro.of("10.00"), 100, 1);
        return new Pedido(
                "1",
                LocalDateTime.now(),
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                CodigoCupom.DESCONTO5,
                Dinheiro.of("10.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("20.00"),
                Dinheiro.of("30.00"),
                StatusPedido.PAGO
        );
    }
}
