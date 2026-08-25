# Spec: Testes e documentação operacional

> feature: testes-documentacao-operacional
> status: auditada

## Contexto

A suíte atual comprova os primeiros critérios de prontidão, mas ainda deixa sem proteção direta a API de pedidos, o processador de cartão e os handlers que transformam comandos Kafka em eventos. O README também não permite que uma pessoa execute e demonstre o fluxo completo a partir de um clone limpo. Esta entrega amplia as provas automatizadas e transforma a documentação em um guia operacional verificável, sem alterar regras de negócio.

## Histórias

### US-005 — Contratos HTTP protegidos contra regressões

Como pessoa que integra com as APIs, quero que pedidos e pagamentos inválidos sejam rejeitados de forma previsível, para detectar erros antes que alcancem a saga.

#### AC-008 — Pedido válido é aceito

- **Dado** um cliente, produto e quantidade válidos
- **Quando** o cliente envia `POST /orders`
- **Então** a API responde com status 202 e os dados do pedido criado

#### AC-009 — Pedido inválido é rejeitado antes do serviço

- **Dado** um pedido sem identificadores obrigatórios ou com quantidade não positiva
- **Quando** o cliente envia `POST /orders`
- **Então** a API responde com status 400 e não chama o serviço de pedidos

#### AC-010 — Processador de cartão valida sua entrada

- **Dado** um cartão e um valor de pagamento
- **Quando** o cliente envia `POST /ccp/process`
- **Então** dados positivos recebem status 202 e dados ausentes ou não positivos recebem status 400

### US-006 — Handlers assíncronos protegidos contra regressões

Como pessoa mantenedora, quero provar os eventos emitidos pelos handlers da saga, para impedir que falhas ou compensações percam os identificadores necessários.

#### AC-011 — Reserva de produto publica o resultado correspondente

- **Dado** um comando de reserva com pedido, produto e quantidade conhecidos
- **Quando** a reserva tem sucesso ou falha
- **Então** o handler publica o evento correspondente preservando os dados da operação

#### AC-012 — Cancelamento de reserva confirma a compensação

- **Dado** um comando para cancelar uma reserva existente
- **Quando** o produto é devolvido ao estoque
- **Então** o handler publica a confirmação do cancelamento com pedido e produto originais

#### AC-013 — Processamento de pagamento publica o resultado correspondente

- **Dado** um comando de pagamento com os dados do pedido
- **Quando** o processamento tem sucesso ou o processador está indisponível
- **Então** o handler publica o evento de sucesso ou falha preservando os identificadores necessários à saga

### US-007 — Execução e qualidade documentadas

Como pessoa desenvolvedora, quero um guia operacional e um relatório de cobertura gerado pela CI, para executar o exemplo e localizar lacunas sem conhecimento prévio do repositório.

#### AC-014 — README permite executar e demonstrar o projeto

- **Dado** um clone limpo com Java 17 e Docker
- **Quando** a pessoa segue o README
- **Então** encontra comandos para subir Kafka, criar tópicos, iniciar os serviços, cadastrar produto e pedido, consultar histórico, configurar pagamentos e resolver problemas comuns

#### AC-015 — Build gera relatório de cobertura

- **Dado** o projeto com testes automatizados
- **Quando** o build de verificação roda localmente ou na CI
- **Então** relatórios JaCoCo são gerados por módulo e publicados como artefato da execução contínua

## Fora de escopo

- Subir um fluxo ponta a ponta com Kafka real ou Testcontainers nesta entrega.
- Alterar regras de negócio, contratos públicos ou persistência dos serviços.
- Definir uma meta mínima de cobertura antes de existir uma linha de base estável.
- Documentar implantação em nuvem ou ambientes que não sejam locais.

## Suposições

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-002 | A melhoria deve priorizar testes rápidos e documentação local, sem alterar o comportamento funcional. | confirmada | Confirmada pelo pedido para melhorar especificamente testes e documentação após a revisão de 24/08/2026. |

## Perguntas em aberto

Nenhuma.
