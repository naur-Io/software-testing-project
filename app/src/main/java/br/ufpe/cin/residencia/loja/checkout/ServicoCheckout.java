package br.ufpe.cin.residencia.loja.checkout;

import br.ufpe.cin.residencia.loja.catalogo.Catalogo;
import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.descontos.Cupom;
import br.ufpe.cin.residencia.loja.descontos.PoliticaCupom;
import br.ufpe.cin.residencia.loja.descontos.RepositorioCupom;
import br.ufpe.cin.residencia.loja.dominio.Carrinho;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.LinhaCarrinho;
import br.ufpe.cin.residencia.loja.dominio.Item;
import br.ufpe.cin.residencia.loja.frete.CalculadoraFrete;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;
import br.ufpe.cin.residencia.loja.pagamento.PoliticaDescontoPagamento;
import br.ufpe.cin.residencia.loja.precificacao.CalculadoraImposto;
import br.ufpe.cin.residencia.loja.precificacao.Recibo;
import br.ufpe.cin.residencia.loja.pedidos.ItemPedido;
import br.ufpe.cin.residencia.loja.pedidos.Pedido;
import br.ufpe.cin.residencia.loja.pedidos.StatusPedido;
import br.ufpe.cin.residencia.loja.persistencia.RepositorioPedidos;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServicoCheckout {
    private final Catalogo catalogo;
    private final RepositorioCupom repositorioCupom;
    private final RepositorioPedidos repositorioPedidos;
    private final CalculadoraFrete calculadoraFrete = new CalculadoraFrete();
    private final PoliticaCupom politicaCupom = new PoliticaCupom();
    private final PoliticaDescontoPagamento politicaDescontoPagamento = new PoliticaDescontoPagamento();
    private final CalculadoraImposto calculadoraImposto = new CalculadoraImposto();

    public ServicoCheckout(Catalogo catalogo, RepositorioCupom repositorioCupom,
                           RepositorioPedidos repositorioPedidos) {
        if (catalogo == null || repositorioCupom == null || repositorioPedidos == null) {
            throw new IllegalArgumentException("Catálogo, repositório de cupom e pedidos são obrigatórios.");
        }
        this.catalogo = catalogo;
        this.repositorioCupom = repositorioCupom;
        this.repositorioPedidos = repositorioPedidos;
    }

    public ResultadoCheckout finalizarCompra(Carrinho carrinho, MetodoFrete frete, MetodoPagamento pagamento,
                                             CodigoCupom codigoCupomOpcional) {
        validarEntrada(carrinho, frete, pagamento);
        validarEstoque(carrinho);

        Cupom cupom = obterCupom(codigoCupomOpcional);

        Dinheiro subtotal = carrinho.subtotal();
        int pesoTotal = carrinho.pesoTotalEmGramas();

        Dinheiro descontoCupom = calcularDescontoCupom(cupom, subtotal);
        Dinheiro baseDescontoPagamento = subtotal.subtrair(descontoCupom).clampMinZero();

        Dinheiro descontoPagamento = calcularDescontoPagamento(pagamento, cupom, baseDescontoPagamento, subtotal);

        Dinheiro imposto = calcularImposto(subtotal, descontoCupom, descontoPagamento);
        Dinheiro freteFinal = calcularFrete(frete, subtotal, pesoTotal, cupom);
        Dinheiro total = calcularTotal(subtotal, descontoCupom, descontoPagamento, imposto, freteFinal);

        Recibo recibo = new Recibo(subtotal, descontoCupom, descontoPagamento, imposto, freteFinal, total);
        Pedido pedido = criarPedido(carrinho, frete, pagamento, codigoCupomOpcional, recibo);

        aplicarEfeitosColaterais(carrinho, cupom, pedido);
        return new ResultadoCheckout(recibo, pedido.getId());
    }

    private void validarEntrada(Carrinho carrinho, MetodoFrete frete, MetodoPagamento pagamento) {
        if (carrinho == null || frete == null || pagamento == null) {
            throw new IllegalArgumentException("Entradas obrigatórias não podem ser nulas.");
        }
        if (carrinho.vazio()) {
            throw new IllegalArgumentException("Carrinho vazio não pode finalizar compra.");
        }
    }

    private Cupom obterCupom(CodigoCupom codigoCupomOpcional) {
        if (codigoCupomOpcional == null) {
            return null;
        }
        Cupom cupom = repositorioCupom.obter(codigoCupomOpcional);
        if (cupom == null) {
            throw new ExcecaoCupomInvalido("Cupom inexistente: " + codigoCupomOpcional);
        }
        politicaCupom.validarCupom(cupom);
        return cupom;
    }

    private Dinheiro calcularDescontoCupom(Cupom cupom, Dinheiro subtotal) {
        return politicaCupom.descontoNoSubtotal(cupom, subtotal);
    }

    private Dinheiro calcularDescontoPagamento(MetodoPagamento pagamento, Cupom cupom,
                                               Dinheiro baseDescontoPagamento, Dinheiro subtotal) {
        Dinheiro descontoPagamento = politicaDescontoPagamento.descontoPorPagamento(
                pagamento, cupom, baseDescontoPagamento);

        if (pagamento == MetodoPagamento.BOLETO && !subtotal.maiorOuIgual(Dinheiro.of("200.00"))) {
            descontoPagamento = Dinheiro.zero();
        }

        return descontoPagamento.min(baseDescontoPagamento);
    }

    private Dinheiro calcularImposto(Dinheiro subtotal, Dinheiro descontoCupom, Dinheiro descontoPagamento) {
        Dinheiro baseImposto = subtotal.subtrair(descontoCupom).subtrair(descontoPagamento).clampMinZero();
        return calculadoraImposto.calcularImposto(baseImposto);
    }

    private Dinheiro calcularFrete(MetodoFrete frete, Dinheiro subtotal, int pesoTotal, Cupom cupom) {
        Dinheiro freteCalculado = calculadoraFrete.custoFrete(frete, subtotal, pesoTotal);
        boolean freteGratisPromocao = frete == MetodoFrete.PADRAO && subtotal.maiorOuIgual(Dinheiro.of("250.00"));
        boolean freteGratisCupom = politicaCupom.freteDeveSerGratis(cupom, frete, subtotal);
        return (freteGratisPromocao || freteGratisCupom) ? Dinheiro.zero() : freteCalculado;
    }

    private Dinheiro calcularTotal(Dinheiro subtotal, Dinheiro descontoCupom, Dinheiro descontoPagamento,
                                   Dinheiro imposto, Dinheiro freteFinal) {
        Dinheiro total = subtotal.subtrair(descontoCupom)
                .subtrair(descontoPagamento)
                .somar(imposto)
                .somar(freteFinal);
        if (total.ehNegativo()) {
            throw new ExcecaoTotalInvalido("Total negativo não é permitido.");
        }
        return total;
    }

    private void registrarUsoCupom(Cupom cupom) {
        if (cupom != null && cupom.isUsoUnico()) {
            repositorioCupom.marcarComoUsado(cupom.getCodigo());
        }
    }

    private Pedido criarPedido(Carrinho carrinho, MetodoFrete frete, MetodoPagamento pagamento,
                               CodigoCupom codigoCupom, Recibo recibo) {
        List<ItemPedido> itens = carrinho.linhas().stream()
                .map(linha -> {
                    Item item = linha.getItem();
                    return new ItemPedido(item.getSku(), item.getNome(), item.getPrecoUnitario(),
                            item.getPesoEmGramas(), linha.getQuantidade());
                })
                .collect(java.util.stream.Collectors.toList());
        return new Pedido(UUID.randomUUID().toString(), LocalDateTime.now(), null, itens,
                frete, pagamento, codigoCupom,
                recibo.subtotal, recibo.descontoCupom, recibo.descontoPagamento,
                recibo.imposto, recibo.frete, recibo.total, StatusPedido.PAGO);
    }

    private void aplicarEfeitosColaterais(Carrinho carrinho, Cupom cupom, Pedido pedido) {
        Map<String, Integer> totais = new HashMap<>();
        for (LinhaCarrinho linha : carrinho.linhas()) {
            Item item = linha.getItem();
            totais.merge(item.getSku(), linha.getQuantidade(), Integer::sum);
        }
        for (Map.Entry<String, Integer> entrada : totais.entrySet()) {
            catalogo.baixarEstoque(entrada.getKey(), entrada.getValue());
        }
        repositorioPedidos.salvar(pedido);
        registrarUsoCupom(cupom);
    }

    private void validarEstoque(Carrinho carrinho) {
        Map<String, Integer> totais = new HashMap<>();
        for (LinhaCarrinho linha : carrinho.linhas()) {
            Item item = linha.getItem();
            totais.merge(item.getSku(), linha.getQuantidade(), Integer::sum);
        }

        for (Map.Entry<String, Integer> entrada : totais.entrySet()) {
            String sku = entrada.getKey();
            int quantidadeSolicitada = entrada.getValue();
            if (catalogo.obterPorSku(sku) == null) {
                throw new ExcecaoSemEstoque("SKU inexistente no catálogo: " + sku);
            }
            int estoque = catalogo.estoquePara(sku);
            if (estoque < quantidadeSolicitada) {
                throw new ExcecaoSemEstoque("Sem estoque para SKU: " + sku);
            }
        }
    }
}
