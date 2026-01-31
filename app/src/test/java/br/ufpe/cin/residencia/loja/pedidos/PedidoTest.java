package br.ufpe.cin.residencia.loja.pedidos;


import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {


    private ItemPedido item;
    private LocalDateTime agora;

    @BeforeEach
    void setup() {
        item = new ItemPedido(
                "KB",
                "Keyboard",
                Dinheiro.of("100.00"),
                500,
                2
        );
        agora = LocalDateTime.now();
    }

    private Pedido pedidoValido(StatusPedido status) {
        return new Pedido(
                "PED-1",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                CodigoCupom.DESCONTO5,
                Dinheiro.of("200.00"),
                Dinheiro.of("5.00"),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                Dinheiro.of("20.00"),
                Dinheiro.of("225.00"),
                status
        );
    }

    @Test
    void criaPedidoValidoComStatusPadraoPago() {
        Pedido pedido = pedidoValido(null);

        assertEquals("PED-1", pedido.getId());
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        assertEquals(1, pedido.getItens().size());
        assertEquals(Dinheiro.of("225.00"), pedido.getTotal());
    }

    @Test
    void construtorRejeitaIdNuloOuEmBranco() {
        ItemPedido item = new ItemPedido("SKU1", "Produto", Dinheiro.of("10.00"), 100, 1);
        LocalDateTime agora = LocalDateTime.now();

        // Id nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                null,  // <--- id nulo
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        // Id em branco
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "  ", // <--- id em branco
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));
    }


    @Test
    void construtorRejeitaDataCriacaoNula() {
        assertThrows(IllegalArgumentException.class, () ->
                new Pedido(
                        "PED",
                        null,
                        null,
                        List.of(item),
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        null,
                        Dinheiro.of("10.00"),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.of("10.00"),
                        StatusPedido.PAGO
                )
        );
    }

    @Test
    void construtorRejeitaListaDeItensNulaOuVazia() {
        assertThrows(IllegalArgumentException.class, () ->
                new Pedido(
                        "PED",
                        agora,
                        null,
                        List.of(),
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        null,
                        Dinheiro.of("10.00"),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.of("10.00"),
                        StatusPedido.PAGO
                )
        );
    }



    @Test
    void construtorRejeitaValoresMonetariosNulos() {
        ItemPedido item = new ItemPedido("SKU1", "Produto", Dinheiro.of("10.00"), 100, 1);
        LocalDateTime agora = LocalDateTime.now();

        // subtotal nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-1",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                null, // <--- subtotal nulo
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        // descontoCupom nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-2",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                null, // <--- descontoCupom nulo
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        // descontoPagamento nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-3",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                null, // <--- descontoPagamento nulo
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        // imposto nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-4",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                null, // <--- imposto nulo
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        // frete nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-5",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                null, // <--- frete nulo
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        // total nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-6",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                null, // <--- total nulo
                StatusPedido.PAGO
        ));
    }


    @Test
    void construtorRejeitaTotalNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                new Pedido(
                        "PED",
                        agora,
                        null,
                        List.of(item),
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        null,
                        Dinheiro.of("10.00"),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.zero(),
                        Dinheiro.of("-1.00"),
                        StatusPedido.PAGO
                )
        );
    }



    @Test
    void marcarReembolsadoAtualizaStatusEData() {
        Pedido pedido = pedidoValido(StatusPedido.PAGO);
        LocalDateTime quando = LocalDateTime.now().plusDays(1);

        pedido.marcarReembolsado(quando);

        assertEquals(StatusPedido.REEMBOLSADO, pedido.getStatus());
        assertEquals(quando, pedido.getReembolsadoEm());
    }

    @Test
    void marcarReembolsadoDuasVezesLancaExcecao() {
        Pedido pedido = pedidoValido(StatusPedido.REEMBOLSADO);

        assertThrows(ExcecaoPedidoJaReembolsado.class, () ->
                pedido.marcarReembolsado(LocalDateTime.now())
        );
    }

    @Test
    void construtorRejeitaItensNulosOuVazios() {
        // Lista nula
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-1",
                LocalDateTime.now(),
                null,
                null, // <--- itera nula
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-1",
                LocalDateTime.now(),
                null,
                List.of(), // <--- lista vazia
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));
    }

    @Test
    void construtorRejeitaFreteOuPagamentoNulos() {
        ItemPedido item = new ItemPedido("SKU1", "Produto", Dinheiro.of("10.00"), 100, 1);
        LocalDateTime agora = LocalDateTime.now();

        // Frete nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-1",
                agora,
                null,
                List.of(item),
                null, // <--- frete nulo
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));

        // Pagamento nulo
        assertThrows(IllegalArgumentException.class, () -> new Pedido(
                "PED-2",
                agora,
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                null, // <--- pagamento nulo
                null,
                Dinheiro.of("10.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                StatusPedido.PAGO
        ));
    }




    @Test
    void construtorRejeitaListaDeItensNulaOuVaziaW() {
        assertThrows(IllegalArgumentException.class, () ->
                new Pedido("PED", LocalDateTime.now(), null,
                        List.of(), MetodoFrete.PADRAO, MetodoPagamento.CARTAO,
                        null, Dinheiro.of("10.00"), Dinheiro.zero(), Dinheiro.zero(),
                        Dinheiro.zero(), Dinheiro.zero(), Dinheiro.of("10.00"), StatusPedido.PAGO)
        );
    }


    @Test
    void marcarReembolsadoComDataNulaLancaExcecao() {
        Pedido pedido = pedidoValido(StatusPedido.PAGO);

        assertThrows(NullPointerException.class, () ->
                pedido.marcarReembolsado(null)
        );
    }

    @Test
    void criaPedidoComStatusEspecifico() {
        Pedido pedido = pedidoValido(StatusPedido.REEMBOLSADO);
        assertEquals(StatusPedido.REEMBOLSADO, pedido.getStatus());
    }

}
