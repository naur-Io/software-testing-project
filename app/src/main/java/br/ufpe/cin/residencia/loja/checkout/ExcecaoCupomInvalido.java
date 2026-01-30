package br.ufpe.cin.residencia.loja.checkout;

public class ExcecaoCupomInvalido extends RuntimeException {
    public ExcecaoCupomInvalido(String mensagem) {
        super(mensagem);
    }
}
