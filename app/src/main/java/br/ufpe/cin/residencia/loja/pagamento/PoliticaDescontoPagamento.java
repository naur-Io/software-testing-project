package br.ufpe.cin.residencia.loja.pagamento;

import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.descontos.Cupom;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;

import java.math.BigDecimal;

public class PoliticaDescontoPagamento {
    public Dinheiro descontoPorPagamento(MetodoPagamento metodo, Cupom cupom, Dinheiro baseParaDesconto) {
        if (metodo == MetodoPagamento.CARTAO || baseParaDesconto.ehNegativo()) {
            return Dinheiro.zero();
        }

        if (metodo == MetodoPagamento.PIX) {
            if (cupom != null && (cupom.getCodigo() == CodigoCupom.PORCENTO10
                    || cupom.getCodigo() == CodigoCupom.PORCENTO20_VIP)) {
                return Dinheiro.zero();
            }
            return baseParaDesconto.multiplicar(new BigDecimal("0.05"));
        }

        if (metodo == MetodoPagamento.BOLETO) {
            if (cupom != null && cupom.getCodigo() == CodigoCupom.DESCONTO5) {
                return Dinheiro.zero();
            }
            return baseParaDesconto.multiplicar(new BigDecimal("0.02"));
        }

        return Dinheiro.zero();
    }
}
