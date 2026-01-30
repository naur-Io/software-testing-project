package br.ufpe.cin.residencia.loja.catalogo;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import br.ufpe.cin.residencia.loja.checkout.ExcecaoSemEstoque;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalogoArquivoCSV implements Catalogo {
    private static final String[] CAMPOS_CABECALHO = {"sku", "nome", "preco", "pesoEmGramas", "estoque"};
    private static final char SEPARADOR_PADRAO = ';';

    private final Path arquivo;
    private char separador = SEPARADOR_PADRAO;
    private final Map<String, Item> itens = new LinkedHashMap<>();
    private final Map<String, Integer> estoque = new LinkedHashMap<>();

    private CatalogoArquivoCSV(Path arquivo) {
        this.arquivo = arquivo;
    }

    public static CatalogoArquivoCSV carregar(Path arquivo) {
        CatalogoArquivoCSV catalogo = new CatalogoArquivoCSV(arquivo);
        if (Files.notExists(arquivo)) {
            catalogo.criarArquivoPadrao();
        }
        catalogo.carregarDoArquivo();
        return catalogo;
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
        salvar();
    }

    @Override
    public void reporEstoque(String sku, int quantidade) {
        validarMovimentacao(sku, quantidade);
        int atual = estoque.getOrDefault(sku, 0);
        estoque.put(sku, atual + quantidade);
        salvar();
    }

    public Map<String, Item> itens() {
        return Collections.unmodifiableMap(itens);
    }

    public void baixarEstoque(Map<String, Integer> itensVendidos) {
        for (Map.Entry<String, Integer> entrada : itensVendidos.entrySet()) {
            String sku = entrada.getKey();
            int quantidade = entrada.getValue();
            baixarEstoque(sku, quantidade);
        }
    }

    public void salvar() {
        StringBuilder conteudo = new StringBuilder();
        conteudo.append(String.join(String.valueOf(separador), CAMPOS_CABECALHO))
                .append(System.lineSeparator());
        for (Map.Entry<String, Item> entrada : itens.entrySet()) {
            String sku = entrada.getKey();
            Item item = entrada.getValue();
            int qtd = estoque.getOrDefault(sku, 0);
            conteudo.append(sku).append(separador)
                    .append(item.getNome()).append(separador)
                    .append(item.getPrecoUnitario()).append(separador)
                    .append(item.getPesoEmGramas()).append(separador)
                    .append(qtd)
                    .append(System.lineSeparator());
        }

        try {
            Path parent = arquivo.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(arquivo, conteudo.toString());
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao salvar catalogo: " + arquivo, e);
        }
    }

    private void carregarDoArquivo() {
        itens.clear();
        estoque.clear();
        List<String> linhas;
        try {
            linhas = Files.readAllLines(arquivo);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler catalogo: " + arquivo, e);
        }

        boolean separadorDefinido = false;
        for (String linha : linhas) {
            String limpa = linha.trim();
            if (limpa.isEmpty()) {
                continue;
            }
            if (!separadorDefinido) {
                separador = descobrirSeparador(limpa);
                separadorDefinido = true;
            }
            if (ehCabecalho(limpa)) {
                continue;
            }
            String[] partes = limpa.split(String.valueOf(separador));
            if (partes.length != 5) {
                throw new IllegalStateException("Linha invalida no catalogo: " + linha);
            }
            String sku = partes[0].trim();
            String nome = partes[1].trim();
            String preco = partes[2].trim();
            int peso = Integer.parseInt(partes[3].trim());
            int qtd = Integer.parseInt(partes[4].trim());
            Item item = new Item(sku, nome, Dinheiro.of(preco), peso);
            itens.put(sku, item);
            estoque.put(sku, qtd);
        }
    }

    private void criarArquivoPadrao() {
        CatalogoEmMemoria padrao = CatalogoEmMemoria.catalogoPadrao();
        itens.clear();
        estoque.clear();
        itens.putAll(padrao.itens());
        estoque.putAll(padrao.estoque());
        salvar();
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

    private boolean ehCabecalho(String linha) {
        String[] campos = linha.split(String.valueOf(separador));
        if (campos.length != CAMPOS_CABECALHO.length) {
            return false;
        }
        for (int i = 0; i < campos.length; i++) {
            if (!CAMPOS_CABECALHO[i].equalsIgnoreCase(campos[i].trim())) {
                return false;
            }
        }
        return true;
    }

    private char descobrirSeparador(String linha) {
        if (linha.indexOf(';') >= 0) {
            return ';';
        }
        if (linha.indexOf(',') >= 0) {
            return ',';
        }
        throw new IllegalStateException("Separador invalido no catalogo: " + linha);
    }
}
