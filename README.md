# Projeto: Suíte de testes para o sistema **Loja CLI**

## Contexto

Neste projeto você recebeu um sistema escrito em Java que simula o processo de )_checkout_ de um carrinho de compras em linha de comando. O sistema implementa regras de negócio para subtotal, descontos por cupom, descontos por forma de pagamento, cálculo de frete (incluindo variação por peso) e cálculo de imposto.

O código do sistema já está pronto e funcional, e o objetivo do projeto é construir uma suíte de testes automatizados de alta qualidade, usando **JUnit Jupiter**, garantindo a maior **cobertura de _branches_** possível e alta efetividade medida por **testes de mutação** com PIT.

> **Importante:** o foco principal do projeto é testar o **_core_** (regras de negócio). O componente de _Command-Line Interface_ (CLI), responsável por parsing de argumentos e impressão no console existe para viabilizar execução via terminal, mas **não é o principal alvo da suíte de testes**. Se desejar atacar este componente, informe no ato da entrega para que o esforço extra seja contabilizado. 

## Como executar a aplicação (modo interativo)

Execute a CLI com a task `run` do Gradle. A flag `--console=plain` evita a barra de progresso do Gradle, que atrapalha o uso interativo da CLI. Portanto, use:

```bash
./gradlew run --console=plain
```

O programa exibirá um menu interativo, que permite listar produtos, adicionar e remover itens, escolher frete e forma de pagamento, aplicar cupom, finalizar compra, salvar o carrinho, consultar histórico de pedidos e emitir reembolsos de pedidos já faturados, entre outros detalhes.

### Arquivos persistentes

- `dados/catalogo.csv`: catálogo e estoque persistente.
- `dados/pedidos.csv`: histórico de pedidos (um por linha).
- `dados/carrinho.csv`: arquivo padrão para importação/exportação do carrinho.

Se os arquivos não existirem, eles são criados automaticamente. O estoque é atualizado após compras bem-sucedidas e também após reembolsos.

**Formato do arquivo `dados/pedidos.csv` (CSV simples):**

```csv
id;criadoEm;reembolsadoEm;status;metodoFrete;metodoPagamento;codigoCupom;subtotal;descontoCupom;descontoPagamento;imposto;frete;total;itens
```

O campo `itens` usa `|` para separar itens e `,` para campos:

```
sku,nome,precoUnitario,peso,quantidade|sku2,nome2,preco2,peso2,quant2
```

O histórico exibido na CLI é ordenado do pedido mais recente para o mais antigo.

**Formato do `dados/carrinho.csv`:**

```
SKU;QTD
KB;2
MS;1
```

### Importação/exportação de carrinho

- Exportar: gera um CSV simples com `SKU;QTD` (padrão em `dados/carrinho.csv`).
- Importar: lê o CSV, valida SKUs e soma quantidades repetidas.

---

## Tarefas

Com base no projeto disponibilizado, realize as tarefas abaixo nesta ordem:

### 1) Escrever testes em **JUnit Jupiter**

* Escreva testes automatizados que validem o comportamento esperado do sistema e seus principais casos de borda.
* O projeto contém apenas 1 teste inicial como exemplo. Todo o restante deve ser escrito por você.

### 2) Maximizar cobertura de branch

* Use o relatório de cobertura do JaCoCo para verificar que os _branches_ (decisões `if/else`, `switch`, etc.) do `core` foram cobertos.
* O critério desejado é **100% de cobertura de _branch_** no escopo de classes de regra de negócio. Esta atividade será pontuada de forma proporcional à cobertura alcançada.

### 3) Matar **todos os mutantes** gerados pelo **PIT**

* Use o PIT, por meio da tarefa `./gradlew pitest` para gerar mutantes e avaliar a força da sua suíte.
* A meta é **matar 100% dos mutantes** no escopo de classes-alvo configuradas, ajuste a suíte de testes, possivelmente criando mais testes, para aumentar este _score_. Esta atividade será pontuada de forma proporcional à pontuação alcançada.

## Ferramentas já configuradas no Gradle

O projeto já vem com **JaCoCo** e **PIT** configurados. Você deve trabalhar com os comandos abaixo:

### Rodar testes (e gerar relatório de cobertura automaticamente)

```bash
./gradlew test
```

O relatório HTML do JaCoCo é gerado automaticamente após `test` em:

```
app/build/reports/jacoco/test/html/index.html
```

### Verificação de cobertura (_branch coverage_)

```bash
./gradlew jacocoTestCoverageVerification
```

### Rodar testes de mutação (PIT)

```bash
./gradlew pitest
```

O relatório HTML do PIT é gerado em uma pasta dentro de:

```
app/build/reports/pitest/
```

> Observação: enquanto sua suíte estiver fraca, é normal que `jacocoTestCoverageVerification` e/ou `pitest` falhem. Isso faz parte do ciclo de evolução dos testes.

---

## Escopo do que deve ser testado (classes-alvo)

O PIT e os critérios de cobertura estão focados no **_core_** do sistema, principalmente nos pacotes debaixo de `br.ufpe.cin.residencia.loja.*`. A interface de linha de comando em `br.ufpe.cin.residencia.loja.cli.Main` não é prioridade. 

## Regras de conduta e restrições

Para manter o projeto justo e focado em testes:

✅ **Permitido**

* Escrever testes e refatorar **testes** livremente.
* Fazer refatorações pequenas e seguras no código (ex.: renomear variáveis, extrair método) **_sem mudar comportamento_**.
* Melhorar mensagens de erro **_somente se_** isso não alterar regras e não _mascarar_ comportamento.

🚫 **Não permitido**

* Alterar a lógica de negócio para _fazer o teste passar_ (ex.: remover _branches_, simplificar regras, mudar condições, remover validações).
* _Desarmar_ o PIT/JaCoCo para facilitar (ex.: mudar `targetClasses`, baixar thresholds, excluir classes do core, etc.). Nenhuma alteração ao arquivo `build.gradle` é necessária. 
* Escrever testes artificiais só para enganar o mutante sem validar comportamento relevante (ex.: _asserts_ sem sentido, testes que dependem de detalhes internos sem necessidade).

> Em caso de dúvida: priorize sempre testes que expressem regras de negócio e contratos observáveis.
