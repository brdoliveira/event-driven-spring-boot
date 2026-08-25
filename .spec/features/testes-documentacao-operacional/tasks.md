# Tasks: Testes e documentação operacional

> feature: testes-documentacao-operacional

## T-005 — Testar os contratos HTTP ainda descobertos [concluida]
- Refs: US-005, AC-008, AC-009, AC-010
- Arquivos: orders-service/src/test/java/com/appsdeveloperblog/orders/web/controller/OrdersControllerTest.java, credit-card-processor-service/pom.xml, credit-card-processor-service/src/test/java/com/appsdeveloperblog/ccps/web/controller/CreditCardProcessorControllerTest.java
- Modelo: gpt-5.6-terra
- Esforço: medio
- Notas: usar testes MVC isolados, cobrindo respostas válidas e validação sem iniciar Kafka.

## T-006 — Testar os handlers de produto e pagamento [concluida]
- Refs: US-006, AC-011, AC-012, AC-013
- Arquivos: products-service/src/test/java/com/appsdeveloperblog/products/service/handler/ProductCommandsHandlerTest.java, payments-service/src/test/java/com/appsdeveloperblog/payments/service/handler/PaymentsCommandsHandlerTest.java
- Modelo: gpt-5.6-terra
- Esforço: alto
- Notas: usar mocks e captura dos eventos publicados, verificando caminhos de sucesso, falha e compensação.

## T-007 — Criar guia operacional e relatórios de cobertura [concluida]
- Refs: US-007, AC-014, AC-015
- Arquivos: README.md, pom.xml, .github/workflows/ci.yml, core/src/test/java/com/appsdeveloperblog/core/DocumentationCompletenessTest.java, core/src/test/java/com/appsdeveloperblog/core/BuildQualityConfigurationTest.java, onpspec.config.json
- Modelo: gpt-5.6-terra
- Esforço: medio
- Notas: documentar o fluxo local completo, configurar JaCoCo no reactor, executar `verify` na CI e guardar os relatórios como artefato.
