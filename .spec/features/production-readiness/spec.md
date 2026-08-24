# Spec: Prontidão de produção

> feature: production-readiness
> status: em-implementacao

## Contexto

O projeto demonstra uma saga orquestrada, mas hoje não oferece um build reproduzível a partir de um clone limpo, não prova os caminhos críticos com testes e mantém parâmetros operacionais importantes dentro do código. Esta entrega melhora a experiência de desenvolvimento e reduz falhas silenciosas sem alterar o fluxo de negócio da saga.

## Histórias

### US-001 — Build reproduzível

Como pessoa desenvolvedora, quero compilar e testar todos os módulos sem instalar Maven globalmente, para que o mesmo comando funcione localmente e na integração contínua.

#### AC-001 — Clone limpo compila todos os módulos

- **Dado** um clone limpo com Java 17 e acesso ao Maven Central
- **Quando** a pessoa executa o Maven Wrapper na raiz
- **Então** `core` e os quatro serviços são compilados e todos os testes terminam com sucesso

### US-002 — Criação de produto confiável

Como cliente da API, quero receber respostas previsíveis ao cadastrar produtos, para corrigir dados inválidos sem descobrir erros internos do servidor.

#### AC-002 — Produto válido é criado

- **Dado** um nome, preço e quantidade válidos em JSON
- **Quando** o cliente envia `POST /products`
- **Então** a API aceita o JSON, cria o produto e responde com status 201 e seus dados

#### AC-003 — Quantidade ausente ou inválida é rejeitada

- **Dado** um cadastro sem quantidade ou com quantidade não positiva
- **Quando** o cliente envia `POST /products`
- **Então** a API responde com status 400 e não chama o serviço de persistência

### US-003 — Pagamento com falha previsível

Como pessoa operadora, quero configurar a conexão com o processador de cartões fora do código, para limitar indisponibilidades e adaptar cada ambiente sem recompilar.

#### AC-004 — Chamada remota tem limites de espera configuráveis

- **Dado** valores de timeout definidos na configuração do serviço de pagamentos
- **Quando** o cliente HTTP do processador é criado
- **Então** os limites de conexão e leitura configurados são aplicados à chamada remota

#### AC-005 — Cartão de demonstração vem da configuração

- **Dado** um número de cartão de demonstração fornecido por propriedade ou variável de ambiente
- **Quando** um pagamento é processado
- **Então** o serviço envia o número configurado e não depende de um cartão fixo no código Java

### US-004 — Compensação da saga protegida contra regressões

Como pessoa mantenedora, quero provas automatizadas do caminho compensatório, para evitar que uma falha de pagamento deixe estoque ou pedido em estado inconsistente.

#### AC-006 — Falha no pagamento inicia devolução do estoque

- **Dado** uma saga com pedido, produto e quantidade conhecidos
- **Quando** chega um evento de falha no pagamento
- **Então** é publicado um comando de cancelamento de reserva com os mesmos identificadores e quantidade

#### AC-007 — Estoque devolvido rejeita o pedido

- **Dado** uma reserva de produto já cancelada
- **Quando** chega o evento de cancelamento da reserva
- **Então** é publicado um comando de rejeição do pedido e o histórico registra o estado rejeitado

## Decisões de projeto

- Usar o Maven Wrapper oficial no modo somente scripts, evitando binários versionados.
- Manter Java 17 e Spring Boot 3.2.5 nesta entrega; atualização de dependências exige uma rodada própria de compatibilidade.
- Usar testes unitários/MVC rápidos; testes ponta a ponta com Kafka real ficam fora deste incremento.
- Externalizar endpoints, timeouts e o cartão de demonstração com `@ConfigurationProperties` validado.

## Fora de escopo

- Implementar transactional outbox, idempotência de consumidores ou exatamente-uma-vez.
- Atualizar Spring Boot, Kafka ou imagens Docker.
- Substituir H2 por banco de dados de produção.
- Executar testes ponta a ponta com um cluster Kafka.

## Suposições

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-001 | A prioridade desta melhoria é confiabilidade, testabilidade e experiência de desenvolvimento, não uma nova regra de negócio. | confirmada | Confirmada pelo usuário ao escolher a execução recomendada em 24/08/2026. |

## Perguntas em aberto

Nenhuma.
