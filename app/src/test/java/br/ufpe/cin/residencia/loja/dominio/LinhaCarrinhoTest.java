package br.ufpe.cin.residencia.loja.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinhaCarrinhoTest {

    @Test
    void totalDaLinhaEhPrecoVezesQuantidade() {
        Item item = new Item(
                "A",
                "Produto",
                Dinheiro.of("10.00"),
                100
        );

        LinhaCarrinho linha = new LinhaCarrinho(item, 3);

        assertEquals(
                Dinheiro.of("30.00"),
                linha.totalDaLinha()
        );
    }

    @Test
    void pesoDaLinhaEhPesoVezesQuantidade() {
        Item item = new Item(
                "B",
                "Produto Pesado",
                Dinheiro.of("5.00"),
                250
        );

        LinhaCarrinho linha = new LinhaCarrinho(item, 4);

        assertEquals(1000, linha.pesoDaLinhaEmGramas());
    }
}
