# Tasks: Prontidão de produção

> feature: production-readiness

## T-001 — Tornar o build reproduzível e contínuo [concluida]
- Refs: US-001, AC-001
- Arquivos: pom.xml, mvnw, mvnw.cmd, .mvn/wrapper/maven-wrapper.properties, .github/workflows/ci.yml, README.md, core/src/test/java/com/appsdeveloperblog/core/BuildReproducibilityTest.java, onpspec.config.json, .spec/constituicao.md
- Modelo: gpt-5.6-terra
- Esforço: medio
- Notas: incluir `core` no reactor, instalar o Wrapper oficial no modo somente scripts, documentar o comando e configurar CI com Java 17.

## T-002 — Corrigir e testar o contrato de criação de produtos [concluida]
- Refs: US-002, AC-002, AC-003
- Arquivos: products-service/pom.xml, products-service/src/main/java/com/appsdeveloperblog/products/dto/ProductCreationRequest.java, products-service/src/test/java/com/appsdeveloperblog/products/web/controller/ProductsControllerTest.java
- Modelo: gpt-5.6-terra
- Esforço: medio
- Notas: permitir desserialização pelo Jackson, exigir quantidade e provar respostas 201/400 sem subir Kafka.

## T-003 — Externalizar e limitar a integração de pagamentos [concluida]
- Refs: US-003, AC-004, AC-005
- Arquivos: payments-service/pom.xml, payments-service/src/main/java/com/appsdeveloperblog/payments/config/PaymentProcessorProperties.java, payments-service/src/main/java/com/appsdeveloperblog/payments/config/ApplicationConfig.java, payments-service/src/main/java/com/appsdeveloperblog/payments/service/PaymentServiceImpl.java, payments-service/src/main/resources/application.properties, payments-service/src/test/java/com/appsdeveloperblog/payments/config/ApplicationConfigTest.java, payments-service/src/test/java/com/appsdeveloperblog/payments/service/PaymentServiceImplTest.java
- Modelo: gpt-5.6-terra
- Esforço: alto
- Notas: usar propriedades validadas para URL, cartão de demonstração e timeouts; manter o comportamento funcional existente.

## T-004 — Provar o caminho compensatório da saga [concluida]
- Refs: US-004, AC-006, AC-007
- Arquivos: orders-service/src/test/java/com/appsdeveloperblog/orders/saga/OrderSagaTest.java
- Modelo: gpt-5.6-terra
- Esforço: medio
- Notas: testes unitários com captor para os comandos publicados e verificação do histórico.
