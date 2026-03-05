# sboot-sqs

Sistema de demonstração de integração entre Spring Boot e Amazon SQS (Simple Queue Service), configurado para usar AWS real seguindo os padrões da documentação oficial do Spring Cloud AWS.

## 📋 Sobre o Projeto

Este projeto demonstra como implementar um sistema de mensageria assíncrona usando Spring Boot e AWS SQS. O sistema é composto por:

- **Producer**: Endpoint REST que recebe mensagens e as envia para uma fila SQS
- **Consumer**: Listener que consome mensagens da fila SQS de forma assíncrona

O projeto está configurado para usar **AWS real** seguindo os padrões da documentação oficial do Spring Cloud AWS.

## 🛠️ Tecnologias Utilizadas

- **Java 17**: Linguagem de programação
- **Spring Boot 3.4.1**: Framework para desenvolvimento de aplicações Java
- **Spring Cloud AWS SQS 3.0.1**: Integração com Amazon SQS
- **Lombok**: Redução de boilerplate (getters, setters, construtores)
- **Maven**: Gerenciador de dependências e build

## 📦 Dependências Principais

### Spring Boot Starter Web
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
Fornece funcionalidades REST e servidor web embutido.

### Spring Cloud AWS SQS
```xml
<dependency>
    <groupId>io.awspring.cloud</groupId>
    <artifactId>spring-cloud-aws-starter-sqs</artifactId>
</dependency>
```
Integração com Amazon SQS para envio e recebimento de mensagens.

### Spring Boot Starter Test
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```
Dependências para testes automatizados.

### Lombok
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
Reduz código boilerplate (getters, setters, construtores).

## 🚀 Como Executar

### Pré-requisitos

1. **Java 17** instalado
2. **Maven** instalado (ou use o wrapper `mvnw` incluído no projeto)
3. **Credenciais AWS** configuradas (veja seção de Configuração AWS abaixo)

### Configuração AWS

O Spring Cloud AWS detecta credenciais na seguinte ordem (conforme documentação oficial):

1. **IAM Role** (recomendado para produção em EC2/ECS/Lambda)
2. **AWS Profile** (`~/.aws/credentials` e `~/.aws/config`)
3. **Variáveis de ambiente**: `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY`
4. **Propriedades** no `application.properties` (não recomendado para produção)

#### Opção 1: Usando AWS Profile (Recomendado para desenvolvimento)

```bash
# Configure o AWS CLI
aws configure

# Ou crie manualmente ~/.aws/credentials
[default]
aws_access_key_id = YOUR_ACCESS_KEY
aws_secret_access_key = YOUR_SECRET_KEY
```

#### Opção 2: Usando Variáveis de Ambiente

```bash
export AWS_ACCESS_KEY_ID=YOUR_ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=YOUR_SECRET_KEY
export AWS_REGION=us-east-1
```

#### Opção 3: Usando IAM Role (Produção)

Quando executando em EC2, ECS ou Lambda, configure uma IAM Role com permissões SQS. O Spring Cloud AWS detecta automaticamente.

### Passos para Execução

1. **Configure as credenciais AWS** (veja seção acima)

2. **Crie a fila SQS na AWS**:
   ```bash
   aws sqs create-queue --queue-name minha-fila --region us-east-1
   ```
   
   Ou via Console AWS: https://console.aws.amazon.com/sqs/

3. **Configure o `application.properties`**:
   ```properties
   spring.cloud.aws.region.static=us-east-1
   aws.sqs.queue-name=minha-fila
   aws.sqs.account-id=123456789012  # Seu Account ID AWS
   ```

4. **Execute a aplicação Spring Boot**:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Ou usando o wrapper do Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

5. **A aplicação estará disponível em**: `http://localhost:8082`

### Testando o Sistema

**Enviar uma mensagem** (Producer):
```bash
curl -X POST http://localhost:8082/producer/send \
  -H "Content-Type: application/json" \
  -d '{"content": "Minha mensagem de teste"}'
```

A mensagem será enviada para a fila SQS e automaticamente consumida pelo `MyConsumer`, que imprimirá no console:
```
SQS::Message received = Minha mensagem de teste
```

## 📁 Estrutura do Projeto

```
sboot-sqs/
├── src/
│   ├── main/
│   │   ├── java/tech/buildrun/sboot_sqs/
│   │   │   ├── config/
│   │   │   │   └── SqsProperties.java      # Propriedades SQS centralizadas
│   │   │   ├── consumer/
│   │   │   │   └── MyConsumer.java         # Consumidor de mensagens SQS
│   │   │   ├── controller/
│   │   │   │   └── ProducerController.java # Endpoint REST para envio
│   │   │   ├── dto/
│   │   │   │   └── MyMessage.java          # DTO de mensagem
│   │   │   ├── service/
│   │   │   │   └── MessageSender.java      # Serviço de envio de mensagens
│   │   │   └── SbootSqsApplication.java    # Classe principal
│   │   └── resources/
│   │       └── application.properties       # Configurações da aplicação
│   └── test/
├── pom.xml                                  # Dependências Maven
└── README.md
```

