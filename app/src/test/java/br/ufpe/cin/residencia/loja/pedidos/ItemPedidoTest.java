package br.ufpe.cin.residencia.loja.pedidos;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemPedidoTest {

    @Test
    void criaItemPedidoComDadosValidos() {
        ItemPedido item = new ItemPedido(
                "KB",
                "Keyboard",
                Dinheiro.of("100.00"),
                500,
                2
        );

        assertEquals("KB", item.getSku());
        assertEquals("Keyboard", item.getNome());
        assertEquals(Dinheiro.of("100.00"), item.getPrecoUnitario());
        assertEquals(500, item.getPesoEmGramas());
        assertEquals(2, item.getQuantidade());
    }

    @Test
    void skuNuloLanceException(){
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(
                null,
                "Keyboard",
                Dinheiro.of("100.00"),
                500,
                2
        ));
    }

    @Test
    void skuEmBrancoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new ItemPedido(
                        "   ",
                        "Produto",
                        Dinheiro.of("10.00"),
                        100,
                        1
                )
        );
    }

    @Test
    void nomeNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new ItemPedido(
                        "SKU",
                        null,
                        Dinheiro.of("10.00"),
                        100,
                        1
                )
        );
    }

    @Test
    void nomeEmBrancoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new ItemPedido(
                        "SKU",
                        "   ",
                        Dinheiro.of("10.00"),
                        100,
                        1
                )
        );
    }

    @Test
    void precoUnitarioNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new ItemPedido(
                        "SKU",
                        "Produto",
                        null,
                        100,
                        1
                )
        );
    }

    @Test
    void quantidadeZeroOuNegativaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new ItemPedido(
                        "SKU",
                        "Produto",
                        Dinheiro.of("10.00"),
                        100,
                        0
                )
        );

        assertThrows(IllegalArgumentException.class, () ->
                new ItemPedido(
                        "SKU",
                        "Produto",
                        Dinheiro.of("10.00"),
                        100,
                        -1
                )
        );
    }

    @Test
    void totalCalculaPrecoVezesQuantidade() {
        ItemPedido item = new ItemPedido(
                "MS",
                "Monitor",
                Dinheiro.of("800.00"),
                3000,
                3
        );

        assertEquals(Dinheiro.of("2400.00"), item.total());
    }

    @Test
    void pesoTotalCalculaPesoVezesQuantidade() {
        ItemPedido item = new ItemPedido(
                "MS",
                "Monitor",
                Dinheiro.of("800.00"),
                3000,
                3
        );

        assertEquals(9000, item.pesoTotalEmGramas());
    }
}
