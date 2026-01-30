package br.ufpe.cin.residencia.loja.checkout;

public class ExcecaoSemEstoque extends RuntimeException {
    public ExcecaoSemEstoque(String mensagem) {
        super(mensagem);
    }
}
