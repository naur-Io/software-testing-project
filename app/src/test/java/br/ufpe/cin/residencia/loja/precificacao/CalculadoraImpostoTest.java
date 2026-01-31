package br.ufpe.cin.residencia.loja.precificacao;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CalculadoraImpostoTest {

    @BeforeEach
    void setUp() {
        CalculadoraImposto calculadoraImposto = new CalculadoraImposto();
    }

    @Test
    void impostoDeveSerZeroParaBaseNegativa() {
        CalculadoraImposto calculadoraImposto = new CalculadoraImposto();
        Dinheiro baseNegativa = Dinheiro.of("-10.00");
        Dinheiro imposto = calculadoraImposto.calcularImposto(baseNegativa);
        assert imposto.equals(Dinheiro.zero());
    }

    @Test
    void deveCalcularImpostoParaValorAlto(){
        CalculadoraImposto calculadoraImposto = new CalculadoraImposto();
        Dinheiro baseAlta = Dinheiro.of("200.00");
        Dinheiro imposto = calculadoraImposto.calcularImposto(baseAlta);
        assert imposto.equals(Dinheiro.of("16.00"));
    }

    @Test
    void deveCalcularImpostoParaValorBaixo(){
        CalculadoraImposto calculadoraImposto = new CalculadoraImposto();
        Dinheiro baseBaixa = Dinheiro.of("5.00");
        Dinheiro imposto = calculadoraImposto.calcularImposto(baseBaixa);
        assert imposto.equals(Dinheiro.of("0.40"));
    }

    @Test
    void deveCalcularImpostoParaValorMinimo(){
        CalculadoraImposto calculadoraImposto = new CalculadoraImposto();
        Dinheiro baseMinima = Dinheiro.of("0.10");
        Dinheiro imposto = calculadoraImposto.calcularImposto(baseMinima);
        assert imposto.equals(Dinheiro.of("0.01"));
    }
}
