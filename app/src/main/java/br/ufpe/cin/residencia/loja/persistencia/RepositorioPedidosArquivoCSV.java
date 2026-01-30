package br.ufpe.cin.residencia.loja.persistencia;

import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;
import br.ufpe.cin.residencia.loja.pedidos.ExcecaoPedidoNaoEncontrado;
import br.ufpe.cin.residencia.loja.pedidos.ItemPedido;
import br.ufpe.cin.residencia.loja.pedidos.Pedido;
import br.ufpe.cin.residencia.loja.pedidos.StatusPedido;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositorioPedidosArquivoCSV implements RepositorioPedidos {
    private static final String CABECALHO = "id;criadoEm;reembolsadoEm;status;metodoFrete;metodoPagamento;"
            + "codigoCupom;subtotal;descontoCupom;descontoPagamento;imposto;frete;total;itens";
    private static final String SEPARADOR_CAMPO = ";";
    private static final String SEPARADOR_ITENS = "\\|";
    private static final String SEPARADOR_ITENS_ESCRITA = "|";
    private static final String SEPARADOR_ITEM_CAMPO = ",";

    private final Path arquivo;
    private final Map<String, Pedido> pedidos = new LinkedHashMap<>();

    public RepositorioPedidosArquivoCSV() {
        this(Path.of("dados", "pedidos.csv"));
    }

    public RepositorioPedidosArquivoCSV(Path arquivo) {
        this.arquivo = arquivo;
        carregar();
    }

    @Override
    public void salvar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo.");
        }
        pedidos.put(pedido.getId(), pedido);
        persistir();
    }

    @Override
    public Pedido obterPorId(String id) {
        Pedido pedido = pedidos.get(id);
        if (pedido == null) {
            throw new ExcecaoPedidoNaoEncontrado("Pedido não encontrado: " + id);
        }
        return pedido;
    }

    @Override
    public List<Pedido> listarTodos() {
        return pedidos.values().stream()
                .sorted(Comparator.comparing(Pedido::getCriadoEm).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void atualizar(Pedido pedido) {
        salvar(pedido);
    }

    private void carregar() {
        pedidos.clear();
        if (Files.notExists(arquivo)) {
            criarArquivoVazio();
            return;
        }
        List<String> linhas;
        try {
            linhas = Files.readAllLines(arquivo);
        } catch (IOException e) {
            throw new ExcecaoPersistenciaPedidos("Falha ao ler pedidos: " + arquivo, e);
        }
        for (String linha : linhas) {
            String limpa = linha.trim();
            if (limpa.isEmpty() || limpa.equalsIgnoreCase(CABECALHO)) {
                continue;
            }
            Pedido pedido = parsePedido(limpa);
            pedidos.put(pedido.getId(), pedido);
        }
    }

    private void criarArquivoVazio() {
        try {
            Path parent = arquivo.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(arquivo, CABECALHO + System.lineSeparator());
        } catch (IOException e) {
            throw new ExcecaoPersistenciaPedidos("Falha ao criar arquivo de pedidos: " + arquivo, e);
        }
    }

    private void persistir() {
        StringBuilder conteudo = new StringBuilder();
        conteudo.append(CABECALHO).append(System.lineSeparator());
        for (Pedido pedido : listarTodos()) {
            conteudo.append(serializarPedido(pedido)).append(System.lineSeparator());
        }
        try {
            Path parent = arquivo.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(arquivo, conteudo.toString());
        } catch (IOException e) {
            throw new ExcecaoPersistenciaPedidos("Falha ao salvar pedidos: " + arquivo, e);
        }
    }

    private Pedido parsePedido(String linha) {
        String[] partes = linha.split(SEPARADOR_CAMPO, -1);
        if (partes.length != 14) {
            throw new ExcecaoPersistenciaPedidos("Linha inválida em pedidos: " + linha);
        }
        try {
            String id = partes[0].trim();
            LocalDateTime criadoEm = LocalDateTime.parse(partes[1].trim());
            String reembolsadoEmTexto = partes[2].trim();
            LocalDateTime reembolsadoEm = reembolsadoEmTexto.isBlank() ? null : LocalDateTime.parse(reembolsadoEmTexto);
            StatusPedido status = StatusPedido.valueOf(partes[3].trim());
            MetodoFrete metodoFrete = MetodoFrete.valueOf(partes[4].trim());
            MetodoPagamento metodoPagamento = MetodoPagamento.valueOf(partes[5].trim());
            String codigoCupomTexto = partes[6].trim();
            CodigoCupom codigoCupom = codigoCupomTexto.isBlank() ? null : CodigoCupom.valueOf(codigoCupomTexto);
            Dinheiro subtotal = Dinheiro.of(partes[7].trim());
            Dinheiro descontoCupom = Dinheiro.of(partes[8].trim());
            Dinheiro descontoPagamento = Dinheiro.of(partes[9].trim());
            Dinheiro imposto = Dinheiro.of(partes[10].trim());
            Dinheiro frete = Dinheiro.of(partes[11].trim());
            Dinheiro total = Dinheiro.of(partes[12].trim());
            List<ItemPedido> itens = parseItens(partes[13].trim());
            return new Pedido(id, criadoEm, reembolsadoEm, itens, metodoFrete, metodoPagamento, codigoCupom,
                    subtotal, descontoCupom, descontoPagamento, imposto, frete, total, status);
        } catch (RuntimeException e) {
            throw new ExcecaoPersistenciaPedidos("Erro ao interpretar pedido: " + linha, e);
        }
    }

    private List<ItemPedido> parseItens(String texto) {
        if (texto.isBlank()) {
            throw new ExcecaoPersistenciaPedidos("Pedido sem itens não é válido.");
        }
        List<ItemPedido> itens = new ArrayList<>();
        String[] itensTexto = texto.split(SEPARADOR_ITENS);
        for (String itemTexto : itensTexto) {
            String[] campos = itemTexto.split(SEPARADOR_ITEM_CAMPO, -1);
            if (campos.length != 5) {
                throw new ExcecaoPersistenciaPedidos("Item inválido em pedido: " + itemTexto);
            }
            String sku = campos[0].trim();
            String nome = campos[1].trim();
            Dinheiro preco = Dinheiro.of(campos[2].trim());
            int peso = Integer.parseInt(campos[3].trim());
            int quantidade = Integer.parseInt(campos[4].trim());
            itens.add(new ItemPedido(sku, nome, preco, peso, quantidade));
        }
        return itens;
    }

    private String serializarPedido(Pedido pedido) {
        String codigoCupom = pedido.getCodigoCupom() == null ? "" : pedido.getCodigoCupom().name();
        String reembolsadoEm = pedido.getReembolsadoEm() == null ? "" : pedido.getReembolsadoEm().toString();
        String itensTexto = pedido.getItens().stream()
                .map(this::serializarItem)
                .collect(Collectors.joining(SEPARADOR_ITENS_ESCRITA));
        return String.join(SEPARADOR_CAMPO,
                pedido.getId(),
                pedido.getCriadoEm().toString(),
                reembolsadoEm,
                pedido.getStatus().name(),
                pedido.getMetodoFrete().name(),
                pedido.getMetodoPagamento().name(),
                codigoCupom,
                pedido.getSubtotal().toString(),
                pedido.getDescontoCupom().toString(),
                pedido.getDescontoPagamento().toString(),
                pedido.getImposto().toString(),
                pedido.getFrete().toString(),
                pedido.getTotal().toString(),
                itensTexto);
    }

    private String serializarItem(ItemPedido item) {
        return String.join(SEPARADOR_ITEM_CAMPO,
                item.getSku(),
                item.getNome(),
                item.getPrecoUnitario().toString(),
                String.valueOf(item.getPesoEmGramas()),
                String.valueOf(item.getQuantidade()));
    }
}
