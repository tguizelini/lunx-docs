# Guia de Configuração AWS - sboot-sqs

Este documento fornece um passo-a-passo completo e didático para configurar o projeto `sboot-sqs` para usar Amazon SQS real na AWS.

## 📋 Pré-requisitos

Antes de começar, você precisa ter:

1. **Conta AWS ativa** com acesso ao console AWS
2. **AWS CLI instalado** ([instruções de instalação](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html))
3. **Permissões adequadas** na sua conta AWS para:
   - Criar e gerenciar filas SQS
   - Criar usuários IAM e políticas
   - Criar roles IAM (se necessário)

## 🎯 Visão Geral do Processo

O processo de configuração envolve 4 etapas principais:

1. **Criar a fila SQS na AWS**
2. **Obter o Account ID da sua conta AWS**
3. **Configurar credenciais AWS** (escolha uma das opções)
4. **Configurar o application.properties**

---

## Passo 1: Criar a Fila SQS na AWS

### Opção A: Via Console AWS (Recomendado para iniciantes)

1. Acesse o [Console AWS](https://console.aws.amazon.com/)
2. Faça login com suas credenciais
3. No campo de busca, digite "SQS" e selecione **Amazon SQS**
4. Clique em **Create queue**
5. Configure a fila:
   - **Name**: `minha-fila` (ou o nome que preferir)
   - **Type**: Standard (padrão) ou FIFO (se precisar de ordem garantida)
   - **Region**: Selecione a região desejada (ex: `us-east-1`)
   - Deixe as outras configurações padrão para começar
6. Clique em **Create queue**
7. **Anote o nome da fila** criada

### Opção B: Via AWS CLI

```bash
# Configure suas credenciais primeiro (se ainda não fez)
aws configure

# Crie a fila
aws sqs create-queue \
  --queue-name minha-fila \
  --region us-east-1

# O comando retornará a URL da fila, anote-a
```

**Saída esperada:**
```json
{
    "QueueUrl": "https://sqs.us-east-1.amazonaws.com/123456789012/minha-fila"
}
```

---

## Passo 2: Obter o Account ID da AWS

O Account ID é necessário para construir a URL completa da fila. Você pode obtê-lo de várias formas:

### Opção A: Via Console AWS

1. No canto superior direito do console, clique no seu nome de usuário
2. O Account ID aparece logo abaixo do nome (formato: `123456789012`)

### Opção B: Via AWS CLI

```bash
aws sts get-caller-identity --query Account --output text
```

### Opção C: Extrair da URL da Fila

Se você já criou a fila, o Account ID está na URL:
```
https://sqs.us-east-1.amazonaws.com/123456789012/minha-fila
                                    ^^^^^^^^^^^^
                                    Este é o Account ID
```

---

## Passo 3: Configurar Credenciais AWS

O Spring Cloud AWS detecta credenciais automaticamente na seguinte ordem de prioridade:

1. **IAM Role** (quando executando em EC2/ECS/Lambda)
2. **AWS Profile** (`~/.aws/credentials`)
3. **Variáveis de ambiente** (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`)
4. **Propriedades no application.properties** (não recomendado)

Escolha uma das opções abaixo conforme seu ambiente:

### Opção A: AWS Profile (Recomendado para Desenvolvimento Local)

Esta é a forma mais segura e prática para desenvolvimento local.

#### 3.1. Criar um usuário IAM na AWS

1. Acesse o [Console IAM](https://console.aws.amazon.com/iam/)
2. Clique em **Users** → **Create user**
3. Digite um nome de usuário (ex: `sboot-sqs-user`)
4. Clique em **Next**
5. Em **Set permissions**, selecione **Attach policies directly**
6. Procure e selecione a política `AmazonSQSFullAccess` (ou crie uma política customizada mais restritiva)
7. Clique em **Next** → **Create user**

#### 3.2. Criar Access Keys

1. Clique no usuário criado
2. Vá na aba **Security credentials**
3. Clique em **Create access key**
4. Selecione **Command Line Interface (CLI)**
5. Marque a checkbox de confirmação
6. Clique em **Next** → **Create access key**
7. **IMPORTANTE**: Copie e guarde em local seguro:
   - **Access key ID**
   - **Secret access key** (só aparece uma vez!)

#### 3.3. Configurar AWS CLI

```bash
# Execute o comando de configuração
aws configure

# Você será solicitado a informar:
# AWS Access Key ID: [cole o Access Key ID]
# AWS Secret Access Key: [cole o Secret Access Key]
# Default region name: us-east-1 (ou sua região preferida)
# Default output format: json (ou deixe em branco)
```

Isso criará os arquivos:
- `~/.aws/credentials` (Windows: `C:\Users\SEU_USUARIO\.aws\credentials`)
- `~/.aws/config` (Windows: `C:\Users\SEU_USUARIO\.aws\config`)

#### 3.4. Verificar configuração

```bash
# Teste se está funcionando
aws sts get-caller-identity

# Deve retornar algo como:
# {
#     "UserId": "AIDAXXXXXXXXXXXXXXXXX",
#     "Account": "123456789012",
#     "Arn": "arn:aws:iam::123456789012:user/sboot-sqs-user"
# }
```

**Pronto!** O Spring Cloud AWS detectará automaticamente essas credenciais.

### Opção B: Variáveis de Ambiente (Recomendado para CI/CD)

Configure as variáveis de ambiente no seu sistema:

#### Linux/macOS:

```bash
export AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
export AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
export AWS_REGION=us-east-1
```

Para tornar permanente, adicione ao `~/.bashrc` ou `~/.zshrc`:

```bash
echo 'export AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE' >> ~/.bashrc
echo 'export AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY' >> ~/.bashrc
echo 'export AWS_REGION=us-east-1' >> ~/.bashrc
source ~/.bashrc
```

#### Windows (PowerShell):

```powershell
$env:AWS_ACCESS_KEY_ID="AKIAIOSFODNN7EXAMPLE"
$env:AWS_SECRET_ACCESS_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
$env:AWS_REGION="us-east-1"
```

Para tornar permanente (PowerShell):

```powershell
[System.Environment]::SetEnvironmentVariable('AWS_ACCESS_KEY_ID', 'AKIAIOSFODNN7EXAMPLE', 'User')
[System.Environment]::SetEnvironmentVariable('AWS_SECRET_ACCESS_KEY', 'wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY', 'User')
[System.Environment]::SetEnvironmentVariable('AWS_REGION', 'us-east-1', 'User')
```

#### Windows (CMD):

```cmd
setx AWS_ACCESS_KEY_ID "AKIAIOSFODNN7EXAMPLE"
setx AWS_SECRET_ACCESS_KEY "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
setx AWS_REGION "us-east-1"
```

**Nota**: Reinicie o terminal após usar `setx`.

### Opção C: IAM Role (Recomendado para Produção)

Quando executando em EC2, ECS ou Lambda, use IAM Roles:

1. **EC2**: Crie uma IAM Role com política `AmazonSQSFullAccess` e anexe à instância EC2
2. **ECS**: Configure a task role com as permissões necessárias
3. **Lambda**: Configure a execution role com as permissões necessárias

O Spring Cloud AWS detecta automaticamente a role e não precisa de credenciais adicionais.

---

## Passo 4: Configurar application.properties

Edite o arquivo `src/main/resources/application.properties`:

```properties
spring.application.name=java-sqs
server.port=8082

# Spring Cloud AWS Configuration
spring.cloud.aws.region.static=us-east-1

# Configuracao SQS
spring.cloud.aws.sqs.enabled=true

# Configuracoes customizadas da aplicacao
aws.sqs.queue-name=minha-fila
aws.sqs.account-id=123456789012
```

**Substitua os valores:**
- `spring.cloud.aws.region.static`: Região onde você criou a fila (ex: `us-east-1`, `sa-east-1`, `eu-west-1`)
- `aws.sqs.queue-name`: Nome exato da fila criada no Passo 1
- `aws.sqs.account-id`: Account ID obtido no Passo 2

---

## Passo 5: Testar a Configuração

### 5.1. Executar a aplicação

```bash
./mvnw spring-boot:run
```

### 5.2. Verificar logs de inicialização

Procure por mensagens como:
```
Creating SQS listener container for queue: minha-fila
```

Se houver erros relacionados a credenciais, verifique o Passo 3.

### 5.3. Enviar uma mensagem de teste

```bash
curl -X POST http://localhost:8082/producer/send \
  -H "Content-Type: application/json" \
  -d '{"content": "Mensagem de teste AWS"}'
```

### 5.4. Verificar consumo da mensagem

A mensagem deve aparecer no console da aplicação:
```
SQS::Message received = Mensagem de teste AWS
```

### 5.5. Verificar na AWS (Opcional)

No Console AWS → SQS → Sua fila → **Send and receive messages** → **Poll for messages**

Você deve ver a mensagem processada (ou não ver nada se já foi consumida).

---

## 🔒 Segurança e Boas Práticas

### Políticas IAM Restritivas

Em vez de usar `AmazonSQSFullAccess`, crie uma política customizada mais restritiva:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "sqs:SendMessage",
                "sqs:ReceiveMessage",
                "sqs:DeleteMessage",
                "sqs:GetQueueAttributes",
                "sqs:GetQueueUrl"
            ],
            "Resource": "arn:aws:sqs:us-east-1:123456789012:minha-fila"
        }
    ]
}
```

### Não commitar credenciais

- **NUNCA** adicione credenciais no `application.properties` e faça commit
- Use variáveis de ambiente ou AWS Profile
- Adicione `application.properties` ao `.gitignore` se contiver informações sensíveis

### Rotação de credenciais

- Rotacione Access Keys regularmente (a cada 90 dias é uma boa prática)
- Use IAM Roles em produção sempre que possível

---

## 🐛 Troubleshooting

### Erro: "Unable to load credentials"

**Causa**: Credenciais não encontradas ou inválidas.

**Solução**:
1. Verifique se configurou AWS Profile (`aws configure`)
2. Verifique variáveis de ambiente (`echo $AWS_ACCESS_KEY_ID`)
3. Teste com `aws sts get-caller-identity`

### Erro: "Access Denied" ou "403 Forbidden"

**Causa**: Usuário IAM não tem permissões suficientes.

**Solução**:
1. Verifique se o usuário tem a política `AmazonSQSFullAccess` ou equivalente
2. Verifique se a política está anexada ao usuário correto
3. Verifique se o Resource ARN na política corresponde à sua fila

### Erro: "Queue does not exist"

**Causa**: Nome da fila incorreto ou região diferente.

**Solução**:
1. Verifique o nome exato da fila no Console AWS
2. Verifique se `spring.cloud.aws.region.static` corresponde à região da fila
3. Verifique se `aws.sqs.queue-name` está correto no `application.properties`

### Erro: "Account ID não configurado"

**Causa**: `aws.sqs.account-id` não está configurado.

**Solução**:
1. Adicione `aws.sqs.account-id=123456789012` no `application.properties`
2. Substitua pelo seu Account ID real

### Aplicação não consome mensagens

**Causa**: Listener não está configurado corretamente.

**Solução**:
1. Verifique se `@SqsListener` está usando `${aws.sqs.queue-name}`
2. Verifique logs de inicialização para erros
3. Verifique se a fila existe e está na região correta

---

## 📚 Recursos Adicionais

- [Documentação Spring Cloud AWS SQS](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html)
- [Documentação Amazon SQS](https://docs.aws.amazon.com/sqs/)
- [Guia de Credenciais AWS](https://docs.aws.amazon.com/general/latest/gr/aws-sec-cred-types.html)
- [AWS CLI User Guide](https://docs.aws.amazon.com/cli/latest/userguide/)

---

## ✅ Checklist Final

Antes de considerar a configuração completa, verifique:

- [ ] Fila SQS criada na AWS
- [ ] Account ID obtido e configurado
- [ ] Credenciais AWS configuradas (Profile, variáveis de ambiente ou IAM Role)
- [ ] `application.properties` atualizado com valores corretos
- [ ] Aplicação inicia sem erros
- [ ] Mensagem é enviada com sucesso
- [ ] Mensagem é consumida e aparece no console

Se todos os itens estiverem marcados, sua configuração está completa! 🎉
