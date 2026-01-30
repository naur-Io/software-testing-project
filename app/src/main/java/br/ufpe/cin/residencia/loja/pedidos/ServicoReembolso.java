package br.ufpe.cin.residencia.loja.pedidos;

import br.ufpe.cin.residencia.loja.catalogo.Catalogo;
import br.ufpe.cin.residencia.loja.persistencia.RepositorioPedidos;

import java.time.LocalDateTime;

public class ServicoReembolso {
    private final RepositorioPedidos repositorioPedidos;
    private final Catalogo catalogo;

    public ServicoReembolso(RepositorioPedidos repositorioPedidos, Catalogo catalogo) {
        if (repositorioPedidos == null || catalogo == null) {
            throw new IllegalArgumentException("Repositório de pedidos e catálogo são obrigatórios.");
        }
        this.repositorioPedidos = repositorioPedidos;
        this.catalogo = catalogo;
    }

    public Pedido reembolsar(String idPedido) {
        Pedido pedido = repositorioPedidos.obterPorId(idPedido);
        if (pedido.getStatus() == StatusPedido.REEMBOLSADO) {
            throw new ExcecaoPedidoJaReembolsado("Pedido já reembolsado: " + idPedido);
        }
        for (ItemPedido item : pedido.getItens()) {
            catalogo.reporEstoque(item.getSku(), item.getQuantidade());
        }
        pedido.marcarReembolsado(LocalDateTime.now());
        repositorioPedidos.atualizar(pedido);
        return pedido;
    }
}
