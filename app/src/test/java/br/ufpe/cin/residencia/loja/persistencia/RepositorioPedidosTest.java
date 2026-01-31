package br.ufpe.cin.residencia.loja.persistencia;

import br.ufpe.cin.residencia.loja.persistencia.RepositorioPedidos;
import br.ufpe.cin.residencia.loja.pedidos.Pedido;
import br.ufpe.cin.residencia.loja.pedidos.StatusPedido;
import br.ufpe.cin.residencia.loja.pedidos.ItemPedido;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioPedidosTest {

    // Implementação fake da interface para testar
    static class RepositorioPedidosFake implements RepositorioPedidos {
        Map<String, Pedido> map = new HashMap<>();

        @Override
        public void salvar(Pedido pedido) {
            map.put(pedido.getId(), pedido);
        }

        @Override
        public Pedido obterPorId(String id) {
            Pedido p = map.get(id);
            if (p == null) throw new RuntimeException("Pedido não encontrado");
            return p;
        }

        @Override
        public List<Pedido> listarTodos() {
            return new ArrayList<>(map.values());
        }

        @Override
        public void atualizar(Pedido pedido) {
            map.put(pedido.getId(), pedido);
        }
    }

    private Pedido criarPedidoValido(String id) {
        ItemPedido item = new ItemPedido(
                "SKU1",
                "Produto Teste",
                Dinheiro.of("10.00"),
                500,
                2
        );
        return new Pedido(
                id,
                LocalDateTime.now(),
                null,
                List.of(item),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("20.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("0.00"),
                Dinheiro.of("1.00"),
                Dinheiro.of("5.00"),
                Dinheiro.of("26.00"),
                StatusPedido.PAGO
        );
    }

    @Test
    void deveSalvarEObterPedido() {
        RepositorioPedidos repo = new RepositorioPedidosFake();
        Pedido pedido = criarPedidoValido("1");

        repo.salvar(pedido);

        Pedido obtido = repo.obterPorId("1");
        assertEquals("1", obtido.getId());
    }

    @Test
    void deveAtualizarPedido() {
        RepositorioPedidos repo = new RepositorioPedidosFake();
        Pedido pedido = criarPedidoValido("2");
        repo.salvar(pedido);

        Pedido atualizado = criarPedidoValido("2");
        repo.atualizar(atualizado);

        Pedido obtido = repo.obterPorId("2");
        assertEquals("2", obtido.getId());
    }

    @Test
    void deveListarTodosPedidos() {
        RepositorioPedidos repo = new RepositorioPedidosFake();

        Pedido p1 = criarPedidoValido("10");
        Pedido p2 = criarPedidoValido("11");

        repo.salvar(p1);
        repo.salvar(p2);

        List<Pedido> todos = repo.listarTodos();
        assertEquals(2, todos.size());
        assertTrue(todos.stream().anyMatch(p -> p.getId().equals("10")));
        assertTrue(todos.stream().anyMatch(p -> p.getId().equals("11")));
    }
}
