package br.ufpe.cin.residencia.loja.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DinheiroTest {

    @Test
    void zeroCriaValorZero() {
        assertEquals(Dinheiro.of("0.00"), Dinheiro.zero());
    }

    @Test
    void somarValores() {
        Dinheiro a = Dinheiro.of("10.00");
        Dinheiro b = Dinheiro.of("5.50");

        assertEquals(Dinheiro.of("15.50"), a.somar(b));
    }

    @Test
    void subtrairPodeGerarValorNegativo() {
        Dinheiro a = Dinheiro.of("10.00");
        Dinheiro b = Dinheiro.of("15.00");

        assertEquals(Dinheiro.of("-5.00"), a.subtrair(b));
    }

    @Test
    void maiorOuIgualTrueEFalse() {
        assertTrue(Dinheiro.of("10.00")
                .maiorOuIgual(Dinheiro.of("10.00")));

        assertFalse(Dinheiro.of("9.99")
                .maiorOuIgual(Dinheiro.of("10.00")));
    }

    @Test
    void ehNegativo() {
        assertTrue(Dinheiro.of("-1.00").ehNegativo());
        assertFalse(Dinheiro.of("0.00").ehNegativo());
    }

    @Test
    void minEMax() {
        Dinheiro menor = Dinheiro.of("5.00");
        Dinheiro maior = Dinheiro.of("10.00");

        assertEquals(menor, menor.min(maior));
        assertEquals(maior, menor.max(maior));
    }

    @Test
    void clampMinZero() {
        assertEquals(
                Dinheiro.zero(),
                Dinheiro.of("-3.00").clampMinZero()
        );

        assertEquals(
                Dinheiro.of("3.00"),
                Dinheiro.of("3.00").clampMinZero()
        );
    }
}
