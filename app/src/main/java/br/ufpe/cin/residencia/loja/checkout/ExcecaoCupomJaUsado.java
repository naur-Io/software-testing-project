package br.ufpe.cin.residencia.loja.checkout;

public class ExcecaoCupomJaUsado extends RuntimeException {
    public ExcecaoCupomJaUsado(String mensagem) {
        super(mensagem);
    }
}
