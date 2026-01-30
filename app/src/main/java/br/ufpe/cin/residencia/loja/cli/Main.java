package br.ufpe.cin.residencia.loja.cli;

import br.ufpe.cin.residencia.loja.catalogo.CatalogoArquivoCSV;
import br.ufpe.cin.residencia.loja.checkout.ResultadoCheckout;
import br.ufpe.cin.residencia.loja.checkout.ServicoCheckout;
import br.ufpe.cin.residencia.loja.descontos.CodigoCupom;
import br.ufpe.cin.residencia.loja.descontos.RepositorioCupomEmMemoria;
import br.ufpe.cin.residencia.loja.dominio.Carrinho;
import br.ufpe.cin.residencia.loja.dominio.Dinheiro;
import br.ufpe.cin.residencia.loja.dominio.Item;
import br.ufpe.cin.residencia.loja.frete.MetodoFrete;
import br.ufpe.cin.residencia.loja.io.ExcecaoImportacaoCarrinho;
import br.ufpe.cin.residencia.loja.io.ExportadorCarrinho;
import br.ufpe.cin.residencia.loja.io.ImportadorCarrinho;
import br.ufpe.cin.residencia.loja.pagamento.MetodoPagamento;
import br.ufpe.cin.residencia.loja.pedidos.Pedido;
import br.ufpe.cin.residencia.loja.pedidos.ServicoReembolso;
import br.ufpe.cin.residencia.loja.persistencia.RepositorioPedidos;
import br.ufpe.cin.residencia.loja.persistencia.RepositorioPedidosArquivoCSV;
import br.ufpe.cin.residencia.loja.precificacao.Recibo;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Path ARQUIVO_CATALOGO = Path.of("dados", "catalogo.csv");
    private static final Path ARQUIVO_CARRINHO = Path.of("dados", "carrinho.csv");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        CatalogoArquivoCSV catalogo = CatalogoArquivoCSV.carregar(ARQUIVO_CATALOGO);
        RepositorioCupomEmMemoria repositorioCupom = new RepositorioCupomEmMemoria();
        RepositorioPedidos repositorioPedidos = new RepositorioPedidosArquivoCSV();
        ServicoCheckout checkout = new ServicoCheckout(catalogo, repositorioCupom, repositorioPedidos);
        ServicoReembolso servicoReembolso = new ServicoReembolso(repositorioPedidos, catalogo);
        ExportadorCarrinho exportadorCarrinho = new ExportadorCarrinho();
        ImportadorCarrinho importadorCarrinho = new ImportadorCarrinho();

        Map<String, Integer> carrinho = new LinkedHashMap<>();
        MetodoFrete frete = MetodoFrete.PADRAO;
        MetodoPagamento pagamento = MetodoPagamento.CARTAO;
        CodigoCupom cupom = null;

        try (Scanner scanner = new Scanner(System.in)) {
            boolean continuar = true;
            while (continuar) {
                imprimirMenu(frete, pagamento, cupom);
                String opcao = lerLinha(scanner);
                if (opcao == null) {
                    System.out.println("Entrada encerrada. Saindo.");
                    break;
                }
                opcao = opcao.trim();
                switch (opcao) {
                    case "1":
                        listarProdutos(catalogo);
                        break;
                    case "2":
                        adicionarItem(scanner, catalogo, carrinho);
                        break;
                    case "3":
                        removerItem(scanner, carrinho);
                        break;
                    case "4":
                        mostrarCarrinho(catalogo, carrinho);
                        break;
                    case "5":
                        frete = escolherFrete(scanner, frete);
                        break;
                    case "6":
                        pagamento = escolherPagamento(scanner, pagamento);
                        break;
                    case "7":
                        cupom = aplicarCupom(scanner, cupom);
                        break;
                    case "8":
                        finalizarCompra(scanner, checkout, catalogo, carrinho, frete, pagamento, cupom);
                        break;
                    case "9":
                        listarPedidos(repositorioPedidos);
                        break;
                    case "10":
                        mostrarDetalhesPedido(scanner, repositorioPedidos);
                        break;
                    case "11":
                        reembolsarPedido(scanner, servicoReembolso);
                        break;
                    case "12":
                        exportarCarrinho(scanner, catalogo, carrinho, exportadorCarrinho);
                        break;
                    case "13":
                        importarCarrinho(scanner, catalogo, carrinho, importadorCarrinho);
                        break;
                    case "14":
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opcao invalida. Escolha um numero do menu.");
                        break;
                }
            }
        } finally {
            catalogo.salvar();
            System.out.println("Catalogo salvo. Encerrando aplicacao.");
        }
    }

    private static void imprimirMenu(MetodoFrete frete, MetodoPagamento pagamento, CodigoCupom cupom) {
        System.out.println();
        System.out.println("==== Loja CLI ====");
        System.out.println("1) Listar produtos");
        System.out.println("2) Adicionar item ao carrinho");
        System.out.println("3) Remover item do carrinho");
        System.out.println("4) Ver carrinho");
        System.out.println("5) Escolher frete (atual: " + frete + ")");
        System.out.println("6) Escolher pagamento (atual: " + pagamento + ")");
        System.out.println("7) Aplicar cupom (atual: " + (cupom == null ? "nenhum" : cupom) + ")");
        System.out.println("8) Finalizar compra");
        System.out.println("9) Ver historico de pedidos");
        System.out.println("10) Ver detalhes de um pedido");
        System.out.println("11) Reembolsar pedido");
        System.out.println("12) Exportar carrinho para arquivo");
        System.out.println("13) Importar carrinho de arquivo");
        System.out.println("14) Sair");
        System.out.print("Selecione uma opcao: ");
    }

    private static void listarProdutos(CatalogoArquivoCSV catalogo) {
        System.out.println();
        System.out.println("Catalogo:");
        System.out.println("SKU | Nome | Preco | Peso(g) | Estoque");
        for (Map.Entry<String, Item> entrada : catalogo.itens().entrySet()) {
            String sku = entrada.getKey();
            Item item = entrada.getValue();
            System.out.println(sku + " | " + item.getNome() + " | R$ " + item.getPrecoUnitario()
                    + " | " + item.getPesoEmGramas() + " | " + catalogo.estoquePara(sku));
        }
    }

    private static void adicionarItem(Scanner scanner, CatalogoArquivoCSV catalogo, Map<String, Integer> carrinho) {
        System.out.print("Informe o SKU: ");
        String sku = lerLinha(scanner);
        if (sku == null) {
            System.out.println("Entrada encerrada. Operacao cancelada.");
            return;
        }
        sku = sku.trim();
        Item item = catalogo.obterPorSku(sku);
        if (item == null) {
            System.out.println("SKU nao encontrado no catalogo.");
            return;
        }

        int quantidade = lerQuantidade(scanner, "Informe a quantidade: ");
        if (quantidade <= 0) {
            return;
        }

        int estoqueDisponivel = catalogo.estoquePara(sku);
        int noCarrinho = carrinho.getOrDefault(sku, 0);
        if (quantidade + noCarrinho > estoqueDisponivel) {
            System.out.println("Quantidade solicitada excede o estoque disponivel.");
            return;
        }

        carrinho.put(sku, noCarrinho + quantidade);
        System.out.println("Item adicionado ao carrinho.");
    }

    private static void removerItem(Scanner scanner, Map<String, Integer> carrinho) {
        if (carrinho.isEmpty()) {
            System.out.println("Carrinho vazio.");
            return;
        }
        System.out.print("Informe o SKU a remover: ");
        String sku = lerLinha(scanner);
        if (sku == null) {
            System.out.println("Entrada invalida. Operacao cancelada.");
            return;
        }
        sku = sku.trim();
        if (!carrinho.containsKey(sku)) {
            System.out.println("SKU nao esta no carrinho.");
            return;
        }

        int quantidade = lerQuantidade(scanner, "Informe a quantidade a remover: ");
        if (quantidade <= 0) {
            return;
        }

        int atual = carrinho.getOrDefault(sku, 0);
        if (quantidade >= atual) {
            carrinho.remove(sku);
            System.out.println("Item removido do carrinho.");
        } else {
            carrinho.put(sku, atual - quantidade);
            System.out.println("Quantidade atualizada no carrinho.");
        }
    }

    private static void mostrarCarrinho(CatalogoArquivoCSV catalogo, Map<String, Integer> carrinho) {
        if (carrinho.isEmpty()) {
            System.out.println("Carrinho vazio.");
            return;
        }

        Dinheiro subtotal = Dinheiro.zero();
        int pesoTotal = 0;
        java.util.List<String[]> linhas = new java.util.ArrayList<>();
        int larguraSku = "SKU".length();
        int larguraNome = "Nome".length();
        int larguraQuantidade = "Quantidade".length();
        int larguraPreco = "Preco".length();
        int larguraTotal = "Total".length();
        System.out.println();
        System.out.println("Carrinho:");
        for (Map.Entry<String, Integer> entrada : carrinho.entrySet()) {
            String sku = entrada.getKey();
            int quantidade = entrada.getValue();
            Item item = catalogo.obterPorSku(sku);
            if (item == null) {
                continue;
            }
            Dinheiro totalLinha = item.getPrecoUnitario().multiplicar(new java.math.BigDecimal(quantidade));
            subtotal = subtotal.somar(totalLinha);
            pesoTotal += item.getPesoEmGramas() * quantidade;
            String nome = item.getNome();
            String quantidadeTexto = String.valueOf(quantidade);
            String precoTexto = "R$ " + item.getPrecoUnitario();
            String totalTexto = "R$ " + totalLinha;
            larguraSku = Math.max(larguraSku, sku.length());
            larguraNome = Math.max(larguraNome, nome.length());
            larguraQuantidade = Math.max(larguraQuantidade, quantidadeTexto.length());
            larguraPreco = Math.max(larguraPreco, precoTexto.length());
            larguraTotal = Math.max(larguraTotal, totalTexto.length());
            linhas.add(new String[]{sku, nome, quantidadeTexto, precoTexto, totalTexto});
        }
        String formato = "%-" + larguraSku + "s | %-" + larguraNome + "s | %"
                + larguraQuantidade + "s | %" + larguraPreco + "s | %" + larguraTotal + "s";
        System.out.println(String.format(formato, "SKU", "Nome", "Quantidade", "Preco", "Total"));
        for (String[] linha : linhas) {
            System.out.println(String.format(formato, linha[0], linha[1], linha[2], linha[3], linha[4]));
        }
        System.out.println("Subtotal: R$ " + subtotal);
        System.out.println("Peso total: " + pesoTotal + "g");
    }

    private static MetodoFrete escolherFrete(Scanner scanner, MetodoFrete atual) {
        System.out.print("Informe o frete (PADRAO/EXPRESSO): ");
        String valor = lerLinha(scanner);
        if (valor == null) {
            System.out.println("Entrada encerrada. Mantendo: " + atual);
            return atual;
        }
        valor = valor.trim();
        try {
            return MetodoFrete.valueOf(valor);
        } catch (IllegalArgumentException e) {
            System.out.println("Frete invalido. Mantendo: " + atual);
            return atual;
        }
    }

    private static MetodoPagamento escolherPagamento(Scanner scanner, MetodoPagamento atual) {
        System.out.print("Informe o pagamento (PIX/CARTAO/BOLETO): ");
        String valor = lerLinha(scanner);
        if (valor == null) {
            System.out.println("Entrada encerrada. Mantendo: " + atual);
            return atual;
        }
        valor = valor.trim();
        try {
            return MetodoPagamento.valueOf(valor);
        } catch (IllegalArgumentException e) {
            System.out.println("Pagamento invalido. Mantendo: " + atual);
            return atual;
        }
    }

    private static CodigoCupom aplicarCupom(Scanner scanner, CodigoCupom atual) {
        System.out.print("Informe o cupom (ou deixe vazio para remover): ");
        String valor = lerLinha(scanner);
        if (valor == null) {
            System.out.println("Entrada encerrada. Mantendo: " + (atual == null ? "nenhum" : atual));
            return atual;
        }
        valor = valor.trim();
        if (valor.isEmpty()) {
            System.out.println("Cupom removido.");
            return null;
        }
        try {
            return CodigoCupom.valueOf(valor);
        } catch (IllegalArgumentException e) {
            System.out.println("Cupom invalido. Mantendo: " + (atual == null ? "nenhum" : atual));
            return atual;
        }
    }

    private static void finalizarCompra(Scanner scanner, ServicoCheckout checkout, CatalogoArquivoCSV catalogo,
                                        Map<String, Integer> carrinho, MetodoFrete frete,
                                        MetodoPagamento pagamento, CodigoCupom cupom) {
        if (carrinho.isEmpty()) {
            System.out.println("Carrinho vazio. Adicione itens antes de finalizar.");
            return;
        }

        Carrinho carrinhoCore = new Carrinho();
        for (Map.Entry<String, Integer> entrada : carrinho.entrySet()) {
            Item item = catalogo.obterPorSku(entrada.getKey());
            if (item != null) {
                carrinhoCore.adicionar(item, entrada.getValue());
            }
        }

        System.out.println();
        System.out.println("Resumo da compra:");
        mostrarCarrinho(catalogo, carrinho);
        System.out.println("Frete: " + frete);
        System.out.println("Pagamento: " + pagamento);
        System.out.println("Cupom: " + (cupom == null ? "nenhum" : cupom));
        System.out.print("Confirmar compra? (s/n): ");

        String resposta = lerLinha(scanner);
        if (resposta == null) {
            System.out.println("Entrada encerrada. Compra cancelada.");
            return;
        }
        resposta = resposta.trim().toLowerCase();
        if (!resposta.equals("s")) {
            System.out.println("Compra cancelada.");
            return;
        }

        try {
            ResultadoCheckout resultado = checkout.finalizarCompra(carrinhoCore, frete, pagamento, cupom);
            Recibo recibo = resultado.getRecibo();
            System.out.println();
            System.out.println(recibo);
            carrinho.clear();
            System.out.println("Pedido registrado com id: " + resultado.getIdPedido());
            System.out.println("Compra finalizada com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("Falha ao finalizar compra: " + e.getMessage());
        }
    }

    private static void listarPedidos(RepositorioPedidos repositorioPedidos) {
        System.out.println();
        System.out.println("Historico de pedidos:");
        if (repositorioPedidos.listarTodos().isEmpty()) {
            System.out.println("Nenhum pedido registrado.");
            return;
        }
        for (Pedido pedido : repositorioPedidos.listarTodos()) {
            System.out.println(pedido.getId() + " | " + pedido.getCriadoEm().format(FORMATO_DATA)
                    + " | Total: R$ " + pedido.getTotal() + " | " + pedido.getStatus());
        }
    }

    private static void mostrarDetalhesPedido(Scanner scanner, RepositorioPedidos repositorioPedidos) {
        System.out.print("Informe o id do pedido: ");
        String id = lerLinha(scanner);
        if (id == null || id.isBlank()) {
            System.out.println("Entrada invalida.");
            return;
        }
        try {
            Pedido pedido = repositorioPedidos.obterPorId(id.trim());
            System.out.println();
            System.out.println("Pedido: " + pedido.getId());
            System.out.println("Criado em: " + pedido.getCriadoEm().format(FORMATO_DATA));
            System.out.println("Status: " + pedido.getStatus());
            if (pedido.getReembolsadoEm() != null) {
                System.out.println("Reembolsado em: " + pedido.getReembolsadoEm().format(FORMATO_DATA));
            }
            System.out.println("Itens:");
            for (var item : pedido.getItens()) {
                System.out.println("- " + item.getSku() + " | " + item.getNome()
                        + " | Qtd: " + item.getQuantidade()
                        + " | Unit: R$ " + item.getPrecoUnitario()
                        + " | Total: R$ " + item.total());
            }
            System.out.println("Subtotal: R$ " + pedido.getSubtotal());
            System.out.println("Desconto cupom: R$ " + pedido.getDescontoCupom());
            System.out.println("Desconto pagamento: R$ " + pedido.getDescontoPagamento());
            System.out.println("Imposto: R$ " + pedido.getImposto());
            System.out.println("Frete: R$ " + pedido.getFrete());
            System.out.println("Total: R$ " + pedido.getTotal());
        } catch (RuntimeException e) {
            System.out.println("Falha ao buscar pedido: " + e.getMessage());
        }
    }

    private static void reembolsarPedido(Scanner scanner, ServicoReembolso servicoReembolso) {
        System.out.print("Informe o id do pedido para reembolso: ");
        String id = lerLinha(scanner);
        if (id == null || id.isBlank()) {
            System.out.println("Entrada invalida.");
            return;
        }
        try {
            Pedido pedido = servicoReembolso.reembolsar(id.trim());
            System.out.println("Pedido reembolsado. Status atual: " + pedido.getStatus());
        } catch (RuntimeException e) {
            System.out.println("Falha ao reembolsar pedido: " + e.getMessage());
        }
    }

    private static void exportarCarrinho(Scanner scanner, CatalogoArquivoCSV catalogo,
                                         Map<String, Integer> carrinho, ExportadorCarrinho exportador) {
        if (carrinho.isEmpty()) {
            System.out.println("Carrinho vazio. Nada para exportar.");
            return;
        }
        System.out.print("Informe o caminho do arquivo (padrao: " + ARQUIVO_CARRINHO + "): ");
        String caminho = lerLinha(scanner);
        Path destino = caminho == null || caminho.isBlank() ? ARQUIVO_CARRINHO : Path.of(caminho.trim());
        try {
            exportador.exportar(montarCarrinho(catalogo, carrinho), destino);
            System.out.println("Carrinho exportado para: " + destino);
        } catch (RuntimeException e) {
            System.out.println("Falha ao exportar carrinho: " + e.getMessage());
        }
    }

    private static void importarCarrinho(Scanner scanner, CatalogoArquivoCSV catalogo,
                                         Map<String, Integer> carrinho, ImportadorCarrinho importador) {
        System.out.print("Informe o caminho do arquivo (padrao: " + ARQUIVO_CARRINHO + "): ");
        String caminho = lerLinha(scanner);
        Path origem = caminho == null || caminho.isBlank() ? ARQUIVO_CARRINHO : Path.of(caminho.trim());
        try {
            Carrinho importado = importador.importar(origem, catalogo);
            atualizarCarrinho(carrinho, importado);
            System.out.println("Carrinho importado com sucesso.");
        } catch (ExcecaoImportacaoCarrinho e) {
            System.out.println("Falha ao importar carrinho: " + e.getMessage());
        }
    }

    private static int lerQuantidade(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        String valor = lerLinha(scanner);
        if (valor == null) {
            System.out.println("Entrada encerrada.");
            return -1;
        }
        valor = valor.trim();
        try {
            int quantidade = Integer.parseInt(valor);
            if (quantidade <= 0) {
                System.out.println("Quantidade deve ser maior que zero.");
                return -1;
            }
            return quantidade;
        } catch (NumberFormatException e) {
            System.out.println("Quantidade invalida.");
            return -1;
        }
    }

    private static String lerLinha(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    private static Carrinho montarCarrinho(CatalogoArquivoCSV catalogo, Map<String, Integer> carrinho) {
        Carrinho carrinhoCore = new Carrinho();
        for (Map.Entry<String, Integer> entrada : carrinho.entrySet()) {
            Item item = catalogo.obterPorSku(entrada.getKey());
            if (item != null) {
                carrinhoCore.adicionar(item, entrada.getValue());
            }
        }
        return carrinhoCore;
    }

    private static void atualizarCarrinho(Map<String, Integer> carrinho, Carrinho importado) {
        carrinho.clear();
        importado.linhas().forEach(linha ->
                carrinho.put(linha.getItem().getSku(), linha.getQuantidade()));
    }
}
