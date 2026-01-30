package br.ufpe.cin.residencia.loja.persistencia;

import br.ufpe.cin.residencia.loja.pedidos.Pedido;

import java.util.List;

public interface RepositorioPedidos {
    void salvar(Pedido pedido);

    Pedido obterPorId(String id);

    List<Pedido> listarTodos();

    void atualizar(Pedido pedido);
}
