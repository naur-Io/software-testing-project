package br.ufpe.cin.residencia.loja.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Carrinho {
    private final List<LinhaCarrinho> linhas = new ArrayList<>();

    public void adicionar(Item item, int quantidade) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        linhas.add(new LinhaCarrinho(item, quantidade));
    }

    public List<LinhaCarrinho> linhas() {
        return Collections.unmodifiableList(linhas);
    }

    public Dinheiro subtotal() {
        Dinheiro total = Dinheiro.zero();
        for (LinhaCarrinho linha : linhas) {
            total = total.somar(linha.totalDaLinha());
        }
        return total;
    }

    public int pesoTotalEmGramas() {
        int total = 0;
        for (LinhaCarrinho linha : linhas) {
            total += linha.pesoDaLinhaEmGramas();
        }
        return total;
    }

    public boolean vazio() {
        return linhas.isEmpty();
    }
}
