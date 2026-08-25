# Saga Pattern Spring Boot Demo

Um exemplo local de saga orquestrada com Spring Boot e Kafka. O serviço de
pedidos inicia a saga; produtos reserva ou devolve estoque; pagamentos chama o
processador de cartão; e o serviço de pedidos registra o histórico e coordena
as compensações quando uma etapa falha.

```
POST /orders -> orders-service -> products-commands -> products-service
                                      |                    |
                                      +-- products-events --+
                                                           v
                                                payments-service -> credit-card-processor-service
                                                           |
                                                     payments-events
                                                           v
                                                    orders-service
```

## Requisitos

- Java 17 (`java -version`)
- Docker Desktop com Docker Compose v2 (`docker compose version`)
- Acesso ao Maven Central na primeira execução; não é necessário Maven global.

## Build e verificação

No diretório raiz, o wrapper compila todos os módulos, executa testes e gera
relatórios JaCoCo em cada `target/site/jacoco/`.

```powershell
.\mvnw.cmd -B verify
```

```sh
./mvnw -B verify
```

## Kafka local

Suba o cluster KRaft de três brokers e aguarde os contêineres ficarem ativos:

```sh
docker compose up -d
docker compose ps
```

Os serviços desabilitam a criação automática de tópicos. Crie os seis tópicos
explicitamente antes de iniciar as aplicações:

```sh
for topic in orders-commands orders-events products-commands products-events payments-commands payments-events; do
  docker compose exec kafka-1 kafka-topics.sh --bootstrap-server kafka-1:9192 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 3
done
```

No PowerShell, use:

```powershell
$topics = 'orders-commands', 'orders-events', 'products-commands', 'products-events', 'payments-commands', 'payments-events'
foreach ($topic in $topics) {
  docker compose exec kafka-1 kafka-topics.sh --bootstrap-server kafka-1:9192 --create --if-not-exists --topic $topic --partitions 3 --replication-factor 3
}
```

Para encerrar o ambiente, execute `docker compose down`. Acrescente `-v` apenas
se quiser remover os dados Kafka locais.

## Iniciar os serviços

Abra um terminal para cada comando. As portas locais são: pedidos `8080`,
produtos `8081`, pagamentos `8082` e processador de cartão `8084`.

```powershell
.\mvnw.cmd -pl credit-card-processor-service spring-boot:run
.\mvnw.cmd -pl products-service spring-boot:run
.\mvnw.cmd -pl payments-service spring-boot:run
.\mvnw.cmd -pl orders-service spring-boot:run
```

Em macOS/Linux, execute os mesmos objetivos usando `./mvnw` no lugar de
`.\mvnw.cmd`.

O serviço de pagamentos aceita estas variáveis de ambiente para a integração:

```powershell
$env:PAYMENT_PROCESSOR_SAMPLE_CREDIT_CARD_NUMBER = '374245455400126'
$env:PAYMENT_PROCESSOR_CONNECT_TIMEOUT = '2s'
$env:PAYMENT_PROCESSOR_READ_TIMEOUT = '5s'
```

Elas correspondem ao cartão de demonstração e aos timeouts de conexão/leitura.
O processador local usa `http://localhost:8084` por padrão.

## Demonstrar a saga

Cadastre um produto e guarde o `id` retornado:

```powershell
$product = Invoke-RestMethod -Method Post -Uri http://localhost:8081/products -ContentType application/json -Body '{"name":"Teclado","price":199.90,"quantity":10}'
$product.id
```

```sh
curl -X POST http://localhost:8081/products -H 'Content-Type: application/json' -d '{"name":"Teclado","price":199.90,"quantity":10}'
```

Crie um pedido, substituindo os UUIDs pelo `id` do produto e por um cliente:

```powershell
$customerId = [guid]::NewGuid().ToString()
$order = Invoke-RestMethod -Method Post -Uri http://localhost:8080/orders -ContentType application/json -Body "{`"customerId`":`"$customerId`",`"productId`":`"$($product.id)`",`"productQuantity`":1}"
$order.orderId
```

```sh
curl -X POST http://localhost:8080/orders -H 'Content-Type: application/json' -d '{"customerId":"REPLACE_CUSTOMER_UUID","productId":"REPLACE_PRODUCT_UUID","productQuantity":1}'
```

Consulte o histórico do pedido depois que os consumidores processarem os eventos:

```powershell
Invoke-RestMethod http://localhost:8080/orders/$($order.orderId)/history
```

```sh
curl http://localhost:8080/orders/REPLACE_ORDER_UUID/history
```

## Troubleshooting

- **Tópico inexistente ou consumidor parado:** confirme os seis tópicos com
  `docker compose exec kafka-1 kafka-topics.sh --bootstrap-server kafka-1:9192 --list`.
- **Porta ocupada:** pare o processo que usa 8080, 8081, 8082, 8084 ou as
  portas Kafka 9091–9093; depois inicie novamente o serviço.
- **Falha ao conectar no Kafka:** execute `docker compose ps`, espere os três
  brokers e verifique se as aplicações usam os brokers locais 9091, 9092 e 9093.
- **Build falha no primeiro uso:** confirme Java 17 e conectividade com o Maven
  Central; então repita o comando `verify`.

## Limitações

Este projeto é uma demonstração local. Ele não inclui transactional outbox,
idempotência de consumidores, garantia exatamente-uma-vez, banco de produção
nem testes ponta a ponta com um cluster Kafka real.
