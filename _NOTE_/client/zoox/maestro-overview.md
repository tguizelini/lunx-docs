# Maestro Project Overview

## Visão Geral

**Maestro** é uma plataforma de microserviços desenvolvida em Kotlin/Spring Boot que gerencia o sistema de dispatch (despacho) da Zoox, uma empresa de mobilidade autônoma. O projeto é responsável por orquestrar missões de veículos autônomos, gerenciar usuários, calcular preços, processar pagamentos, coordenar robôs e fornecer APIs para aplicações móveis e administrativas.

O Maestro funciona como o "cérebro" do sistema de dispatch, coordenando todas as operações necessárias para que veículos autônomos possam realizar viagens de passageiros de forma eficiente e segura.

## Arquitetura Geral

O projeto segue uma arquitetura de microserviços, onde cada serviço tem responsabilidades bem definidas e se comunica através de:
- **REST APIs** para comunicação síncrona
- **Apache Kafka** para comunicação assíncrona via eventos
- **PostgreSQL** para persistência de dados
- **Protocol Buffers (Protobuf)** para serialização de mensagens

## Principais Serviços e Responsabilidades

### 1. Mission Service (`mission/`)

**Responsabilidade Principal:** Gerencia o ciclo de vida completo de missões (viagens) de veículos autônomos.

**Principais Features:**
- Criação e gerenciamento de missões
- Rastreamento do status e progresso de missões
- Processamento de atualizações de missões via Kafka
- Consumo de eventos de robôs ociosos e não saudáveis
- Gerenciamento de cancelamentos de missões
- APIs REST para consulta e gerenciamento de missões (v2)

**Exemplos de Uso:**
- Quando um usuário solicita uma viagem, uma missão é criada
- O serviço monitora o progresso da missão através de eventos Kafka
- Atualiza o status da missão conforme o robô avança na rota
- Processa cancelamentos e reagenda missões quando necessário

**Comunicação:**
- **Consome Kafka:** `mission_update`, `robot_unhealthy`, `mission_cancellation`
- **Publica Kafka:** Eventos de criação e atualização de missões
- **REST APIs:** `/v2/missions` para consulta e gerenciamento

### 2. Auto-Dispatch Service (`auto-dispatch/`)

**Responsabilidade Principal:** Sistema inteligente de atribuição automática de missões para robôs disponíveis.

**Principais Features:**
- Algoritmos de matching entre missões pendentes e robôs disponíveis
- Otimização de atribuições baseada em proximidade, capacidade e estado
- Processamento em batch de atribuições pendentes
- Consumo de eventos de robôs ociosos para trigger de atribuições

**Exemplos de Uso:**
- Quando um robô fica ocioso, o serviço verifica missões pendentes na área
- Calcula a melhor atribuição considerando distância, tempo estimado e estado do robô
- Atribui automaticamente a missão ao robô mais adequado

**Comunicação:**
- **Consome Kafka:** `pending-assignments`, `robot-idle`
- **Publica Kafka:** Eventos de atribuição de missões
- **REST APIs:** Possivelmente APIs administrativas para configuração

### 3. Mobile Gateway (`mobile-gateway/`)

**Responsabilidade Principal:** Gateway de API para aplicações móveis, fornecendo endpoints para usuários finais.

**Principais Features:**
- APIs REST para gerenciamento de viagens (trips)
- APIs para pontos de pickup/dropoff (PUDO - Pick Up Drop Off)
- Gerenciamento de usuários via API
- Versões v1 e v2 das APIs
- Autenticação e autorização de requisições móveis

**Exemplos de Uso:**
- App móvel solicita criação de viagem: `POST /v2/trips`
- Consulta de pontos de pickup disponíveis: `GET /v2/pudos`
- Gerenciamento de perfil de usuário: `GET /v2/users/{id}`

**Comunicação:**
- **REST APIs:** `/v1` e `/v2` endpoints para trips, pudos, users
- **Chama outros serviços:** Mission, User, Atlas, Pricing via clientes REST

### 4. Admin Gateway (`admin-gateway/`)

**Responsabilidade Principal:** Gateway para funcionalidades administrativas internas da Zoox.

