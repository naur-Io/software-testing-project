package br.ufpe.cin.residencia.loja.dominio;

public class Item {
    private final String sku;
    private final String nome;
    private final Dinheiro precoUnitario;
    private final int pesoEmGramas;

    public Item(String sku, String nome, Dinheiro precoUnitario, int pesoEmGramas) {
        this.sku = sku;
        this.nome = nome;
        this.precoUnitario = precoUnitario;
        this.pesoEmGramas = pesoEmGramas;
    }

    public String getSku() {
        return sku;
    }

    public String getNome() {
        return nome;
    }

    public Dinheiro getPrecoUnitario() {
        return precoUnitario;
    }

    public int getPesoEmGramas() {
        return pesoEmGramas;
    }
}
