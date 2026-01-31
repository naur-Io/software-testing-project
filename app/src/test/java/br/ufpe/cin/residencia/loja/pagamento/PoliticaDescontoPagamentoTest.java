package br.ufpe.cin.residencia.loja.pagamento;

import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.descontos.Cupom;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import org.junit.jupiter.api.Test;
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

    /* ========= HELPERS ========= */

    private Cupom cupom(CodigoCupom codigo) {
        return new Cupom(
                codigo,
                LocalDate.now().plusDays(10), // válido
                false,                        // não é uso único
                false                         // não usado
        );
    }
}