**Principais Features:**
- Gerenciamento de usuários administrativos
- Gerenciamento de serviços de viagem
- Configuração de missões
- Configuração de robôs
- Integração com OpenSearch para busca de resultados de missões
- Autenticação SSO/Azure OAuth2

**Exemplos de Uso:**
- Administradores criam/configuram usuários do sistema
- Gerenciamento de configurações de robôs da frota
- Visualização e análise de missões através de OpenSearch

**Comunicação:**
- **REST APIs:** Endpoints administrativos diversos
- **Integrações:** OpenSearch, outros serviços internos

### 5. Billing Service (`billing/`)

**Responsabilidade Principal:** Gerenciamento de faturas e processamento de pagamentos para viagens.

**Principais Features:**
- Criação e processamento de faturas (invoices)
- Integração com Stripe para processamento de pagamentos
- Consumo de eventos de missões para gerar faturas
- Processamento de reembolsos (refunds)
- Gerenciamento de estados de faturas

**Exemplos de Uso:**
- Quando uma missão é completada, uma fatura é criada automaticamente
- Processamento de pagamento via Stripe quando a fatura é gerada
- Processamento de reembolsos quando uma viagem é cancelada

**Comunicação:**
- **Consome Kafka:** `mission_update` (para criar faturas), `incoming_refund` (para reembolsos)
- **Integrações:** Stripe API para pagamentos
- **REST APIs:** Endpoints para consulta de faturas

### 6. Pricing Service (`pricing/`)

**Responsabilidade Principal:** Cálculo de preços estimados para viagens antes da solicitação do usuário.

**Principais Features:**
- Cálculo de preços baseado em distância, tempo e outros fatores
- Estimativas de preço antes da confirmação da viagem
- Armazenamento de histórico de preços

**Exemplos de Uso:**
- Usuário visualiza preço estimado antes de confirmar viagem
- Cálculo dinâmico baseado em condições de tráfego e demanda

**Comunicação:**
- **REST APIs:** Endpoints para cálculo de preços
- **Chamado por:** Mobile Gateway quando usuário solicita estimativa

### 7. User Service (`user/`)

**Responsabilidade Principal:** Gerenciamento de recursos de usuários no ecossistema de dispatch.

**Principais Features:**
- CRUD completo de usuários
- Gerenciamento de perfis de usuários
- Autenticação e autorização
- Histórico de viagens por usuário

**Exemplos de Uso:**
- Criação de novo usuário no sistema
- Atualização de perfil de usuário
- Consulta de histórico de viagens de um usuário

**Comunicação:**
- **REST APIs:** `/v1/users` endpoints
- **Chamado por:** Mobile Gateway, Admin Gateway

### 8. Robot Service (`robot/`)

**Responsabilidade Principal:** Gerenciamento de informações e estado dos robôs da frota.

**Principais Features:**
- Rastreamento de estado dos robôs (ocioso, em missão, indisponível)
- Gerenciamento de configurações de robôs
- Integração com sistemas de robôs (RSM - Robot State Manager)
- Publicação de eventos de estado de robôs

**Exemplos de Uso:**
- Robô reporta mudança de estado (ocioso → em missão)
- Sistema consulta disponibilidade de robôs na área
- Configuração de capacidades e restrições de robôs

**Comunicação:**
- **Publica Kafka:** Eventos de estado de robô (`robot-idle`, `robot_unhealthy`)
- **REST APIs:** Endpoints para consulta e configuração de robôs
- **Integrações:** RSM interface via Protocol Buffers

### 9. Robot Gateway (`robot-gateway/`)

**Responsabilidade Principal:** Gateway para comunicação entre Maestro e sistemas de robôs.

**Principais Features:**
- Tradução entre APIs do Maestro e protocolos de robôs
- Gerenciamento de conexões com robôs
- Roteamento de comandos para robôs específicos

**Comunicação:**
- **REST APIs:** Endpoints para comunicação com robôs
- **Protocolos:** RSM interface, Protocol Buffers

### 10. Atlas Service (`atlas/`)

**Responsabilidade Principal:** Fornece features geográficas necessárias para operação da frota em uma região.

**Principais Features:**
- Gerenciamento de áreas de serviço (service areas)
- Geofences (cercas virtuais)
- Pontos de interesse (POIs)
- Zonas PUDO (Pick Up Drop Off)
- Cache Redis para dados geográficos frequentes

