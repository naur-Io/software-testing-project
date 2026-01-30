package br.ufpe.cin.residencia.loja.descontos;

import java.time.LocalDate;

public class Cupom {
    private final CodigoCupom codigo;
    private final LocalDate validoAte;
    private final boolean usoUnico;
    private boolean usado;

    public Cupom(CodigoCupom codigo, LocalDate validoAte, boolean usoUnico, boolean usado) {
        this.codigo = codigo;
        this.validoAte = validoAte;
        this.usoUnico = usoUnico;
        this.usado = usado;
    }

    public CodigoCupom getCodigo() {
        return codigo;
    }

    public LocalDate getValidoAte() {
        return validoAte;
    }

    public boolean isUsoUnico() {
        return usoUnico;
    }

    public boolean isUsado() {
        return usado;
    }

    public void marcarComoUsado() {
        this.usado = true;
    }
}
