package br.ufpe.cin.residencia.loja.cli;

import br.ufpe.cin.residencia.loja.catalogo.CatalogoArquivoCSV;
import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void deveRodarMenuSairSemErro() {
        // Simula a entrada do usuário: "14" para sair imediatamente
        String input = "14\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Captura saída do console
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Rodar main
        Main.main(new String[]{});

        String saida = out.toString();
        // Verifica se o menu inicial foi impresso
        assertTrue(saida.contains("==== Loja CLI ===="));
        assertTrue(saida.contains("Sair"));
    }



    @Test
    void deveEscolherFreteExpresso() {
        String input = "5\nEXPRESSO\n14\n"; // muda frete e sai
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Main.main(new String[]{});

        String saida = out.toString();
        assertTrue(saida.contains("Escolher frete"));
    }

    @Test
    void deveFinalizarCompraVazia() {
        String input = "8\n14\n"; // tenta finalizar compra com carrinho vazio
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Main.main(new String[]{});

        String saida = out.toString();
        assertTrue(saida.contains("Carrinho vazio. Adicione itens antes de finalizar."));
    }

}
