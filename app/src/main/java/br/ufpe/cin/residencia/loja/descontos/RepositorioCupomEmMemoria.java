package br.ufpe.cin.residencia.loja.descontos;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RepositorioCupomEmMemoria implements RepositorioCupom {
    private final Map<CodigoCupom, Cupom> cupons = new HashMap<>();

    public RepositorioCupomEmMemoria() {
        LocalDate hoje = LocalDate.now();
        cupons.put(CodigoCupom.PORCENTO10, new Cupom(CodigoCupom.PORCENTO10, hoje.plusDays(365), false, false));
        cupons.put(CodigoCupom.DESCONTO5, new Cupom(CodigoCupom.DESCONTO5, hoje.plusDays(365), false, false));
        cupons.put(CodigoCupom.FRETE_GRATIS, new Cupom(CodigoCupom.FRETE_GRATIS, hoje.plusDays(365), true, false));
        cupons.put(CodigoCupom.PORCENTO20_VIP, new Cupom(CodigoCupom.PORCENTO20_VIP, hoje.minusDays(1), false, false));
        cupons.put(CodigoCupom.DESCONTO50_ACIMA_1000, new Cupom(CodigoCupom.DESCONTO50_ACIMA_1000, hoje.plusDays(30), true, false));
    }

    @Override
    public Cupom obter(CodigoCupom codigo) {
        return cupons.get(codigo);
    }

    @Override
    public void marcarComoUsado(CodigoCupom codigo) {
        Cupom cupom = cupons.get(codigo);
        if (cupom != null && cupom.isUsoUnico()) {
            cupom.marcarComoUsado();
        }
    }
}
