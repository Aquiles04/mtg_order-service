
# Projeto Final End-to-End

> Projeto individual, tema livre. 

## Descrição

Construa um fluxo completo de negócio (ex.: pedidos, assinaturas, billing, logística) com arquitetura hexagonal, BFF, mensageria (Kafka/Rabbit), resiliência, cache, observabilidade e deploy em Kubernetes via Helm. Infra de dev provisionada por Terraform. Documentação viva (C4 + ADRs) e automação de CI/CD.

## Entregáveis

-  Repositório com código, README de execução, /docs (C4 + ADRs), docker-compose, charts Helm, Terraform (dev).
-  Dashboards (métricas/tracing) e runbook curto de incidentes.
-  PR final integrando tudo.

## Critérios de avaliação (com pesos)

-  20% Arquitetura & Domínio: portas/adaptadores, linguagem ubíqua, testes de use case.
-  20% Mensageria & Orquestração: tópicos/filas, idempotência, Saga (coreografia ou orquestração) e/ou CQRS.
-  15% BFF & Contrato: view models centrados no cliente, fallback sem vazar domínio.
-  10% Cache & Desempenho: L1/L2, invalidação por evento, métricas p95/p99.
-  10% Resiliência: timeouts, retry com jitter, circuit breaker, bulkhead/rate limit.
-  10% Kubernetes & Helm: probes, rollout, HPA, NetworkPolicy.
-  10% IaC & CI/CD: Terraform (state remoto), pipeline validate > plan > apply.
-  5% FinOps & Governança: tags, budget/alertas, quotas ou KEDA.


# MTG Order Service - Full Business Flow (Orders, Subscriptions, Billing, Logistics)

Este repositório contém um esqueleto de microserviço em Java 21 + Spring Boot seguindo arquitetura hexagonal, com:
- Autenticação JWT (access + refresh tokens) via JPA (usuários + refresh tokens)
- Persistência JPA (Postgres)
- Kafka para integração entre serviços (event-driven)
- Fluxo de negócio com saga (coreografia): pedido -> billing -> fulfillment/logistica
- Idempotência básica (Idempotency key suportado no controller principal)
- Docker Compose com Postgres, Zookeeper e Kafka para facilitar execução local

## Componentes e responsabilidades

- **Order Service** (neste projeto)
  - Recebe requisições HTTP para criar pedidos de cartas (`POST /api/orders`)
  - Persiste `CardOrder` no banco
  - Publica evento `order.created` no Kafka

- **Billing (Coreografia)**
  - Ouve `order.created`
  - Processa pagamento (simulado) e publica `payment.processed` ou `payment.failed`

- **Logistics / Fulfillment**
  - Ouve `payment.processed`
  - Atualiza status do pedido para `PAID` e publica `order.fulfill`
  - O Fulfillment ouve `order.fulfill` e publica `order.shipped` quando enviado
  - Logistics atualiza pedido para `FULFILLED` ao receber `order.shipped`

> Observação: Todos os componentes acima estão implementados como listeners/adapters nesse projeto para demonstrar o fluxo (coreografia). Em um ambiente real, cada responsabilidade poderia ser um microserviço independente.

## Topics / Eventos usados
- `order.created` — publicado quando novo pedido é criado
- `payment.processed` — publicado pelo serviço de billing após pagamento bem sucedido
- `payment.failed` — billing publica em caso de falha
- `order.fulfill` — pedido pronto para fulfillment
- `order.shipped` — fulfillment finaliza e informa que foi enviado

## Como executar localmente (com Docker)

1. Ajuste `src/main/resources/application.yml` se necessário (principalmente `jwt.secret`).
2. Subir infra com Docker Compose (Postgres + Zookeeper + Kafka):
   ```bash
   docker-compose up -d
   ```
3. Build e run da aplicação:
   ```bash
   mvn -DskipTests package
   java -jar target/mtg-order-service-0.0.1-SNAPSHOT.jar
   ```
   Ou, em desenvolvimento:
   ```bash
   mvn spring-boot:run
   ```

4. Teste o fluxo:
   - Registrar usuário (opcional) / `POST /api/auth/register` payload `{"username":"user","password":"pass"}`
   - Login `/api/auth/login` -> obter `accessToken` e `refreshToken`.
   - Criar pedido: `POST /api/orders` com body `{"buyerId":"user","cardName":"Black Lotus","quantity":1}`
   - Observe que a criação publica `order.created`, o billing escuta e publica `payment.processed`, o logistics ouve e publica `order.fulfill`, o fulfillment publica `order.shipped` e o pedido é atualizado para `FULFILLED`.
   - Você pode consultar `GET /api/orders` para ver o status atualizado (pode demorar 1-2s pela coreografia em listeners).

## Notas de design e próximos passos
- **Saga**: implementado via *coreografia* (eventos Kafka). Para orquestração, crie um orquestrador que mantenha a saga state machine.  
- **Idempotência**: controller aceita header `Idempotency-Key` (implemente armazenamento e lookup para respostas idempotentes).  
- **Resiliência**: adicionar retries/exponential backoff nas operações Kafka/DB; dead-letter topics para mensagens que repetidamente falham.  
- **Segurança**: ajuste `jwt.secret` e use HTTPS em produção. Tokens e senhas devem ser guardados em secret manager.  
- **Separação**: extraia Billing/Fulfillment/Logistics para microsserviços independentes para escalabilidade e deploy independente.

## Arquivos importantes
- `docker-compose.yml` - sobe Postgres, Zookeeper, Kafka
- `src/main/resources/application.yml` - configurações da app
- `src/main/java/com/mtg/orders/adapters/in/web/OrderController.java` - endpoint de pedidos
- `src/main/java/com/mtg/orders/adapters/out/messaging/*Listener.java` - listeners que compõem a coreografia
- `src/main/java/com/mtg/orders/domain/model/CardOrder.java` - entidade de pedido


