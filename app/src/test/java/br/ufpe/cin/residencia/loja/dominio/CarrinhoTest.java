package br.ufpe.cin.residencia.loja.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarrinhoTest {

    private Carrinho carrinho;
    private Item itemLeve;
    private Item itemPesado;

    @BeforeEach
    void setup() {
        carrinho = new Carrinho();

        itemLeve = new Item(
                "KB",
                "Keyboard",
                Dinheiro.of("100.00"),
                500
        );

        itemPesado = new Item(
                "MS",
                "Monitor",
                Dinheiro.of("800.00"),
                3000
        );
    }

    @Test
    void carrinhoNovoEhVazio() {
        Carrinho carrinho = new Carrinho();

        assertTrue(carrinho.vazio());
    }

    @Test
    void adicionarItemNuloLancaExcecao() {
        Carrinho carrinho = new Carrinho();

        assertThrows(IllegalArgumentException.class,
                () -> carrinho.adicionar(null, 1));
    }

    @Test
    void adicionarQuantidadeZeroOuNegativaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> carrinho.adicionar(itemLeve, 0));

        assertThrows(IllegalArgumentException.class,
                () -> carrinho.adicionar(itemLeve, -1));
    }


    @Test
    void carrinhoNaoEhVazioAposAdicionarItem() {
        carrinho.adicionar(itemLeve, 1);

        assertFalse(carrinho.vazio());
    }


    @Test
    void adicionarItemCriaLinhaNoCarrinho() {
        carrinho.adicionar(itemLeve, 2);

        List<LinhaCarrinho> linhas = carrinho.linhas();

        assertEquals(1, linhas.size());
    }


    @Test
    void linhasDoCarrinhoSaoImutaveis() {
        carrinho.adicionar(itemLeve, 1);

        assertThrows(UnsupportedOperationException.class,
                () -> carrinho.linhas().add(null));
    }


    @Test
    void subtotalSomaTotalDasLinhas() {
        carrinho.adicionar(itemLeve, 1);   // 100
        carrinho.adicionar(itemPesado, 2); // 1600

        assertEquals(
                Dinheiro.of("1700.00"),
                carrinho.subtotal()
        );
    }


    @Test
    void pesoTotalEhSomaDoPesoDasLinhas() {
        carrinho.adicionar(itemLeve, 2);   // 1000g
        carrinho.adicionar(itemPesado, 1); // 3000g

        assertEquals(4000, carrinho.pesoTotalEmGramas());
    }



}

