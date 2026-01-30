package br.ufpe.cin.residencia.loja.checkout;

import br.ufpe.cin.residencia.loja.precificacao.Recibo;

public class ResultadoCheckout {
    private final Recibo recibo;
    private final String idPedido;

    public ResultadoCheckout(Recibo recibo, String idPedido) {
        if (recibo == null || idPedido == null || idPedido.isBlank()) {
            throw new IllegalArgumentException("Recibo e id do pedido são obrigatórios.");
        }
        this.recibo = recibo;
        this.idPedido = idPedido;
    }

    public Recibo getRecibo() {
        return recibo;
    }

    public String getIdPedido() {
        return idPedido;
    }
}
