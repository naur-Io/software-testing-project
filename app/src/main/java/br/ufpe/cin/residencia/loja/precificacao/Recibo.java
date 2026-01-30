package br.ufpe.cin.residencia.loja.precificacao;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;

public class Recibo {
    public final Dinheiro subtotal;
    public final Dinheiro descontoCupom;
    public final Dinheiro descontoPagamento;
    public final Dinheiro imposto;
    public final Dinheiro frete;
    public final Dinheiro total;

    public Recibo(Dinheiro subtotal, Dinheiro descontoCupom, Dinheiro descontoPagamento,
                  Dinheiro imposto, Dinheiro frete, Dinheiro total) {
        this.subtotal = subtotal;
        this.descontoCupom = descontoCupom;
        this.descontoPagamento = descontoPagamento;
        this.imposto = imposto;
        this.frete = frete;
        this.total = total;
    }

    @Override
    public String toString() {
        return "Recibo" + System.lineSeparator()
                + "Subtotal: R$ " + subtotal + System.lineSeparator()
                + "Desconto (cupom): R$ " + descontoCupom + System.lineSeparator()
                + "Desconto (pagamento): R$ " + descontoPagamento + System.lineSeparator()
                + "Imposto: R$ " + imposto + System.lineSeparator()
                + "Frete: R$ " + frete + System.lineSeparator()
                + "Total: R$ " + total;
    }
}
