# Plano de execução — production-readiness

> gerado por `onp-spec plano` em 2026-08-24 02:32 — NÃO edite à mão;
> mudou tasks.md ou a config? Regenere: `onp-spec plano production-readiness --paralelizar T-001,T-002,T-003,T-004`

## Resumo — o que vai acontecer

- **4 tarefa(s) pendente(s)**: 4 em 4 faixa(s) paralela(s) + 0 sequencial(is)
- **seleção do usuário**: paralelizar só T-001, T-002, T-003, T-004 — as demais rodam uma após a outra, ao final
- **1 faixa = 1 worktree + 1 branch + 1 janela de contexto limpa** — faixas não compartilham nenhum arquivo entre si
- prefere outra seleção ou uma após a outra? Regenere com `onp-spec plano production-readiness --paralelizar T-xxx,T-yyy` ou `--sequencial`
- tudo acontece na branch de trabalho `spec/production-readiness`; levar para a main é decisão sua

## Faixas e ondas

### Onda 1 — faixa-1 ∥ faixa-2 ∥ faixa-3

#### faixa-1 — branch `spec/production-readiness-faixa-1` — worktree `../onp-worktrees/event-driven-spring-boot-production-readiness-faixa-1`

| tarefa | título | modelo | esforço | arquivos |
|---|---|---|---|---|
| T-001 | Tornar o build reproduzível e contínuo | `gpt-5.6-terra` | medium | `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `.github/workflows/ci.yml`, `README.md`, `onpspec.config.json`, `.spec/constituicao.md` |

#### faixa-2 — branch `spec/production-readiness-faixa-2` — worktree `../onp-worktrees/event-driven-spring-boot-production-readiness-faixa-2`

| tarefa | título | modelo | esforço | arquivos |
|---|---|---|---|---|
| T-002 | Corrigir e testar o contrato de criação de produtos | `gpt-5.6-terra` | medium | `products-service/pom.xml`, `products-service/src/main/java/com/appsdeveloperblog/products/dto/ProductCreationRequest.java`, `products-service/src/test/java/com/appsdeveloperblog/products/web/controller/ProductsControllerTest.java` |

#### faixa-3 — branch `spec/production-readiness-faixa-3` — worktree `../onp-worktrees/event-driven-spring-boot-production-readiness-faixa-3`

| tarefa | título | modelo | esforço | arquivos |
|---|---|---|---|---|
| T-003 | Externalizar e limitar a integração de pagamentos | `gpt-5.6-terra` | high | `payments-service/src/main/java/com/appsdeveloperblog/payments/config/PaymentProcessorProperties.java`, `payments-service/src/main/java/com/appsdeveloperblog/payments/config/ApplicationConfig.java`, `payments-service/src/main/java/com/appsdeveloperblog/payments/service/PaymentServiceImpl.java`, `payments-service/src/main/resources/application.properties`, `payments-service/src/test/java/com/appsdeveloperblog/payments/config/ApplicationConfigTest.java`, `payments-service/src/test/java/com/appsdeveloperblog/payments/service/PaymentServiceImplTest.java` |

### Onda 2 — faixa-4

#### faixa-4 — branch `spec/production-readiness-faixa-4` — worktree `../onp-worktrees/event-driven-spring-boot-production-readiness-faixa-4`

| tarefa | título | modelo | esforço | arquivos |
|---|---|---|---|---|
| T-004 | Provar o caminho compensatório da saga | `gpt-5.6-terra` | medium | `orders-service/src/test/java/com/appsdeveloperblog/orders/saga/OrderSagaTest.java` |

## Gestão de branches e commits

1. branch de trabalho `spec/production-readiness` criada do ponto atual (se ainda não existir)
2. cada faixa nasce dela como branch própria e roda no seu worktree — **1 tarefa = 1 commit** (`T-xxx feature: título`)
3. terminou a onda → merge `--no-ff` de cada faixa de volta, na ordem; conflito interrompe a faixa e pede resolução humana
4. faixa mesclada → worktree removido, branch apagada, tarefa marcada `[concluida]` no tasks.md
5. gate final na branch de trabalho: `onp-spec verify production-readiness` + `onp-spec audit --ci` — **exit 0 ou não está pronto**

## Como executar

### ▶ Execução — Codex headless (codex exec)

```bash
bash .spec/features/production-readiness/executar-tarefas.sh
```

Cada faixa roda `codex exec` com **janela de contexto limpa**, no seu worktree, com
`--model` e `model_reasoning_effort` já definidos por tarefa e sandbox `workspace-write`. Os prompts exatos estão
embutidos no script — quer rodar uma faixa na mão, é só copiá-los de lá.
Logs: `../onp-worktrees/event-driven-spring-boot-production-readiness-logs/`.

**Confirmação de custos — antes de executar**: os modelos e esforços por
tarefa estão nas tabelas acima; o agente CONFIRMA com o usuário se estão
dentro da licença/cota dele (modelo forte + esforço alto torra tokens).
Para gastar menos: `onp-spec plano production-readiness --modelo gpt-5.6-luna --esforco baixo`
(tudo) ou por tarefa `onp-spec tarefa production-readiness T-xxx --modelo <m> --esforco <nível>` — e regenere o plano.

### 📣 Acompanhamento — tabela + resumo no chat (a cada 1 min)

O script roda em **background**: o agente AVISA o usuário antes de iniciar e,
enquanto roda, posta no chat a cada ~1 minuto a **tabela de andamento** (qual
tarefa está rodando, qual não está, o que concluiu/falhou) junto com o
**resumo geral de andamento** (escrito por IA; sem IA, o motor resume). Ao
final, o usuário recebe o resumo completo da execução. A qualquer momento:

```bash
onp-spec resumo production-readiness --tabela   # a tabela de andamento
onp-spec resumo production-readiness            # o resumo em texto
```

