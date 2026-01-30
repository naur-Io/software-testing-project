package br.ufpe.cin.residencia.loja.pedidos;

public class ExcecaoPedidoNaoEncontrado extends RuntimeException {
    public ExcecaoPedidoNaoEncontrado(String mensagem) {
        super(mensagem);
    }
}
