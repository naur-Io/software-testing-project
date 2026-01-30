package br.ufpe.cin.residencia.loja.io;

public class ExcecaoImportacaoCarrinho extends RuntimeException {
    public ExcecaoImportacaoCarrinho(String mensagem) {
        super(mensagem);
    }

    public ExcecaoImportacaoCarrinho(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
