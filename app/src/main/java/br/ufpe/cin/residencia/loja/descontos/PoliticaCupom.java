package br.ufpe.cin.residencia.loja.descontos;

import br.ufpe.cin.residencia.loja.checkout.ExcecaoCupomExpirado;
import br.ufpe.cin.residencia.loja.checkout.ExcecaoCupomJaUsado;
import br.ufpe.cin.residencia.loja.checkout.ExcecaoCupomInvalido;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PoliticaCupom {
    public Dinheiro descontoNoSubtotal(Cupom cupom, Dinheiro subtotal) {
        if (cupom == null) {
            return Dinheiro.zero();
        }

        switch (cupom.getCodigo()) {
            case PORCENTO10:
                if (subtotal.maiorOuIgual(Dinheiro.of("50.00"))) {
                    Dinheiro bruto = subtotal.multiplicar(new BigDecimal("0.10"));
                    return bruto.min(Dinheiro.of("50.00"));
                }
                return Dinheiro.zero();
            case DESCONTO5:
                return subtotal.maiorOuIgual(Dinheiro.of("30.00")) ? Dinheiro.of("5.00") : Dinheiro.zero();
            case PORCENTO20_VIP:
                if (subtotal.maiorOuIgual(Dinheiro.of("300.00"))) {
                    Dinheiro bruto = subtotal.multiplicar(new BigDecimal("0.20"));
                    return bruto.min(Dinheiro.of("120.00"));
                }
                return Dinheiro.zero();
            case DESCONTO50_ACIMA_1000:
                return subtotal.maiorOuIgual(Dinheiro.of("1000.00")) ? Dinheiro.of("50.00") : Dinheiro.zero();
            case FRETE_GRATIS:
            default:
                return Dinheiro.zero();
        }
    }

    public boolean freteDeveSerGratis(Cupom cupom, MetodoFrete metodo, Dinheiro subtotal) {
        if (cupom == null) {
            return false;
        }
        return cupom.getCodigo() == CodigoCupom.FRETE_GRATIS
                && metodo == MetodoFrete.PADRAO
                && subtotal.maiorOuIgual(Dinheiro.of("150.00"));
    }

    public void validarCupom(Cupom cupom) {
        if (cupom == null) {
            throw new ExcecaoCupomInvalido("Cupom inexistente.");
        }
        LocalDate hoje = LocalDate.now();
        if (cupom.getValidoAte().isBefore(hoje)) {
            throw new ExcecaoCupomExpirado("Cupom expirado: " + cupom.getCodigo());
        }
        if (cupom.isUsoUnico() && cupom.isUsado()) {
            throw new ExcecaoCupomJaUsado("Cupom já usado: " + cupom.getCodigo());
        }
    }
}
