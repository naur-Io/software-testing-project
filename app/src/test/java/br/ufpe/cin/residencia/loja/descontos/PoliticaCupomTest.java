package br.ufpe.cin.residencia.loja.descontos;

import br.ufpe.cin.residencia.loja.checkout.ExcecaoCupomExpirado;
import br.ufpe.cin.residencia.loja.checkout.ExcecaoCupomInvalido;
import br.ufpe.cin.residencia.loja.checkout.ExcecaoCupomJaUsado;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaCupomTest {

    private final PoliticaCupom politica = new PoliticaCupom();

    /* ---------- descontoNoSubtotal ---------- */

    @Test
    void cupomNuloNaoDaDesconto() {
        Dinheiro desconto = politica.descontoNoSubtotal(null, Dinheiro.of("100.00"));

        assertEquals(Dinheiro.zero(), desconto);
    }

    @Test
    void cupomPorcento10ComSubtotalSuficiente() {
        Cupom cupom = new Cupom(
                CodigoCupom.PORCENTO10,
                LocalDate.now().plusDays(1),
                false,
                false
        );

        Dinheiro desconto = politica.descontoNoSubtotal(cupom, Dinheiro.of("200.00"));

        assertEquals(Dinheiro.of("20.00"), desconto);
    }

    @Test
    void cupomPorcento10AbaixoDoMinimoNaoDaDesconto() {
        Cupom cupom = new Cupom(
                CodigoCupom.PORCENTO10,
                LocalDate.now().plusDays(1),
                false,
                false
        );

        assertEquals(
                Dinheiro.zero(),
                politica.descontoNoSubtotal(cupom, Dinheiro.of("49.99"))
        );
    }

    @Test
    void desconto5SoAplicaAcimaDoMinimo() {
        Cupom cupom = new Cupom(
                CodigoCupom.DESCONTO5,
                LocalDate.now().plusDays(1),
                false,
                false
        );

        assertEquals(Dinheiro.of("5.00"),
                politica.descontoNoSubtotal(cupom, Dinheiro.of("30.00")));

        assertEquals(Dinheiro.zero(),
                politica.descontoNoSubtotal(cupom, Dinheiro.of("29.99")));
    }

    @Test
    void cupomVipTemLimiteMaximo() {
        Cupom cupom = new Cupom(
                CodigoCupom.PORCENTO20_VIP,
                LocalDate.now().plusDays(1),
                false,
                false
        );

        Dinheiro desconto = politica.descontoNoSubtotal(cupom, Dinheiro.of("1000.00"));

        assertEquals(Dinheiro.of("120.00"), desconto);
    }

    @Test
    void desconto50SoAplicaAcimaDe1000() {
        Cupom cupom = new Cupom(
                CodigoCupom.DESCONTO50_ACIMA_1000,
                LocalDate.now().plusDays(1),
                true,
                false
        );

        assertEquals(Dinheiro.of("50.00"),
                politica.descontoNoSubtotal(cupom, Dinheiro.of("1000.00")));

        assertEquals(Dinheiro.zero(),
                politica.descontoNoSubtotal(cupom, Dinheiro.of("999.99")));
    }

    /* ---------- freteDeveSerGratis ---------- */

    @Test
    void freteGratisSoComCupomFreteGratisPadraoESubtotalMinimo() {
        Cupom cupom = new Cupom(
                CodigoCupom.FRETE_GRATIS,
                LocalDate.now().plusDays(1),
                true,
                false
        );

        assertTrue(politica.freteDeveSerGratis(
                cupom,
                MetodoFrete.PADRAO,
                Dinheiro.of("150.00")
        ));

        assertFalse(politica.freteDeveSerGratis(
                cupom,
                MetodoFrete.EXPRESSO,
                Dinheiro.of("150.00")
        ));
    }

    @Test
    void cupomNuloNaoGeraFreteGratis() {
        assertFalse(
                politica.freteDeveSerGratis(
                        null,
                        MetodoFrete.PADRAO,
                        Dinheiro.of("200.00")
                )
        );
    }

    /* ---------- validarCupom ---------- */

    @Test
    void validarCupomNuloLancaExcecao() {
        assertThrows(
                ExcecaoCupomInvalido.class,
                () -> politica.validarCupom(null)
        );
    }

    @Test
    void validarCupomExpiradoLancaExcecao() {
        Cupom cupom = new Cupom(
                CodigoCupom.DESCONTO5,
                LocalDate.now().minusDays(1),
                false,
                false
        );

        assertThrows(
                ExcecaoCupomExpirado.class,
                () -> politica.validarCupom(cupom)
        );
    }

    @Test
    void cupomUsoUnicoJaUsadoLancaExcecao() {
        Cupom cupom = new Cupom(
                CodigoCupom.FRETE_GRATIS,
                LocalDate.now().plusDays(1),
                true,
                true
        );

        assertThrows(
                ExcecaoCupomJaUsado.class,
                () -> politica.validarCupom(cupom)
        );
    }

    @Test
    void cupomValidoNaoLancaExcecao() {
        Cupom cupom = new Cupom(
                CodigoCupom.DESCONTO5,
                LocalDate.now().plusDays(1),
                false,
                false
        );

        assertDoesNotThrow(() -> politica.validarCupom(cupom));
    }
}