**Exemplos de Uso:**
- Verificação se um ponto está dentro da área de serviço
- Consulta de zonas PUDO disponíveis em uma localização
- Validação de geofences para operações de robôs

**Comunicação:**
- **REST APIs:** Endpoints para consulta de dados geográficos
- **Cache:** Redis para performance
- **Chamado por:** Mobile Gateway, Auto-Dispatch, Mission

### 11. Route Smoother Service (`route-smoother/`)

**Responsabilidade Principal:** Suavização e otimização de rotas usando Mapbox Map Matching API.

**Principais Features:**
- Map matching de coordenadas GPS para rotas viáveis
- Suavização de rotas (smooth routes)
- Integração com Mapbox API
- Cache de rotas para performance

**Exemplos de Uso:**
- Quando criando uma viagem, suaviza a rota proposta
- Corrige coordenadas GPS para alinhar com estradas reais
- Otimiza rotas para melhor experiência do passageiro

**Comunicação:**
- **REST APIs:** `/v1/mapmatch_coordinate`, `/v1/smooth_route`
- **Integrações:** Mapbox API
- **Chamado por:** Mobile Gateway ao criar viagens

### 12. Planned Run Service (`planned-run/`)

**Responsabilidade Principal:** Gerenciamento de corridas planejadas (scheduled runs).

**Principais Features:**
- Criação e gerenciamento de planos de corridas
- Agendamento de corridas recorrentes
- Execução de corridas planejadas

**Exemplos de Uso:**
- Criação de corridas recorrentes (ex: transporte de funcionários)
- Agendamento de corridas para horários específicos

**Comunicação:**
- **REST APIs:** `/v1/plans` endpoints
- **Integrações:** Possivelmente com Scheduled Jobs

### 13. Notification Service (`notification/`)

**Responsabilidade Principal:** Envio de notificações para usuários e sistemas.

**Principais Features:**
- Notificações push para aplicativos móveis
- Notificações por email
- Notificações SMS
- Gerenciamento de preferências de notificação

**Exemplos de Uso:**
- Notificação quando robô está chegando
- Notificação de confirmação de viagem
- Alertas de cancelamento ou atrasos

**Comunicação:**
- **Consome Kafka:** Eventos que requerem notificações
- **Integrações:** Serviços de push, email, SMS

### 14. Promotion Service (`promotion/`)

**Responsabilidade Principal:** Gerenciamento de promoções e descontos.

**Principais Features:**
- Criação e gerenciamento de promoções
- Aplicação de códigos promocionais
- Cálculo de descontos
- Validação de elegibilidade de promoções

**Comunicação:**
- **REST APIs:** Endpoints para gerenciamento de promoções
- **Chamado por:** Pricing Service ao calcular preços

### 15. Event Streams Service (`event-streams/`)

**Responsabilidade Principal:** Processamento de streams de eventos Kafka e transformação/distribuição.

**Principais Features:**
- Processamento de streams Kafka
- Transformação de eventos
- Roteamento de eventos para diferentes tópicos
- Agregação de eventos

**Comunicação:**
- **Kafka:** Consome e publica em múltiplos tópicos
- **Stream Processing:** Usa Kafka Streams ou similar

### 16. Scheduled Jobs Service (`scheduled-jobs/`)

**Responsabilidade Principal:** Execução de jobs agendados e tarefas periódicas.

**Principais Features:**
- Jobs agendados (cron jobs)
- Tarefas de manutenção periódicas
- Limpeza de dados antigos
- Relatórios agendados

**Exemplos de Uso:**
- Limpeza diária de dados antigos
- Geração de relatórios semanais
- Sincronização periódica com sistemas externos

### 17. Inspector (`inspector/`)

**Responsabilidade Principal:** Ferramenta web para debugging, teste e investigação para Engineering, QA e SDET.

**Principais Features:**
- Interface web React para visualização de dados
- Debugging de missões e robôs
- Consolidação de dados relacionados
- Links para outras ferramentas
- Autenticação SSO/Azure OAuth2
- Proxy para Admin Gateway e Robot Sims

**Exemplos de Uso:**
- Visualização de estado de uma missão específica
- Debugging de problemas com robôs
- Investigação de incidentes