## 🎯 Principais Pontos e Responsabilidades

### 1. **SqsProperties** (`config/SqsProperties.java`)
- **Responsabilidade**: Centralizar todas as propriedades de configuração do SQS
- Integra com propriedades padrão do Spring Cloud AWS (`spring.cloud.aws.*`)
- Lê região de `spring.cloud.aws.region.static`
- Fornece métodos para obter URLs de filas AWS
- Usa Lombok para reduzir boilerplate (getters/setters automáticos)
- **Propriedades customizadas**: `queueName`, `accountId`

### 2. **MessageSender** (`service/MessageSender.java`)
- **Responsabilidade**: Encapsular a lógica de envio de mensagens SQS
- **Separação de responsabilidades**: Controller não conhece detalhes de filas
- Método público `send(MyMessage)` que escolhe a fila internamente
- Preparado para métodos específicos de negócio (ex: `sendPaymentStatus()`)
- Usa `SqsTemplate` e `SqsProperties` para envio

### 3. **ProducerController** (`controller/ProducerController.java`)
- **Responsabilidade**: Expor endpoint REST para envio de mensagens
- Endpoint: `POST /producer/send`
- **Não conhece detalhes de SQS**: Delega toda lógica para `MessageSender`
- Recebe `MyMessage` via JSON e chama `messageSender.send()`
- Usa `@RequiredArgsConstructor` do Lombok para injeção de dependências

### 4. **MyConsumer** (`consumer/MyConsumer.java`)
- **Responsabilidade**: Consumir mensagens da fila SQS
- Utiliza `@SqsListener(queueNames = "${aws.sqs.queue-name}")` seguindo padrão da documentação oficial
- Lê o nome da fila do `application.properties` via property placeholder
- Processa mensagens de forma assíncrona automaticamente (auto-acknowledgement)
- Imprime o conteúdo da mensagem no console

### 5. **MyMessage** (`dto/MyMessage.java`)
- **Responsabilidade**: Representar a estrutura de dados das mensagens
- Record Java com campo `content` (String)
- Usado tanto no envio quanto no consumo de mensagens

### 6. **SbootSqsApplication** (`SbootSqsApplication.java`)
- **Responsabilidade**: Classe principal da aplicação Spring Boot
- Inicializa o contexto Spring e inicia o servidor embutido

### 7. **application.properties**
- **Responsabilidade**: Configurações centralizadas da aplicação
- Define nome da aplicação, porta do servidor e todas as propriedades SQS

## 🔧 Configurações

### application.properties

#### Configuração para AWS Real (Padrão)
```properties
spring.application.name=java-sqs
server.port=8082

# Spring Cloud AWS Configuration (padrão da documentação oficial)
spring.cloud.aws.region.static=us-east-1

# Configuração SQS customizada
aws.sqs.queue-name=minha-fila
aws.sqs.account-id=123456789012  # Seu Account ID AWS
```

### Propriedades Spring Cloud AWS

O projeto usa as propriedades padrão do Spring Cloud AWS conforme documentação oficial:

- **`spring.cloud.aws.region.static`**: Região AWS (obrigatória)
- **`spring.cloud.aws.sqs.enabled`**: Habilita SQS (padrão: `true`)
- **`spring.cloud.aws.credentials.*`**: Credenciais (opcional, usa detecção automática)

### Propriedades Customizadas

- **`aws.sqs.queue-name`**: Nome da fila SQS
- **`aws.sqs.account-id`**: ID da conta AWS (necessário para construir URL da fila)

### Arquitetura e Boas Práticas

O projeto segue princípios de **separação de responsabilidades** e **encapsulamento**:

- ✅ **Controller** não conhece detalhes de SQS - apenas delega para o service
- ✅ **Service** encapsula a lógica de qual fila usar para cada tipo de mensagem
- ✅ **Configurações** centralizadas em `SqsProperties` e `application.properties`
- ✅ **Property placeholders** usados conforme documentação oficial do Spring Cloud AWS
- ✅ **Lombok** reduz boilerplate mantendo código limpo

## 📝 Notas Importantes

- O projeto está configurado para usar **AWS real** seguindo padrões da documentação oficial do Spring Cloud AWS
- Todas as configurações estão centralizadas no `application.properties` via `SqsProperties`
- O controller não conhece detalhes de filas SQS - toda lógica está no `MessageSender`
- O `@SqsListener` usa property placeholder `${aws.sqs.queue-name}` conforme padrão da documentação oficial
- **Credenciais AWS**: Configure via IAM Role (produção), AWS Profile ou variáveis de ambiente
- O Spring Cloud AWS detecta credenciais automaticamente na ordem: IAM Role → Profile → Variáveis de ambiente → Properties
- **Account ID**: Necessário para construir URLs de filas. Encontre em: AWS Console → Support → Account ID
- O Spring Cloud AWS cria automaticamente o `SqsAsyncClient` usando as propriedades configuradas

## 🔗 Recursos Adicionais

- [Spring Cloud AWS SQS Documentation](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html)
- [Amazon SQS Documentation](https://docs.aws.amazon.com/sqs/)
