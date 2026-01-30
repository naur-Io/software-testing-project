package br.ufpe.cin.residencia.loja.pedidos;

import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Pedido {
    private final String id;
    private final LocalDateTime criadoEm;
    private LocalDateTime reembolsadoEm;
    private final List<ItemPedido> itens;
    private final MetodoFrete metodoFrete;
    private final MetodoPagamento metodoPagamento;
    private final CodigoCupom codigoCupom;
    private final Dinheiro subtotal;
    private final Dinheiro descontoCupom;
    private final Dinheiro descontoPagamento;
    private final Dinheiro imposto;
    private final Dinheiro frete;
    private final Dinheiro total;
    private StatusPedido status;

    public Pedido(String id, LocalDateTime criadoEm, LocalDateTime reembolsadoEm,
                  List<ItemPedido> itens, MetodoFrete metodoFrete, MetodoPagamento metodoPagamento,
                  CodigoCupom codigoCupom, Dinheiro subtotal, Dinheiro descontoCupom,
                  Dinheiro descontoPagamento, Dinheiro imposto, Dinheiro frete, Dinheiro total,
                  StatusPedido status) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id do pedido não pode ser vazio.");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("Data de criação do pedido é obrigatória.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve possuir ao menos um item.");
        }
        if (metodoFrete == null || metodoPagamento == null) {
            throw new IllegalArgumentException("Frete e pagamento são obrigatórios.");
        }
        if (subtotal == null || descontoCupom == null || descontoPagamento == null
                || imposto == null || frete == null || total == null) {
            throw new IllegalArgumentException("Valores monetários do pedido são obrigatórios.");
        }
        if (total.ehNegativo()) {
            throw new IllegalArgumentException("Total do pedido não pode ser negativo.");
        }
        this.id = id;
        this.criadoEm = criadoEm;
        this.reembolsadoEm = reembolsadoEm;
        this.itens = List.copyOf(itens);
        this.metodoFrete = metodoFrete;
        this.metodoPagamento = metodoPagamento;
        this.codigoCupom = codigoCupom;
        this.subtotal = subtotal;
        this.descontoCupom = descontoCupom;
        this.descontoPagamento = descontoPagamento;
        this.imposto = imposto;
        this.frete = frete;
        this.total = total;
        this.status = status == null ? StatusPedido.PAGO : status;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getReembolsadoEm() {
        return reembolsadoEm;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public MetodoFrete getMetodoFrete() {
        return metodoFrete;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public CodigoCupom getCodigoCupom() {
        return codigoCupom;
    }

    public Dinheiro getSubtotal() {
        return subtotal;
    }

    public Dinheiro getDescontoCupom() {
        return descontoCupom;
    }

    public Dinheiro getDescontoPagamento() {
        return descontoPagamento;
    }

    public Dinheiro getImposto() {
        return imposto;
    }

    public Dinheiro getFrete() {
        return frete;
    }

    public Dinheiro getTotal() {
        return total;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void marcarReembolsado(LocalDateTime quando) {
        if (status == StatusPedido.REEMBOLSADO) {
            throw new ExcecaoPedidoJaReembolsado("Pedido já reembolsado: " + id);
        }
        this.status = StatusPedido.REEMBOLSADO;
        this.reembolsadoEm = Objects.requireNonNull(quando, "Data de reembolso é obrigatória.");
    }
}