**Comunicação:**
- **REST APIs:** Endpoints customizados para debugging
- **Proxies:** Admin Gateway, Robot Sims
- **UI:** React MPA (Multi-Page Application)

### 18. Robot Sims (`robot-sims/`)

**Responsabilidade Principal:** Simulação de robôs para desenvolvimento e teste.

**Principais Features:**
- Criação de sessões de simulação
- Simulação de comportamento de robôs
- Testes de integração com Maestro
- Emissão de eventos simulados

**Exemplos de Uso:**
- Desenvolvedores testam integração sem robôs físicos
- QA executa testes automatizados com robôs simulados
- Debugging de fluxos de missão

**Comunicação:**
- **REST APIs:** Endpoints para gerenciamento de simulações
- **Publica Kafka:** Eventos simulados de robôs

### 19. Invoice Sweeper (`invoice-sweeper/`)

**Responsabilidade Principal:** Processamento e limpeza de faturas pendentes.

**Principais Features:**
- Processamento de faturas pendentes
- Limpeza de faturas antigas
- Reconciliação de faturas

### 20. Vendor Gateway (`vendor-gateway/`)

**Responsabilidade Principal:** Gateway para integração com fornecedores externos.

**Principais Features:**
- APIs para parceiros e fornecedores
- Integração com serviços de terceiros

### 21. Task Status Consumer (`mission/task-status-consumer/`)

**Responsabilidade Principal:** Consumo e processamento de atualizações de status de tarefas.

**Principais Features:**
- Consumo de eventos de status de tarefas
- Processamento de atualizações
- Sincronização de estados

### 22. ETA Update Consumer (`mission/eta-update-consumer/`)

**Responsabilidade Principal:** Consumo e processamento de atualizações de ETA (Estimated Time of Arrival).

**Principais Features:**
- Consumo de eventos de atualização de ETA
- Atualização de ETAs de missões
- Notificação de mudanças de ETA

## Componentes Compartilhados (`shared/`)

O projeto inclui vários módulos compartilhados que são utilizados por múltiplos serviços:

- **events-library**: Biblioteca para consumo/publicação de mensagens Protobuf via Kafka
- **utils-***: Múltiplas bibliotecas utilitárias (logging, tracing, session, http, etc.)
- **payments**: Integração com sistemas de pagamento (Stripe)
- **testcontainers**: Suporte para testes de integração
- **observability**: Instrumentação para métricas e tracing (OpenTelemetry)
- **maestro-autoconfigure**: Auto-configuração Spring Boot para serviços Maestro

## Protocol Buffers (`proto/`)

O projeto define vários schemas Protobuf para comunicação:
- **rsm-interface**: Interface com Robot State Manager
- **routing-data**: Dados de roteamento
- **zrn**: Zoox Reference Network (dados geográficos)
- **vehicle-eta-proto**: Protocolos de ETA de veículos
- **low-bandwidth-telemetry**: Telemetria de baixa largura de banda

## Fluxo de Comunicação Entre Serviços

### Exemplo 1: Criação de uma Viagem (Trip)

```
1. Mobile App → Mobile Gateway
   POST /v2/trips
   { origem, destino, userId }

2. Mobile Gateway → Route Smoother
   POST /v1/smooth_route
   { polyline }

3. Mobile Gateway → Pricing Service
   GET /v1/prices/estimate
   { origem, destino }

4. Mobile Gateway → Mission Service
   POST /v2/missions
   { trip details }

5. Mission Service → Kafka
   Publica: mission_create.v2

6. Auto-Dispatch → Kafka
   Consome: pending-assignments
   
7. Auto-Dispatch → Robot Service
   Consulta robôs disponíveis

8. Auto-Dispatch → Kafka
   Publica: mission_assigned

9. Robot Service → Kafka
   Publica: robot-idle → robot-in-mission

10. Mission Service → Kafka
    Consome: mission_update
    Atualiza status da missão

11. Billing Service → Kafka
    Consome: mission_update (completed)
    Cria invoice

12. Billing Service → Stripe
    Processa pagamento

13. Notification Service → Kafka
    Consome: mission_update
    Envia notificações ao usuário
```

### Exemplo 2: Atribuição Automática de Missão

