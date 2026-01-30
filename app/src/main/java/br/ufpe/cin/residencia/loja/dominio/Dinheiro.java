package br.ufpe.cin.residencia.loja.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Dinheiro {
    private static final int ESCALA = 2;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_UP;

    private final BigDecimal valor;

    private Dinheiro(BigDecimal valor) {
        this.valor = valor.setScale(ESCALA, ARREDONDAMENTO);
    }

    public static Dinheiro zero() {
        return new Dinheiro(BigDecimal.ZERO);
    }

    public static Dinheiro of(String valor) {
        return new Dinheiro(new BigDecimal(valor));
    }

    public static Dinheiro of(BigDecimal valor) {
        return new Dinheiro(valor);
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(this.valor.add(outro.valor));
    }

    public Dinheiro subtrair(Dinheiro outro) {
        return new Dinheiro(this.valor.subtract(outro.valor));
    }

    public Dinheiro multiplicar(BigDecimal fator) {
        return new Dinheiro(this.valor.multiply(fator));
    }

    public boolean maiorOuIgual(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) >= 0;
    }

    public boolean ehNegativo() {
        return this.valor.compareTo(BigDecimal.ZERO) < 0;
    }

    public Dinheiro min(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) <= 0 ? this : outro;
    }

    public Dinheiro max(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) >= 0 ? this : outro;
    }

    public Dinheiro clampMinZero() {
        return ehNegativo() ? zero() : this;
    }

    public BigDecimal comoBigDecimal() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dinheiro)) {
            return false;
        }
        Dinheiro dinheiro = (Dinheiro) o;
        return valor.equals(dinheiro.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
