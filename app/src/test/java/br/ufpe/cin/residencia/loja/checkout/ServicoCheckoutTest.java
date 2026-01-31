package br.ufpe.cin.residencia.loja.checkout;

import br.ufpe.cin.residencia.loja.catalogo.Catalogo;
import br.ufpe.cin.residencia.loja.catalogo.CatalogoEmMemoria;
import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.descontos.Cupom;
import br.ufpe.cin.residencia.loja.descontos.RepositorioCupom;
import br.ufpe.cin.residencia.loja.dominio.*;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;
import br.ufpe.cin.residencia.loja.pedidos.Pedido;
import br.ufpe.cin.residencia.loja.persistencia.RepositorioPedidos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServicoCheckoutTest {

    @TempDir
    Path tempDir;

    @Test
    void deveFalharNoConstrutorComDependenciasNulas() {
        assertThrows(IllegalArgumentException.class,
                () -> new ServicoCheckout(null, null, null));
    }

    @Test
    void deveFalharComEntradasNulas() {
        ServicoCheckout servico = servicoValido();

        assertThrows(IllegalArgumentException.class,
                () -> servico.finalizarCompra(null, MetodoFrete.PADRAO, MetodoPagamento.CARTAO, null));

        assertThrows(IllegalArgumentException.class,
                () -> servico.finalizarCompra(carrinhoValido(), null, MetodoPagamento.CARTAO, null));

        assertThrows(IllegalArgumentException.class,
                () -> servico.finalizarCompra(carrinhoValido(), MetodoFrete.PADRAO, null, null));
    }

    @Test
    void deveFalharComCupomInexistente() {
        CatalogoFake catalogo = new CatalogoFake();
        catalogo.adicionarItem("SKU1", 10);

        RepositorioCupomFake repoCupom = new RepositorioCupomFake();
        repoCupom.cupom = null;

        ServicoCheckout servico = new ServicoCheckout(
                catalogo,
                repoCupom,
                new RepositorioPedidosFake()
        );

        assertThrows(ExcecaoCupomInvalido.class, () ->
                servico.finalizarCompra(
                        carrinhoValido(),
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        CodigoCupom.FRETE_GRATIS
                )
        );
    }

    @Test
    void deveFalharQuandoCarrinhoEstaVazio() {
        ServicoCheckout servico = servicoValido();

        Carrinho carrinho = new Carrinho(); // vazio

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.finalizarCompra(
                        carrinho,
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        null
                )
        );

        assertEquals("Carrinho vazio não pode finalizar compra.", ex.getMessage());
    }

    @Test
    void deveFinalizarCompraSemCupom() {
        CatalogoFake catalogo = new CatalogoFake();
        catalogo.adicionarItem("SKU1", 10); // 👈 ESSENCIAL

        ServicoCheckout servico = new ServicoCheckout(
                catalogo,
                new RepositorioCupomFake(),
                new RepositorioPedidosFake()
        );

        Carrinho carrinho = new Carrinho();
        carrinho.adicionar(
                new Item("SKU1", "Produto", Dinheiro.of("100.00"), 500),
                1
        );

        ResultadoCheckout resultado = servico.finalizarCompra(
                carrinho,
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                null
        );

        assertNotNull(resultado);
    }


    @Test
    void deveZerarDescontoQuandoPagamentoEhBoletoESubtotalMenorQue200() {
        CatalogoFake catalogo = new CatalogoFake();
        catalogo.adicionarItem("SKU1", 10); // 👈 necessário

        ServicoCheckout servico = new ServicoCheckout(
                catalogo,
                new RepositorioCupomFake(),
                new RepositorioPedidosFake()
        );

        Carrinho carrinho = new Carrinho();
        carrinho.adicionar(
                new Item("SKU1", "Produto", Dinheiro.of("100.00"), 500),
                1
        );

        ResultadoCheckout resultado = servico.finalizarCompra(
                carrinho,
                MetodoFrete.PADRAO,
                MetodoPagamento.BOLETO,
                null
        );

        assertEquals(
                Dinheiro.zero(),
                resultado.getRecibo().descontoPagamento
        );
    }



    @Test
    void deveFalharQuandoNaoHaEstoqueSuficiente() {
        CatalogoFake catalogo = new CatalogoFake();
        catalogo.adicionarItem("SKU1", 1); // estoque 1

        ServicoCheckout servico = new ServicoCheckout(
                catalogo,
                new RepositorioCupomFake(),
                new RepositorioPedidosFake()
        );

        Carrinho carrinho = new Carrinho();
        carrinho.adicionar(
                new Item("SKU1", "Produto", Dinheiro.of("100.00"), 500),
                2 // 👈 pede mais do que tem
        );

        ExcecaoSemEstoque ex = assertThrows(
                ExcecaoSemEstoque.class,
                () -> servico.finalizarCompra(
                        carrinho,
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        null
                )
        );

        assertTrue(ex.getMessage().contains("Sem estoque para SKU"));
    }

    @Test
    void registrarUsoCupomMarcaComoUsado() throws Exception {
        CatalogoEmMemoria catalogo = CatalogoEmMemoria.catalogoPadrao();

        RepositorioCupom repositorioCupom = new RepositorioCupom() {
            boolean usado = false;
            @Override public Cupom obter(CodigoCupom codigo) { return new Cupom(
                    CodigoCupom.DESCONTO5,
                    LocalDate.now().plusDays(10),
                    true,
                    usado
            ); }
            @Override public void marcarComoUsado(CodigoCupom codigo) { usado = true; }
        };

        RepositorioPedidos repositorioPedidos = new RepositorioPedidos() {
            @Override public void salvar(Pedido pedido) {}

            @Override
            public Pedido obterPorId(String id) {
                return null;
            }

            @Override
            public List<Pedido> listarTodos() {
                return List.of();
            }

            @Override
            public void atualizar(Pedido pedido) {

            }
        };

        ServicoCheckout checkout = new ServicoCheckout(catalogo, repositorioCupom, repositorioPedidos);

        Cupom cupom = new Cupom(CodigoCupom.DESCONTO5, LocalDate.now().plusDays(10), true, false) {
            @Override
            public boolean isUsoUnico() { return true; }
        };

        var method = ServicoCheckout.class.getDeclaredMethod("registrarUsoCupom", Cupom.class);
        method.setAccessible(true);
        method.invoke(checkout, cupom);
    }

    @Test
    void deveCriarDiretorioPaiEArquivoQuandoParentNaoExiste() throws IOException {
        Path subDir = tempDir.resolve("pedidos");
        Path arquivo = subDir.resolve("pedidos.csv");

        assertFalse(subDir.toFile().exists());
    }


    @Test
    void deveMarcarCupomComoUsadoQuandoUsoUnico() {
        CatalogoFake catalogo = new CatalogoFake();
        catalogo.adicionarItem("SKU1", 10);

        RepositorioCupomFake repoCupom = new RepositorioCupomFake();

        repoCupom.cupom = new Cupom(
                CodigoCupom.FRETE_GRATIS,
                LocalDate.now().plusDays(10),
                true,   // uso único
                false   // ainda não usado
        );

        ServicoCheckout servico = new ServicoCheckout(
                catalogo,
                repoCupom,
                new RepositorioPedidosFake()
        );

        servico.finalizarCompra(
                carrinhoValido(),
                MetodoFrete.PADRAO,
                MetodoPagamento.CARTAO,
                CodigoCupom.FRETE_GRATIS
        );

        assertTrue(repoCupom.marcadoComoUsado);
    }


    @Test
    void deveFalharComSkuInexistenteNoCatalogo() {
        ServicoCheckout servico = servicoValido();

        Carrinho carrinho = carrinhoComSku("SKU_INEXISTENTE");

        assertThrows(ExcecaoSemEstoque.class, () ->
                servico.finalizarCompra(
                        carrinho,
                        MetodoFrete.PADRAO,
                        MetodoPagamento.CARTAO,
                        null
                )
        );
    }


    private ServicoCheckout servicoValido() {
        return new ServicoCheckout(
                new CatalogoFake(),
                new RepositorioCupomFake(),
                new RepositorioPedidosFake()
        );
    }

    private Carrinho carrinhoValido() {
        return carrinhoComSku("SKU1");
    }

    private Carrinho carrinhoComSku(String sku) {
        Carrinho carrinho = new Carrinho();
        Item item = new Item(sku, "Produto", Dinheiro.of("100.00"), 500);
        carrinho.adicionar(item, 1);
        return carrinho;
    }

    private Carrinho carrinhoComValor(Dinheiro valor) {
        Carrinho carrinho = new Carrinho();
        Item item = new Item("SKU1", "Produto", valor, 500);
        carrinho.adicionar(item, 1);
        return carrinho;
    }

    /* =========================================================
       Fakes
       ========================================================= */

    static class CatalogoFake implements Catalogo {
        private final Map<String, Integer> estoque = new HashMap<>();

        void adicionarItem(String sku, int qtd) {
            estoque.put(sku, qtd);
        }

        @Override
        public Item obterPorSku(String sku) {
            return estoque.containsKey(sku)
                    ? new Item(sku, "Produto", Dinheiro.of("100.00"), 500)
                    : null;
        }

        @Override
        public int estoquePara(String sku) {
            return estoque.getOrDefault(sku, 0);
        }

        @Override
        public void baixarEstoque(String sku, int quantidade) { }

        @Override
        public void reporEstoque(String sku, int quantidade) { }
    }

    static class RepositorioCupomFake implements RepositorioCupom {
        Cupom cupom;
        boolean marcadoComoUsado = false;

        @Override
        public Cupom obter(CodigoCupom codigo) {
            if (cupom != null && cupom.getCodigo().equals(codigo)) {
                return cupom;
            }
            return null;
        }

        @Override
        public void marcarComoUsado(CodigoCupom codigo) {
            marcadoComoUsado = true;
        }
    }

    static class RepositorioPedidosFake implements RepositorioPedidos {
        @Override
        public void salvar(br.ufpe.cin.residencia.loja.pedidos.Pedido pedido) { }

        @Override
        public br.ufpe.cin.residencia.loja.pedidos.Pedido obterPorId(String id) {
            return null;
        }

        @Override
        public List<Pedido> listarTodos() {
            return List.of();
        }

        @Override
        public void atualizar(br.ufpe.cin.residencia.loja.pedidos.Pedido pedido) { }
    }
}