```
1. Robot Service detecta robô ocioso
   → Publica Kafka: robot-idle

2. Auto-Dispatch consome robot-idle
   → Consulta missões pendentes
   → Calcula melhor match
   → Atribui missão ao robô

3. Auto-Dispatch → Kafka
   Publica: mission_assigned

4. Mission Service consome mission_assigned
   → Atualiza missão com robô atribuído

5. Robot Gateway → Robot
   Envia comando via RSM interface
```

## Diagrama de Arquitetura de Comunicação

```
┌─────────────────┐
│   Mobile App    │
└────────┬────────┘
         │ REST API
         ▼
┌─────────────────┐      ┌──────────────┐      ┌──────────────┐
│ Mobile Gateway  │──────│Route Smoother │      │Pricing Service│
└────────┬────────┘      └──────────────┘      └──────────────┘
         │
         │ REST API
         ▼
┌─────────────────┐
│ Mission Service │◄─────┐
└────────┬────────┘      │
         │               │ Kafka Events
         │ Kafka         │
         ▼               │
    ┌────────┐           │
    │ Kafka  │◄──────────┘
    │Broker  │
    └───┬────┘
        │
        │ Kafka Events
        ▼
┌─────────────────┐      ┌──────────────┐      ┌──────────────┐
│ Auto-Dispatch   │      │Billing Service│     │Notification │
└────────┬────────┘      └──────┬───────┘     │  Service    │
         │                      │              └──────────────┘
         │                      │
         │ REST API             │ Stripe API
         ▼                      ▼
┌─────────────────┐      ┌──────────────┐
│ Robot Service   │      │   Stripe    │
└────────┬────────┘      └──────────────┘
         │
         │ RSM Interface (Protobuf)
         ▼
┌─────────────────┐
│  Robot Gateway  │
└────────┬────────┘
         │
         ▼
    ┌────────┐
    │ Robots │
    └────────┘

┌─────────────────┐
│  Admin Gateway   │◄───┐
└────────┬─────────┘    │
         │              │ REST API
         │              │
         ▼              │
┌─────────────────┐    │
│  User Service   │    │
└─────────────────┘    │
                       │
┌─────────────────┐    │
│  Inspector UI   │────┘
│  (React MPA)    │
└─────────────────┘
```

## Padrões de Comunicação

### Comunicação Síncrona (REST)
- **Mobile Gateway ↔ Route Smoother**: Suavização de rotas
- **Mobile Gateway ↔ Pricing**: Cálculo de preços
- **Mobile Gateway ↔ Mission**: Criação de missões
- **Admin Gateway ↔ User**: Gerenciamento de usuários
- **Auto-Dispatch ↔ Robot**: Consulta de robôs disponíveis

### Comunicação Assíncrona (Kafka)
- **Eventos de Missão**: `mission_create.v2`, `mission_update`, `mission_cancellation`
- **Eventos de Robô**: `robot-idle`, `robot_unhealthy`
- **Eventos de Atribuição**: `pending-assignments`, `mission_assigned`
- **Eventos de Pagamento**: `incoming_refund`
- **Eventos de ETA**: Atualizações de tempo estimado de chegada

### Persistência (PostgreSQL)
- Cada serviço tem seu próprio banco de dados PostgreSQL
- Migrações gerenciadas via Flyway
- Dados isolados por serviço

## Tecnologias Principais

- **Linguagem**: Kotlin
- **Framework**: Spring Boot 3.x
- **Banco de Dados**: PostgreSQL
- **Mensageria**: Apache Kafka
- **Serialização**: Protocol Buffers (Protobuf)
- **Build**: Gradle
- **Containerização**: Docker
- **Orquestração**: Kubernetes
- **Observabilidade**: OpenTelemetry, Prometheus
- **Cache**: Redis (para Atlas Service)
- **Integrações**: Stripe (pagamentos), Mapbox (rotas)

## Conclusão

O Maestro é um sistema complexo e distribuído que coordena todas as operações necessárias para o funcionamento de uma frota de veículos autônomos. A arquitetura de microserviços permite escalabilidade, manutenibilidade e desenvolvimento paralelo de diferentes equipes. A comunicação via Kafka garante desacoplamento e resiliência, enquanto APIs REST fornecem interfaces claras para integração síncrona quando necessário.
