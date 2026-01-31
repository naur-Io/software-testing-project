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
        assertThrows(IllegalArgumentException.class, () ->
                new Pedido(
                        " ",
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
                )
        );
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
    void construtorRejeitaFreteOuPagamentoNulos() {
        assertThrows(IllegalArgumentException.class, () ->
                new Pedido(
                        "PED",
                        agora,
                        null,
                        List.of(item),
                        null,
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
        assertThrows(IllegalArgumentException.class, () ->
                new Pedido(
                        "PED",
                        agora,
                        null,
                        List.of(item),
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        null,
                        null,
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
    void marcarReembolsadoComDataNulaLancaExcecao() {
        Pedido pedido = pedidoValido(StatusPedido.PAGO);

        assertThrows(NullPointerException.class, () ->
                pedido.marcarReembolsado(null)
        );
    }
}
