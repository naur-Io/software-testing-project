package br.ufpe.cin.residencia.loja.checkout;

public class ExcecaoTotalInvalido extends RuntimeException {
    public ExcecaoTotalInvalido(String mensagem) {
        super(mensagem);
    }
}
