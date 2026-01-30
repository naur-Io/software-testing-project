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
    void carrinho_comeca_vazio() {
        assertTrue(carrinho.vazio());
        assertEquals(0, carrinho.linhas().size());
        assertEquals(Dinheiro.zero(), carrinho.subtotal());
        assertEquals(0, carrinho.pesoTotalEmGramas());
    }

    @Test
    void adicionar_item_valido_torna_carrinho_nao_vazio() {
        carrinho.adicionar(itemLeve, 1);

        assertFalse(carrinho.vazio());
        assertEquals(1, carrinho.linhas().size());
    }

    @Test
    void adicionar_item_nulo_lanca_excecao() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> carrinho.adicionar(null, 1)
        );

        assertEquals("Item não pode ser nulo.", ex.getMessage());
    }

    @Test
    void adicionar_quantidade_zero_lanca_excecao() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> carrinho.adicionar(itemLeve, 0)
        );

        assertEquals("Quantidade deve ser maior que zero.", ex.getMessage());
    }

    @Test
    void adicionar_quantidade_negativa_lanca_excecao() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> carrinho.adicionar(itemLeve, -1)
        );

        assertEquals("Quantidade deve ser maior que zero.", ex.getMessage());
    }

    @Test
    void subtotal_com_um_item() {
        carrinho.adicionar(itemLeve, 2);

        Dinheiro esperado = Dinheiro.of("200.00");

        assertEquals(esperado, carrinho.subtotal());
    }

    @Test
    void subtotal_com_multiplos_itens() {
        carrinho.adicionar(itemLeve, 2);
        carrinho.adicionar(itemPesado, 1);

        Dinheiro esperado = Dinheiro.of("1000.00");

        assertEquals(esperado, carrinho.subtotal());
    }

    @Test
    void peso_total_com_um_item() {
        carrinho.adicionar(itemLeve, 3);

        assertEquals(1500, carrinho.pesoTotalEmGramas());
    }

    @Test
    void peso_total_com_multiplos_itens() {
        carrinho.adicionar(itemLeve, 2);
        carrinho.adicionar(itemPesado, 1);

        assertEquals(4000, carrinho.pesoTotalEmGramas());
    }

    @Test
    void linhas_retorna_lista_imutavel() {
        carrinho.adicionar(itemLeve, 1);

        List<LinhaCarrinho> linhas = carrinho.linhas();

        assertThrows(
                UnsupportedOperationException.class,
                () -> linhas.add(new LinhaCarrinho(itemLeve, 1))
        );
    }
}
