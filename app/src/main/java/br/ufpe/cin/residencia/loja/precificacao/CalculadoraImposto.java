package br.ufpe.cin.residencia.loja.precificacao;

import br.ufpe.cin.residencia.loja.dominio.Dinheiro;

import java.math.BigDecimal;

public class CalculadoraImposto {
    private static final Dinheiro MINIMO = Dinheiro.of("0.01");

    public Dinheiro calcularImposto(Dinheiro base) {
        if (base.ehNegativo() || base.equals(Dinheiro.zero())) {
            return Dinheiro.zero();
        }
        Dinheiro imposto = base.multiplicar(new BigDecimal("0.08"));
        return imposto.max(MINIMO);
    }
}
