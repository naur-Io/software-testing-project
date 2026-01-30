package br.ufpe.cin.residencia.loja.pedidos;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;

import java.math.BigDecimal;

public class ItemPedido {
    private final String sku;
    private final String nome;
    private final Dinheiro precoUnitario;
    private final int pesoEmGramas;
    private final int quantidade;

    public ItemPedido(String sku, String nome, Dinheiro precoUnitario, int pesoEmGramas, int quantidade) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU do item do pedido não pode ser vazio.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do item do pedido não pode ser vazio.");
        }
        if (precoUnitario == null) {
            throw new IllegalArgumentException("Preço unitário do item do pedido não pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade do item do pedido deve ser maior que zero.");
        }
        this.sku = sku;
        this.nome = nome;
        this.precoUnitario = precoUnitario;
        this.pesoEmGramas = pesoEmGramas;
        this.quantidade = quantidade;
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

    public int getQuantidade() {
        return quantidade;
    }

    public Dinheiro total() {
        return precoUnitario.multiplicar(new BigDecimal(quantidade));
    }

    public int pesoTotalEmGramas() {
        return pesoEmGramas * quantidade;
    }
}
