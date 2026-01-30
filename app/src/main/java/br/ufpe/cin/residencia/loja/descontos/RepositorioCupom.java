package br.ufpe.cin.residencia.loja.descontos;

public interface RepositorioCupom {
    Cupom obter(CodigoCupom codigo);

    void marcarComoUsado(CodigoCupom codigo);
}
