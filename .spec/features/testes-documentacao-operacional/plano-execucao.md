# Plano de execução — testes-documentacao-operacional

> gerado por `onp-spec plano` em 2026-08-25 01:07 — NÃO edite à mão;
> mudou tasks.md ou a config? Regenere: `onp-spec plano testes-documentacao-operacional`

## Resumo — o que vai acontecer

- **3 tarefa(s) pendente(s)**: 3 em 3 faixa(s) paralela(s) + 0 sequencial(is)
- **1 faixa = 1 worktree + 1 branch + 1 janela de contexto limpa** — faixas não compartilham nenhum arquivo entre si
- prefere outra seleção ou uma após a outra? Regenere com `onp-spec plano testes-documentacao-operacional --paralelizar T-xxx,T-yyy` ou `--sequencial`
- tudo acontece na branch de trabalho `spec/testes-documentacao-operacional`; levar para a main é decisão sua

## Faixas e ondas

### Onda 1 — faixa-1 ∥ faixa-2 ∥ faixa-3

#### faixa-1 — branch `spec/testes-documentacao-operacional-faixa-1` — worktree `../onp-worktrees/event-driven-spring-boot-testes-documentacao-operacional-faixa-1`

| tarefa | título | modelo | esforço | arquivos |
|---|---|---|---|---|
| T-005 | Testar os contratos HTTP ainda descobertos | `gpt-5.6-terra` | medium | `orders-service/src/test/java/com/appsdeveloperblog/orders/web/controller/OrdersControllerTest.java`, `credit-card-processor-service/pom.xml`, `credit-card-processor-service/src/test/java/com/appsdeveloperblog/ccps/web/controller/CreditCardProcessorControllerTest.java` |

#### faixa-2 — branch `spec/testes-documentacao-operacional-faixa-2` — worktree `../onp-worktrees/event-driven-spring-boot-testes-documentacao-operacional-faixa-2`

| tarefa | título | modelo | esforço | arquivos |
|---|---|---|---|---|
| T-006 | Testar os handlers de produto e pagamento | `gpt-5.6-terra` | high | `products-service/src/test/java/com/appsdeveloperblog/products/service/handler/ProductCommandsHandlerTest.java`, `payments-service/src/test/java/com/appsdeveloperblog/payments/service/handler/PaymentsCommandsHandlerTest.java` |

#### faixa-3 — branch `spec/testes-documentacao-operacional-faixa-3` — worktree `../onp-worktrees/event-driven-spring-boot-testes-documentacao-operacional-faixa-3`

| tarefa | título | modelo | esforço | arquivos |
|---|---|---|---|---|
| T-007 | Criar guia operacional e relatórios de cobertura | `gpt-5.6-terra` | medium | `README.md`, `pom.xml`, `.github/workflows/ci.yml`, `core/src/test/java/com/appsdeveloperblog/core/DocumentationCompletenessTest.java`, `core/src/test/java/com/appsdeveloperblog/core/BuildQualityConfigurationTest.java`, `onpspec.config.json` |

## Gestão de branches e commits

1. branch de trabalho `spec/testes-documentacao-operacional` criada do ponto atual (se ainda não existir)
2. cada faixa nasce dela como branch própria e roda no seu worktree — **1 tarefa = 1 commit** (`T-xxx feature: título`)
3. terminou a onda → merge `--no-ff` de cada faixa de volta, na ordem; conflito interrompe a faixa e pede resolução humana
4. faixa mesclada → worktree removido, branch apagada, tarefa marcada `[concluida]` no tasks.md
5. gate final na branch de trabalho: `onp-spec verify testes-documentacao-operacional` + `onp-spec audit --ci` — **exit 0 ou não está pronto**

## Como executar

### ▶ Execução — Codex headless (codex exec)

```bash
bash .spec/features/testes-documentacao-operacional/executar-tarefas.sh
```

Cada faixa roda `codex exec` com **janela de contexto limpa**, no seu worktree, com
`--model` e `model_reasoning_effort` já definidos por tarefa e sandbox `workspace-write`. Os prompts exatos estão
embutidos no script — quer rodar uma faixa na mão, é só copiá-los de lá.
Logs: `../onp-worktrees/event-driven-spring-boot-testes-documentacao-operacional-logs/`.

**Confirmação de custos — antes de executar**: os modelos e esforços por
tarefa estão nas tabelas acima; o agente CONFIRMA com o usuário se estão
dentro da licença/cota dele (modelo forte + esforço alto torra tokens).
Para gastar menos: `onp-spec plano testes-documentacao-operacional --modelo gpt-5.6-luna --esforco baixo`
(tudo) ou por tarefa `onp-spec tarefa testes-documentacao-operacional T-xxx --modelo <m> --esforco <nível>` — e regenere o plano.

### 📣 Acompanhamento — tabela + resumo no chat (a cada 1 min)

O script roda em **background**: o agente AVISA o usuário antes de iniciar e,
enquanto roda, posta no chat a cada ~1 minuto a **tabela de andamento** (qual
tarefa está rodando, qual não está, o que concluiu/falhou) junto com o
**resumo geral de andamento** (escrito por IA; sem IA, o motor resume). Ao
final, o usuário recebe o resumo completo da execução. A qualquer momento:

```bash
onp-spec resumo testes-documentacao-operacional --tabela   # a tabela de andamento
onp-spec resumo testes-documentacao-operacional            # o resumo em texto
```

