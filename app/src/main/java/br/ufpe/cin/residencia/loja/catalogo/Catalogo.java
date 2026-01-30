package br.ufpe.cin.residencia.loja.catalogo;

import br.ufpe.cin.residencia.loja.dominio.Item;

public interface Catalogo {
    Item obterPorSku(String sku);

    int estoquePara(String sku);

    void baixarEstoque(String sku, int quantidade);

    void reporEstoque(String sku, int quantidade);
}
