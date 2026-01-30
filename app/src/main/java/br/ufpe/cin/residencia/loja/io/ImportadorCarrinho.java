package br.ufpe.cin.residencia.loja.io;

import br.ufpe.cin.residencia.loja.catalogo.Catalogo;
import br.ufpe.cin.residencia.loja.dominio.Carrinho;
import br.ufpe.cin.residencia.loja.dominio.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportadorCarrinho {
    private static final String CABECALHO = "SKU;QTD";

    public Carrinho importar(Path arquivo, Catalogo catalogo) {
        if (arquivo == null) {
            throw new ExcecaoImportacaoCarrinho("Arquivo de importação é obrigatório.");
        }
        if (catalogo == null) {
            throw new ExcecaoImportacaoCarrinho("Catálogo é obrigatório para importação.");
        }
        if (Files.notExists(arquivo)) {
            throw new ExcecaoImportacaoCarrinho("Arquivo não encontrado: " + arquivo);
        }

        List<String> linhas;
        try {
            linhas = Files.readAllLines(arquivo);
        } catch (IOException e) {
            throw new ExcecaoImportacaoCarrinho("Falha ao ler arquivo: " + arquivo, e);
        }

        Map<String, Integer> totais = new LinkedHashMap<>();
        for (String linha : linhas) {
            String limpa = linha.trim();
            if (limpa.isEmpty() || limpa.equalsIgnoreCase(CABECALHO)) {
                continue;
            }
            String[] partes = limpa.split(";");
            if (partes.length != 2) {
                throw new ExcecaoImportacaoCarrinho("Linha inválida no carrinho: " + linha);
            }
            String sku = partes[0].trim();
            String quantidadeTexto = partes[1].trim();
            int quantidade;
            try {
                quantidade = Integer.parseInt(quantidadeTexto);
            } catch (NumberFormatException e) {
                throw new ExcecaoImportacaoCarrinho("Quantidade inválida para SKU: " + sku);
            }
            if (quantidade <= 0) {
                throw new ExcecaoImportacaoCarrinho("Quantidade inválida para SKU: " + sku);
            }
            Item item = catalogo.obterPorSku(sku);
            if (item == null) {
                throw new ExcecaoImportacaoCarrinho("SKU inexistente no catálogo: " + sku);
            }
            totais.merge(sku, quantidade, Integer::sum);
        }

        if (totais.isEmpty()) {
            throw new ExcecaoImportacaoCarrinho("Carrinho importado está vazio.");
        }

        Carrinho carrinho = new Carrinho();
        for (Map.Entry<String, Integer> entrada : totais.entrySet()) {
            Item item = catalogo.obterPorSku(entrada.getKey());
            carrinho.adicionar(item, entrada.getValue());
        }
        return carrinho;
    }
}
