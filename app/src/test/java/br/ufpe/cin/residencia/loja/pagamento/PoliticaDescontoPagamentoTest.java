package br.ufpe.cin.residencia.loja.pagamento;

import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.descontos.Cupom;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliticaDescontoPagamentoTest {

    private final PoliticaDescontoPagamento politica =
            new PoliticaDescontoPagamento();

    /* ========= CARTÃO ========= */

    @Test
    void cartaoNuncaDaDesconto() {
        Dinheiro desconto = politica.descontoPorPagamento(
                MetodoPagamento.CARTAO,
                null,
                Dinheiro.of("100.00")
        );

        assertEquals(Dinheiro.zero(), desconto);
    }

    /* ========= PIX ========= */

    @Test
    void pixSemCupomDaCincoPorcento() {
        Dinheiro desconto = politica.descontoPorPagamento(
                MetodoPagamento.PIX,
                null,
                Dinheiro.of("100.00")
        );

        assertEquals(Dinheiro.of("5.00"), desconto);
    }

    @Test
    void pixComCupomPorcento10NaoDaDescontoPagamento() {
        Cupom cupom = cupom(CodigoCupom.PORCENTO10);

        Dinheiro desconto = politica.descontoPorPagamento(
                MetodoPagamento.PIX,
                cupom,
                Dinheiro.of("100.00")
        );

        assertEquals(Dinheiro.zero(), desconto);
    }

    @Test
    void pixComCupomPorcento20VipNaoDaDescontoPagamento() {
        Cupom cupom = cupom(CodigoCupom.PORCENTO20_VIP);

        Dinheiro desconto = politica.descontoPorPagamento(
                MetodoPagamento.PIX,
                cupom,
                Dinheiro.of("100.00")
        );

        assertEquals(Dinheiro.zero(), desconto);
    }

    /* ========= BOLETO ========= */

    @Test
    void boletoSemCupomDaDoisPorcento() {
        Dinheiro desconto = politica.descontoPorPagamento(
                MetodoPagamento.BOLETO,
                null,
                Dinheiro.of("100.00")
        );

        assertEquals(Dinheiro.of("2.00"), desconto);
    }

    @Test
    void boletoComCupomDesconto5NaoDaDescontoPagamento() {
        Cupom cupom = cupom(CodigoCupom.DESCONTO5);

        Dinheiro desconto = politica.descontoPorPagamento(
                MetodoPagamento.BOLETO,
                cupom,
                Dinheiro.of("100.00")
        );

        assertEquals(Dinheiro.zero(), desconto);
    }

    /* ========= BASE NEGATIVA ========= */

    @Test
    void baseNegativaNuncaDaDesconto() {
        Dinheiro desconto = politica.descontoPorPagamento(
                MetodoPagamento.PIX,
                null,
                Dinheiro.of("-10.00")
        );

        assertEquals(Dinheiro.zero(), desconto);
    }

    @Test
    void descontoZeroSePixComCupomPorcento20VIP() {
        Cupom cupom = new Cupom(CodigoCupom.PORCENTO20_VIP, LocalDate.now().plusDays(10), false, false);
        Dinheiro base = Dinheiro.of("100.00");

        Dinheiro desconto = politica.descontoPorPagamento(MetodoPagamento.PIX, cupom, base);

        assertEquals(Dinheiro.zero(), desconto);
    }

    @Test
    void descontoZeroSeBoletoComCupomDesconto5() {
        Cupom cupom = new Cupom(CodigoCupom.DESCONTO5, LocalDate.now().plusDays(10), false, false);
        Dinheiro base = Dinheiro.of("100.00");

        Dinheiro desconto = politica.descontoPorPagamento(MetodoPagamento.BOLETO, cupom, base);

        assertEquals(Dinheiro.zero(), desconto);
    }

    @Test
    void descontoZeroSeCartao() {
        Dinheiro base = Dinheiro.of("100.00");

        Dinheiro desconto = politica.descontoPorPagamento(MetodoPagamento.CARTAO, null, base);

        assertEquals(Dinheiro.zero(), desconto);
    }


    @Test
    void deveRetornarZeroSeCartao() {
        Dinheiro base = Dinheiro.of("100.00");
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.CARTAO, null, base));
    }

    @Test
    void deveRetornarZeroSeBaseNegativa() {
        Dinheiro base = Dinheiro.of("-50.00");
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.PIX, null, base));
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.BOLETO, null, base));
    }

    @Test
    void deveRetornarZeroSePixComCupomPorcento10() {
        Cupom cupom = new Cupom(CodigoCupom.PORCENTO10, LocalDate.now().plusDays(10), false, false);
        Dinheiro base = Dinheiro.of("100.00");
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.PIX, cupom, base));
    }

    @Test
    void deveRetornarZeroSePixComCupomPorcento20VIP() {
        Cupom cupom = new Cupom(CodigoCupom.PORCENTO20_VIP, LocalDate.now().plusDays(10), false, false);
        Dinheiro base = Dinheiro.of("100.00");
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.PIX, cupom, base));
    }

    @Test
    void deveDarDesconto5PorCentoSePixSemCupomBloqueador() {
        Dinheiro base = Dinheiro.of("100.00");
        Dinheiro esperado = base.multiplicar(new BigDecimal("0.05"));
        assertEquals(esperado, politica.descontoPorPagamento(MetodoPagamento.PIX, null, base));

        Cupom cupom = new Cupom(CodigoCupom.DESCONTO5, LocalDate.now().plusDays(10), false, false); // cupom que não bloqueia PIX
        assertEquals(esperado, politica.descontoPorPagamento(MetodoPagamento.PIX, cupom, base));
    }

    @Test
    void deveRetornarZeroSeBoletoComCupomDesconto5() {
        Cupom cupom = new Cupom(CodigoCupom.DESCONTO5, LocalDate.now().plusDays(10), false, false);
        Dinheiro base = Dinheiro.of("100.00");
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.BOLETO, cupom, base));
    }

    @Test
    void deveDarDesconto2PorCentoSeBoletoSemCupomBloqueador() {
        Dinheiro base = Dinheiro.of("100.00");
        Dinheiro esperado = base.multiplicar(new BigDecimal("0.02"));
        assertEquals(esperado, politica.descontoPorPagamento(MetodoPagamento.BOLETO, null, base));

        Cupom cupom = new Cupom(CodigoCupom.PORCENTO10, LocalDate.now().plusDays(10), false,true); // cupom que não bloqueia BOLETO
        assertEquals(esperado, politica.descontoPorPagamento(MetodoPagamento.BOLETO, cupom, base));
    }

    @Test
    void deveCobrirReturnFinal() {
        PoliticaDescontoPagamento politica = new PoliticaDescontoPagamento();
        Dinheiro base = Dinheiro.of("100.00");

        Cupom cupom = new Cupom(CodigoCupom.DESCONTO5,  LocalDate.now().plusDays(10),false, true); // cupom não bloqueia PIX
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.PIX, cupom, Dinheiro.zero()));

        Cupom cupom2 = new Cupom(CodigoCupom.PORCENTO10, LocalDate.now().plusDays(10),false, true); // cupom não bloqueia BOLETO
        assertEquals(base.multiplicar(new BigDecimal("0.02")), politica.descontoPorPagamento(MetodoPagamento.BOLETO, cupom2, base));

        Dinheiro baseNeg = Dinheiro.of("-50.00");
        assertEquals(Dinheiro.zero(), politica.descontoPorPagamento(MetodoPagamento.PIX, null, baseNeg));
    }



    private Cupom cupom(CodigoCupom codigo) {
        return new Cupom(
                codigo,
                LocalDate.now().plusDays(10), // válido
                false,                        // não é uso único
                false                         // não usado
        );
    }
}