package br.ufpe.cin.residencia.loja.dominio;

import java.math.BigDecimal;

public class LinhaCarrinho {
    private final Item item;
    private final int quantidade;

    public LinhaCarrinho(Item item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public Dinheiro totalDaLinha() {
        return item.getPrecoUnitario().multiplicar(new BigDecimal(quantidade));
    }

    public int pesoDaLinhaEmGramas() {
        return item.getPesoEmGramas() * quantidade;
    }
}
