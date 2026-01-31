package br.ufpe.cin.residencia.loja.precificacao;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ReciboTest {

    @Test
    void construtor_deve_atribuir_campos(){
        Dinheiro subtotal = Dinheiro.of("100.00");
        Dinheiro descontoCupom = Dinheiro.of("10.00");
        Dinheiro descontoPagamento = Dinheiro.of("5.00");
        Dinheiro imposto = Dinheiro.of("15.00");
        Dinheiro frete = Dinheiro.of("20.00");
        Dinheiro total = Dinheiro.of("120.00");

        Recibo recibo = new Recibo(subtotal, descontoCupom, descontoPagamento, imposto, frete, total);

        assertEquals(subtotal, recibo.subtotal);
        assertEquals(descontoCupom, recibo.descontoCupom);
        assertEquals(descontoPagamento, recibo.descontoPagamento);
        assertEquals(imposto, recibo.imposto);
        assertEquals(frete, recibo.frete);
        assertEquals(total, recibo.total);
    }

    @Test
    void deveCriarReciboComValoresInformados() {
        Recibo recibo = new Recibo(
                Dinheiro.of("100.00"),
                Dinheiro.of("10.00"),
                Dinheiro.of("5.00"),
                Dinheiro.of("18.00"),
                Dinheiro.of("20.00"),
                Dinheiro.of("123.00")
        );

        assertEquals(Dinheiro.of("100.00"), recibo.subtotal);
        assertEquals(Dinheiro.of("10.00"), recibo.descontoCupom);
        assertEquals(Dinheiro.of("5.00"), recibo.descontoPagamento);
        assertEquals(Dinheiro.of("18.00"), recibo.imposto);
        assertEquals(Dinheiro.of("20.00"), recibo.frete);
        assertEquals(Dinheiro.of("123.00"), recibo.total);
    }

    @Test
    void toStringDeveConterTodosOsValoresFormatados() {
        Recibo recibo = new Recibo(
                Dinheiro.of("100.00"),
                Dinheiro.of("10.00"),
                Dinheiro.of("5.00"),
                Dinheiro.of("18.00"),
                Dinheiro.of("20.00"),
                Dinheiro.of("123.00")
        );

        String texto = recibo.toString();

        assertTrue(texto.contains("Recibo"));
        assertTrue(texto.contains("Subtotal: R$ 100.00"));
        assertTrue(texto.contains("Desconto (cupom): R$ 10.00"));
        assertTrue(texto.contains("Desconto (pagamento): R$ 5.00"));
        assertTrue(texto.contains("Imposto: R$ 18.00"));
        assertTrue(texto.contains("Frete: R$ 20.00"));
        assertTrue(texto.contains("Total: R$ 123.00"));
    }
}

