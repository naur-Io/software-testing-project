package br.ufpe.cin.residencia.loja.frete;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculadoraFreteTest {

    @Test
    void freteNormalAte1kg() {
        CalculadoraFrete calculadoraFrete = new CalculadoraFrete();
        Dinheiro custo = calculadoraFrete.custoFrete(MetodoFrete.PADRAO, Dinheiro.of("50.00"), 800);
        assert custo.equals(Dinheiro.of("20.00"));
    }

    @Test
    void freteNormalEntreUmEQuatroQuilosTemAdicionalDez() {
        CalculadoraFrete calc = new CalculadoraFrete();

        Dinheiro custo = calc.custoFrete(
                MetodoFrete.PADRAO,
                Dinheiro.of("100.00"),
                3000
        );

        assertEquals(Dinheiro.of("30.00"), custo);
    }

    @Test
    void freteNormalAcimaDeCincoQuilosTemAdicionalVinteCinco() {
        CalculadoraFrete calc = new CalculadoraFrete();
        Dinheiro custo = calc.custoFrete(
                MetodoFrete.PADRAO,
                Dinheiro.of("100.00"),
                6000
        );

        assertEquals(Dinheiro.of("45.00"), custo);
    }

    @Test
    void freteExpressoAteUmQuiloNaoTemAdicional(){
        CalculadoraFrete calc = new CalculadoraFrete();
        Dinheiro custo = calc.custoFrete(
                MetodoFrete.EXPRESSO,
                Dinheiro.of("200.00"),
                900
        );

        assertEquals(Dinheiro.of("40.00"), custo);
    }

}
