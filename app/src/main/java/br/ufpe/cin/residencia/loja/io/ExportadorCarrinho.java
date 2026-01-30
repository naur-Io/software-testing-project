package br.ufpe.cin.residencia.loja.io;

import br.ufpe.cin.residencia.loja.dominio.Carrinho;
import br.ufpe.cin.residencia.loja.dominio.LinhaCarrinho;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExportadorCarrinho {
    private static final String CABECALHO = "SKU;QTD";

    public void exportar(Carrinho carrinho, Path arquivo) {
        if (carrinho == null || carrinho.vazio()) {
            throw new IllegalArgumentException("Carrinho vazio não pode ser exportado.");
        }
        if (arquivo == null) {
            throw new IllegalArgumentException("Arquivo de exportação é obrigatório.");
        }

        Map<String, Integer> totais = new LinkedHashMap<>();
        for (LinhaCarrinho linha : carrinho.linhas()) {
            totais.merge(linha.getItem().getSku(), linha.getQuantidade(), Integer::sum);
        }

        StringBuilder conteudo = new StringBuilder();
        conteudo.append(CABECALHO).append(System.lineSeparator());
        for (Map.Entry<String, Integer> entrada : totais.entrySet()) {
            conteudo.append(entrada.getKey())
                    .append(";")
                    .append(entrada.getValue())
                    .append(System.lineSeparator());
        }

        try {
            Path parent = arquivo.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(arquivo, conteudo.toString());
        } catch (IOException e) {
            throw new ExcecaoImportacaoCarrinho("Falha ao exportar carrinho: " + arquivo, e);
        }
    }
}
