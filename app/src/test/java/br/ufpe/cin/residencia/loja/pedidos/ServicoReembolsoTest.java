package br.ufpe.cin.residencia.loja.pedidos;

import br.ufpe.cin.residencia.loja.catalogo.Catalogo;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;
import br.ufpe.cin.residencia.loja.persistencia.RepositorioPedidos;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoReembolsoTest {

    /* =========================
       FAKES / IN-MEMORY
       ========================= */

    static class RepositorioPedidosFake implements RepositorioPedidos {
        private final Map<String, Pedido> pedidos = new HashMap<>();

        @Override
        public void salvar(Pedido pedido) {
            pedidos.put(pedido.getId(), pedido);
        }

        @Override
        public Pedido obterPorId(String id) {
            return pedidos.get(id);
        }

        @Override
        public List<Pedido> listarTodos() {
            return List.of();
        }

        @Override
        public void atualizar(Pedido pedido) {
            pedidos.put(pedido.getId(), pedido);
        }
    }

    static class CatalogoFake implements Catalogo {
        private final Map<String, Integer> estoque = new HashMap<>();

        void adicionarEstoque(String sku, int quantidade) {
            estoque.put(sku, quantidade);
        }

        int estoqueAtual(String sku) {
            return estoque.getOrDefault(sku, 0);
        }

        @Override
        public Item obterPorSku(String sku) {
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
            estoque.put(sku, estoqueAtual(sku) + quantidade);
        }
    }

    /* =========================
       MÉTODOS AUXILIARES
       ========================= */

    private Pedido pedidoPago() {
        ItemPedido item1 = new ItemPedido(
                "SKU1",
                "Produto 1",
                Dinheiro.of("50.00"),
                100,
                2
        );

        ItemPedido item2 = new ItemPedido(
                "SKU2",
                "Produto 2",
                Dinheiro.of("30.00"),
                200,
                1
        );

        return new Pedido(
                "PED-1",
                LocalDateTime.now().minusDays(1),
                null,
                List.of(item1, item2),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("130.00"),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.zero(),
                Dinheiro.of("10.00"),
                Dinheiro.of("140.00"),
                StatusPedido.PAGO
        );
    }

    /* =========================
       TESTES
       ========================= */

    @Test
    void construtorNaoAceitaDependenciasNulas() {
        CatalogoFake catalogo = new CatalogoFake();
        RepositorioPedidosFake repositorio = new RepositorioPedidosFake();

        assertThrows(IllegalArgumentException.class,
                () -> new ServicoReembolso(null, catalogo));

        assertThrows(IllegalArgumentException.class,
                () -> new ServicoReembolso(repositorio, null));
    }

    @Test
    void reembolsarPedidoPagoReporEstoqueAtualizaPedido() {
        CatalogoFake catalogo = new CatalogoFake();
        RepositorioPedidosFake repositorio = new RepositorioPedidosFake();

        catalogo.adicionarEstoque("SKU1", 5);
        catalogo.adicionarEstoque("SKU2", 3);

        Pedido pedido = pedidoPago();
        repositorio.salvar(pedido);

        ServicoReembolso servico = new ServicoReembolso(repositorio, catalogo);

        Pedido resultado = servico.reembolsar("PED-1");

        assertEquals(StatusPedido.REEMBOLSADO, resultado.getStatus());
        assertNotNull(resultado.getReembolsadoEm());

        assertEquals(7, catalogo.estoqueAtual("SKU1")); // 5 + 2
        assertEquals(4, catalogo.estoqueAtual("SKU2")); // 3 + 1
    }

    @Test
    void reembolsarPedidoJaReembolsadoLancaExcecao() {
        CatalogoFake catalogo = new CatalogoFake();
        RepositorioPedidosFake repositorio = new RepositorioPedidosFake();

        Pedido pedido = pedidoPago();
        pedido.marcarReembolsado(LocalDateTime.now().minusHours(2));
        repositorio.salvar(pedido);

        ServicoReembolso servico = new ServicoReembolso(repositorio, catalogo);

        assertThrows(ExcecaoPedidoJaReembolsado.class,
                () -> servico.reembolsar("PED-1"));
    }
}
