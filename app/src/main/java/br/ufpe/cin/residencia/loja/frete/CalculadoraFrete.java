package br.ufpe.cin.residencia.loja.frete;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;

public class CalculadoraFrete {
    public Dinheiro custoFrete(MetodoFrete metodo, Dinheiro subtotal, int pesoTotalEmGramas) {
        Dinheiro base = metodo == MetodoFrete.EXPRESSO
                ? Dinheiro.of("40.00")
                : Dinheiro.of("20.00");

        Dinheiro adicional;
        if (pesoTotalEmGramas <= 1000) {
            adicional = Dinheiro.zero();
        } else if (pesoTotalEmGramas <= 5000) {
            adicional = Dinheiro.of("10.00");
        } else {
            adicional = Dinheiro.of("25.00");
        }

        return base.somar(adicional);
    }
}
