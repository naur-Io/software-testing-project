package br.ufpe.cin.residencia.loja.checkout;

public class ExcecaoCupomExpirado extends RuntimeException {
    public ExcecaoCupomExpirado(String mensagem) {
        super(mensagem);
    }
}
