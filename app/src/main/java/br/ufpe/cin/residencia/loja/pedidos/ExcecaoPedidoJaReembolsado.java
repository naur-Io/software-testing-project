package br.ufpe.cin.residencia.loja.pedidos;

public class ExcecaoPedidoJaReembolsado extends RuntimeException {
    public ExcecaoPedidoJaReembolsado(String mensagem) {
        super(mensagem);
    }
}
