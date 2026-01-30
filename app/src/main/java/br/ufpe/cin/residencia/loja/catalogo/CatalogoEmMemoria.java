package br.ufpe.cin.residencia.loja.catalogo;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import br.ufpe.cin.residencia.loja.checkout.ExcecaoSemEstoque;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CatalogoEmMemoria implements Catalogo {
    private final Map<String, Item> itens = new HashMap<>();
    private final Map<String, Integer> estoque = new HashMap<>();

    public void addItem(Item item, int quantidadeEmEstoque) {
        itens.put(item.getSku(), item);
        estoque.put(item.getSku(), quantidadeEmEstoque);
    }

    @Override
    public Item obterPorSku(String sku) {
        return itens.get(sku);
    }

    @Override
    public int estoquePara(String sku) {
        return estoque.getOrDefault(sku, 0);
    }

    @Override
    public void baixarEstoque(String sku, int quantidade) {
        validarMovimentacao(sku, quantidade);
        int atual = estoque.getOrDefault(sku, 0);
        int novoValor = atual - quantidade;
        if (novoValor < 0) {
            throw new ExcecaoSemEstoque("Sem estoque para SKU: " + sku);
        }
        estoque.put(sku, novoValor);
    }

    @Override
    public void reporEstoque(String sku, int quantidade) {
        validarMovimentacao(sku, quantidade);
        int atual = estoque.getOrDefault(sku, 0);
        estoque.put(sku, atual + quantidade);
    }

    public Map<String, Item> itens() {
        return Collections.unmodifiableMap(itens);
    }

    public Map<String, Integer> estoque() {
        return Collections.unmodifiableMap(estoque);
    }

    public static CatalogoEmMemoria catalogoPadrao() {
        CatalogoEmMemoria catalogo = new CatalogoEmMemoria();
        catalogo.addItem(new Item("KB", "Teclado", Dinheiro.of("120.00"), 800), 10);
        catalogo.addItem(new Item("MS", "Mouse", Dinheiro.of("80.00"), 200), 10);
        catalogo.addItem(new Item("HD", "Headset", Dinheiro.of("200.00"), 400), 5);
        catalogo.addItem(new Item("CP", "Cabo USB", Dinheiro.of("25.00"), 50), 50);
        catalogo.addItem(new Item("MN", "Monitor", Dinheiro.of("900.00"), 3500), 2);
        return catalogo;
    }

    private void validarMovimentacao(String sku, int quantidade) {
        if (sku == null || sku.isBlank()) {
            throw new ExcecaoSemEstoque("SKU inválido.");
        }
        if (quantidade <= 0) {
            throw new ExcecaoSemEstoque("Quantidade inválida para SKU: " + sku);
        }
        if (!itens.containsKey(sku)) {
            throw new ExcecaoSemEstoque("SKU inexistente no catálogo: " + sku);
        }
    }
}
