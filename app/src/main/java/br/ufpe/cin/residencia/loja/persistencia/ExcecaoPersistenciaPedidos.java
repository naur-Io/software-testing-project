package br.ufpe.cin.residencia.loja.persistencia;

public class ExcecaoPersistenciaPedidos extends RuntimeException {
    public ExcecaoPersistenciaPedidos(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public ExcecaoPersistenciaPedidos(String mensagem) {
        super(mensagem);
    }
}
