# Plano de Implementação - Kotlin Backend Challenge

**Staff Backend Engineer - Visão Pragmática**

---

## 1. Decisões Técnicas e Justificativas

### Framework: Spring Boot
- **Por quê**: mais familiar para maioria dos times, autoconfiguration reduz boilerplate, excelente para o timebox de 30min
- **Alternativa**: Ktor seria mais leve, mas Spring Boot oferece melhor DX (Developer Experience) e menos configuração manual

### Arquitetura: Ports & Adapters Minimalista (Clean Architecture Lite)
- **Justificativa**: 
  - Clean Architecture completa (Use Cases, Entities, Gateways) seria **overengineering** para 3 endpoints simples
  - Proposta: **3 camadas apenas** - Controller → Service (regras de negócio) → Repository (IO)
  - Benefit: testabilidade + separação de responsabilidades sem complexidade excessiva
  - Trade-off consciente: sacrificamos inversão de dependência completa em prol de velocidade/simplicidade

### Tratamento de Erros
- Path traversal: validar filename (sem `..`, `/`, `\`)
- Filename vazio/nulo: retornar 400 Bad Request
- Content nulo: aceitar (arquivo vazio é válido)
- IOException: capturar e contar como erro nas métricas

### IO e Segurança
- Usar `Path.resolve()` e `normalize()` para evitar path traversal
- Criar `/storage` automaticamente se não existir (graceful)
- UTF-8 encoding explícito
- Não expor stack traces em produção (apenas logs)

### Métricas In-Memory
- `AtomicLong` para thread-safety (concorrência)
- Sem libs externas (nem Micrometer) - apenas contadores simples
- 404 conta como erro (conforme PDF)
- Implementar exatamente: `uploads`, `reads`, `errors`

---

## 2. Estrutura de Pastas Sugerida

```
kotlin-nk/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/
│   │   │       └── taller/
│   │   │           └── backend/
│   │   │               ├── Application.kt                    # Main Spring Boot
│   │   │               ├── controller/
│   │   │               │   ├── FileController.kt            # POST/GET /files
│   │   │               │   ├── MetricsController.kt         # GET /metrics
│   │   │               │   └── GlobalExceptionHandler.kt    # @ControllerAdvice
│   │   │               ├── service/
│   │   │               │   ├── FileService.kt               # Business logic
│   │   │               │   └── MetricsService.kt            # In-memory counters
│   │   │               ├── repository/
│   │   │               │   └── LocalFileRepository.kt       # IO operations
│   │   │               ├── dto/
│   │   │               │   ├── UploadRequest.kt             # Request body
│   │   │               │   ├── MetricsResponse.kt           # Response body
│   │   │               │   └── ErrorResponse.kt             # Error format
│   │   │               └── exception/
│   │   │                   ├── FileNotFoundException.kt
│   │   │                   └── InvalidFilenameException.kt
│   │   └── resources/
│   │       └── application.yml                              # Server config
│   └── test/
│       └── kotlin/
│           └── com/
│               └── taller/
│                   └── backend/
│                       ├── controller/
│                       │   ├── FileControllerTest.kt        # Unit tests
│                       │   └── MetricsControllerTest.kt
│                       ├── service/
│                       │   ├── FileServiceTest.kt
│                       │   └── MetricsServiceTest.kt
│                       ├── repository/
│                       │   └── LocalFileRepositoryTest.kt
│                       └── integration/
│                           └── FileApiIntegrationTest.kt    # Full Spring context
├── storage/                                                 # Git-ignored, criado em runtime
├── Dockerfile
├── docker-compose.yml
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── .gitignore
```

**Decisão de estrutura**:
- Flat package structure (sem subpacotes excessivos) - 7 arquivos principais
- Test mirror production structure (fácil de navegar)
- `integration/` separado de unit tests para rodar seletivamente

---

## 3. Design das Classes/Componentes

### **FileController** (Presentation Layer)
**Responsabilidades**:
- Receber HTTP requests (POST, GET)
- Validar input básico (não-nulo)
- Delegar para `FileService`
- Mapear exceptions para HTTP status codes

**Métodos**:
```kotlin
@PostMapping("/files")
fun uploadFile(@RequestBody request: UploadRequest): ResponseEntity<Unit>

@GetMapping("/files/{filename}")
fun downloadFile(@PathVariable filename: String): ResponseEntity<String>
```

---

### **MetricsController**
**Responsabilidades**:
- Expor GET /metrics
- Buscar dados de `MetricsService`

**Métodos**:
```kotlin
@GetMapping("/metrics")
fun getMetrics(): MetricsResponse
```

---

### **FileService** (Business Logic)
**Responsabilidades**:
- Validar filename (path traversal, caracteres inválidos)
- Orquestrar chamadas para `LocalFileRepository`
- Atualizar métricas via `MetricsService`
- Lançar exceptions customizadas

**Métodos**:
```kotlin
fun saveFile(filename: String, content: String)
fun readFile(filename: String): String
private fun validateFilename(filename: String) // throws InvalidFilenameException
```

**Validações**:
- Filename não pode conter: `..`, `/`, `\`, null bytes
- Filename não pode estar vazio
- Regex: `^[a-zA-Z0-9._-]+$` (conservador, mas seguro)

---

### **MetricsService** (State Management)
**Responsabilidades**:
- Manter contadores thread-safe
- Incrementar atomicamente
- Retornar snapshot atual

**Atributos**:
```kotlin
private val uploads = AtomicLong(0)
private val reads = AtomicLong(0)
private val errors = AtomicLong(0)
```

**Métodos**:
```kotlin
fun incrementUploads()
fun incrementReads()
fun incrementErrors()
fun getSnapshot(): MetricsResponse
```

---

### **LocalFileRepository** (Infrastructure Layer)
**Responsabilidades**:
- Criar diretório `/storage` se não existir
- Escrever arquivo no filesystem
- Ler arquivo do filesystem
- Lançar IOException em caso de falha

**Métodos**:
```kotlin
fun write(filename: String, content: String)
fun read(filename: String): String // throws NoSuchFileException
private fun ensureStorageExists()
```

**Implementação**:
- Usar `java.nio.file.Path` e `Files.writeString()` / `Files.readString()`
- Base path: `./storage` (relativo ao working directory)
- Encoding: `StandardCharsets.UTF_8`

---

### **GlobalExceptionHandler**
**Responsabilidades**:
- Capturar `InvalidFilenameException` → 400
- Capturar `FileNotFoundException` → 404
- Capturar `Exception` genérica → 500
- Incrementar métricas de erro

---

### **DTOs**
```kotlin
data class UploadRequest(
    val filename: String,
    val content: String
)

data class MetricsResponse(
    val uploads: Long,
    val reads: Long,
    val errors: Long
)

data class ErrorResponse(
    val message: String,
    val timestamp: String
)
```

---

## 4. Fluxo de Cada Endpoint

### **POST /files**

```
1. HTTP POST /files com JSON body
   ↓
2. FileController.uploadFile()
   - Valida request não-nulo
   ↓
3. FileService.saveFile(filename, content)
   - validateFilename() → lança InvalidFilenameException se inválido
   - Chama LocalFileRepository.write()
   - Se sucesso: incrementa MetricsService.uploads
   - Se IOException: lança Exception (virar 500)
   ↓
4. LocalFileRepository.write()
   - Resolve path: storage/${filename}
   - Files.writeString() com UTF-8
   ↓
5. Retorna 200 OK
```

**Erros possíveis**:
- 400: filename inválido (path traversal, vazio)
- 500: falha ao escrever no disco (IOException)

---

### **GET /files/{filename}**

```
1. HTTP GET /files/test.txt
   ↓
2. FileController.downloadFile(filename)
   ↓
3. FileService.readFile(filename)
   - validateFilename() → 400 se inválido
   - Chama LocalFileRepository.read()
   - Se FileNotFoundException: incrementa errors + lança FileNotFoundException
   - Se sucesso: incrementa MetricsService.reads
   ↓
4. LocalFileRepository.read()
   - Resolve path: storage/${filename}
   - Files.readString() → lança NoSuchFileException se não existir
   ↓
5. Retorna 200 OK com content
```

**Erros possíveis**:
- 400: filename inválido
- 404: arquivo não existe (incrementa errors)
- 500: falha ao ler (IOException genérica)

---

### **GET /metrics**

```
1. HTTP GET /metrics
   ↓
2. MetricsController.getMetrics()
   ↓
3. MetricsService.getSnapshot()
   - Lê valores atuais dos AtomicLong
   ↓
4. Retorna JSON:
   {
     "uploads": 3,
     "reads": 5,
     "errors": 1
   }
```

**Sem erros esperados** (sempre retorna 200).

---

## 5. Estratégia de Testes

### **Objetivo: Testar o Essencial**

**Prioridade nos testes** (tempo limitado):
1. **FileService**: toda lógica de validação (path traversal, filename vazio)
2. **LocalFileRepository**: IO real com temp directory
3. **Integration Test**: fluxo completo E2E (upload → download → metrics)

**O que NÃO testar** (economizar tempo):
- DTOs (data classes sem lógica)
- Application.kt (main function)
- MetricsService (trivial, apenas incrementos)

---

### **Unit Tests**

#### **FileServiceTest**
**Estratégia**:
- Mockar `LocalFileRepository` (usando Mockito/MockK)
- Mockar `MetricsService`
- Testar:
  - ✅ Filename válido → chama repository corretamente
  - ✅ Filename com `..` → lança InvalidFilenameException
  - ✅ Filename vazio → lança InvalidFilenameException
  - ✅ Filename com `/` → lança InvalidFilenameException
  - ✅ Leitura bem-sucedida → incrementa reads
  - ✅ Arquivo não existe → incrementa errors + lança exception
  - ✅ IOException no save → lança exception (errors incrementado no handler)

**Exemplo de teste**:
```kotlin
@Test
fun `should reject filename with path traversal`() {
    assertThrows<InvalidFilenameException> {
        fileService.saveFile("../etc/passwd", "hack")
    }
}
```

---

#### **MetricsServiceTest**
**Estratégia**:
- Testar thread-safety (opcional, mas recomendado)
- Testar incrementos e snapshot

**Testes**:
- ✅ Incrementar uploads → snapshot retorna valor correto
- ✅ Múltiplos incrementos concorrentes → valor final correto (usar CountDownLatch)

---

#### **LocalFileRepositoryTest**
**Estratégia**:
- Usar **temp directory** (`@TempDir` do JUnit 5)
- Não mockar filesystem (é infra real, mas isolada)

**Testes**:
- ✅ Write cria arquivo com conteúdo correto
- ✅ Read retorna conteúdo correto
- ✅ Read de arquivo inexistente → lança NoSuchFileException
- ✅ Storage directory é criado se não existir

**Exemplo**:
```kotlin
@Test
fun `should write and read file successfully`(@TempDir tempDir: Path) {
    val repo = LocalFileRepository(tempDir.toString())
    repo.write("test.txt", "content")
    
    val result = repo.read("test.txt")
    assertEquals("content", result)
}
```

---

#### **ControllerTest (Unit)**
**Estratégia**:
- Usar `@WebMvcTest` (carrega apenas controller layer)
- Mockar `FileService` e `MetricsService`
- Testar HTTP status codes

**Testes**:
- ✅ POST /files retorna 200
- ✅ GET /files/{filename} retorna 200 com content
- ✅ GET /files/nonexistent retorna 404
- ✅ POST com filename inválido retorna 400

---

### **Integration Tests**

#### **FileApiIntegrationTest**
**Estratégia**:
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- Usar `TestRestTemplate` ou `WebTestClient`
- Usar **temp directory** para /storage (configurar via application-test.yml)
- Testar fluxo completo: POST → GET → Metrics

**Cenários**:
1. ✅ Upload arquivo → download → verificar content correto
2. ✅ Upload 3 arquivos → GET /metrics → verificar `uploads=3`
3. ✅ GET arquivo inexistente → verificar `errors=1` em /metrics
4. ✅ Upload com filename inválido → 400
5. ✅ Upload → delete arquivo manualmente → GET → 404

**Exemplo**:
```kotlin
@SpringBootTest(webEnvironment = RANDOM_PORT)
class FileApiIntegrationTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    
    @Test
    fun `full upload and download flow`() {
        // Upload
        val request = UploadRequest("test.txt", "Hello Nike")
        restTemplate.postForEntity("/files", request, Void::class.java)
        
        // Download
        val response = restTemplate.getForEntity("/files/test.txt", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Hello Nike", response.body)
        
        // Metrics
        val metrics = restTemplate.getForEntity("/metrics", MetricsResponse::class.java)
        assertEquals(1, metrics.body?.uploads)
        assertEquals(1, metrics.body?.reads)
    }
}
```

**Como validar**:
```bash
./gradlew test
# Verificar que todos passam - é suficiente para o desafio
```

---

## 6. Docker

### **Dockerfile (Multi-Stage Build)**

**Abordagem**:
- **Stage 1 (Builder)**: buildar a aplicação com Gradle
- **Stage 2 (Runtime)**: copiar apenas o JAR final para imagem leve

**Benefícios**:
- Imagem final menor (~150MB vs ~500MB)
- Não inclui Gradle, source code, build cache

**Pontos de Atenção**:
1. Usar imagem base oficial `eclipse-temurin:17-jre` (JRE apenas, não JDK)
2. Criar diretório `/storage` no container
3. Expor porta 8080
4. Usar ENTRYPOINT com array (não shell form)
5. Não rodar como root (criar user `appuser`)

**Dockerfile**:
```dockerfile
# Stage 1: Build
FROM gradle:8.5-jdk17 AS builder
WORKDIR /build
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security: non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy JAR from builder
COPY --from=builder /build/build/libs/*.jar app.jar

# Create storage directory
RUN mkdir -p /app/storage

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Validação**:
```bash
docker build -t kotlin-backend .
docker run -p 8080:8080 kotlin-backend
curl localhost:8080/metrics
```

---

### **docker-compose.yml**

**Objetivo**: facilitar desenvolvimento local com volume persistente

```yaml
version: '3.8'

services:
  backend:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - ./storage:/app/storage  # Persistir arquivos no host
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/metrics"]
      interval: 10s
      timeout: 5s
      retries: 3
```

**Benefícios**:
- Volume bind permite inspecionar `/storage` no host
- Healthcheck para wait-for-it em orquestrações
- Profile separado para configs específicas de Docker

**Uso**:
```bash
docker-compose up --build
docker-compose down
```

---

## 7. README Completo e Detalhado

**IMPORTANTE**: 
- O PDF marca README como "Very Important" - precisa ser extremamente detalhado
- **"You do not need to write Terraform, CloudFormation, or Jenkins pipelines, explanations are enough"**
- Ou seja: explicar COMO faria (estratégia, componentes, workflow), não escrever código completo
- Demonstrar raciocínio técnico sênior sobre AWS, DevOps e produção

### **Estrutura do README.md**

```markdown
# Kotlin Backend Challenge - File Storage Service

## 📋 Sobre o Projeto

Serviço backend em Kotlin/Spring Boot que simula um sistema de armazenamento de arquivos (estilo S3) com métricas de observabilidade (estilo CloudWatch). 

**Objetivo**: Demonstrar como um serviço local pode ser mapeado para uma arquitetura AWS de produção, incluindo considerações de deploy, CI/CD e infraestrutura como código.

## 🚀 Como Rodar Localmente

### Pré-requisitos
- Docker instalado

### Com Docker
```bash
docker build -t kotlin-backend .
docker run -p 8080:8080 kotlin-backend
```

### Com Docker Compose
```bash
docker-compose up --build
```

### Sem Docker (desenvolvimento)
```bash
./gradlew bootRun
```

## 📡 Endpoints

### POST /files
Upload de arquivo
```bash
curl -X POST http://localhost:8080/files \
  -H "Content-Type: application/json" \
  -d '{"filename":"test.txt","content":"Hello Nike"}'
```

### GET /files/{filename}
Download de arquivo
```bash
curl http://localhost:8080/files/test.txt
```

### GET /metrics
Métricas
```bash
curl http://localhost:8080/metrics
# {"uploads":1,"reads":0,"errors":0}
```

## ☁️ Estratégia de Deploy na AWS

### 🎯 Visão Geral da Arquitetura

**Serviço atual (local)**:
- Container Docker rodando Spring Boot
- Storage em filesystem local (`/storage`)
- Métricas in-memory

**Arquitetura AWS de produção**:
- **Compute**: EC2 ou ECS Fargate
- **Storage**: Amazon S3
- **Observabilidade**: CloudWatch (Metrics + Logs)
- **Networking**: VPC, ALB, Security Groups
- **Segurança**: IAM Roles, Secrets Manager

---

### 📦 1. Deploy em EC2 (Opção Simples)

**Por quê escolher EC2**:
- Controle total sobre o ambiente
- Custo previsível (vs Fargate que cobra por vCPU-hora)
- Ideal para workload steady-state (não tem picos grandes)
- Mais fácil para debug (SSH direto)

#### Passos de Deploy

**1.1. Provisionar Infraestrutura**

```bash
# Via AWS CLI (ou usar Terraform - ver seção IaC)
aws ec2 run-instances \
  --image-id ami-0abcdef1234567890 \      # Amazon Linux 2023
  --instance-type t3.small \               # 2 vCPU, 2GB RAM
  --key-name my-key \
  --security-group-ids sg-xxxxx \
  --iam-instance-profile Name=EC2-FileStorage-Role \
  --user-data file://user-data.sh \
  --block-device-mappings '[{
    "DeviceName": "/dev/xvda",
    "Ebs": {"VolumeSize": 20, "VolumeType": "gp3"}
  }]'
```

**Por quê t3.small**:
- Suficiente para ~100 req/s (estimativa inicial)
- Custo: ~$15/mês (us-east-1)
- Burstable (usa credits para picos)
- Se escalar: migrar para t3.medium ou usar Auto Scaling Group

**1.2. User Data Script (inicialização automática)**

```bash
#!/bin/bash
# user-data.sh - executa na primeira inicialização

# Instalar Docker
yum update -y
yum install -y docker
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# Configurar disco para /storage
mkdir -p /data/storage
chown -R ec2-user:ec2-user /data/storage

# Login no ECR (registry privado da AWS)
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Rodar container
docker run -d \
  --name kotlin-backend \
  --restart unless-stopped \
  -p 8080:8080 \
  -v /data/storage:/app/storage \
  --log-driver=awslogs \
  --log-opt awslogs-region=us-east-1 \
  --log-opt awslogs-group=/aws/ec2/kotlin-backend \
  <account-id>.dkr.ecr.us-east-1.amazonaws.com/kotlin-backend:latest

# Healthcheck endpoint
echo "*/5 * * * * curl -f http://localhost:8080/metrics || systemctl restart docker" | crontab -
```

**Explicação**:
- `--restart unless-stopped`: container reinicia automaticamente se cair
- `-v /data/storage:/app/storage`: volume persiste arquivos no EBS
- `--log-driver=awslogs`: envia logs para CloudWatch automaticamente
- Cron healthcheck: se /metrics falhar, reinicia Docker (failsafe simples)

**1.3. Networking e Segurança**

**Security Group**:
```
Inbound Rules:
- Port 8080: 0.0.0.0/0 (ou melhor: apenas do ALB se usar Load Balancer)
- Port 22: <seu-ip>/32 (SSH para debug - desabilitar em prod)

Outbound Rules:
- All traffic: 0.0.0.0/0 (para fazer docker pull do ECR)
```

**Por quê usar ALB na frente**:
- Termination de SSL/TLS (certificado HTTPS)
- Distribui tráfego se tiver múltiplas instâncias
- Health checks automáticos (tira instância unhealthy do pool)
- Integra com WAF (proteção contra DDoS)

**IAM Instance Profile** (necessário):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchGetImage",
        "ecr:GetDownloadUrlForLayer"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:log-group:/aws/ec2/kotlin-backend:*"
    },
    {
      "Effect": "Allow",
      "Action": ["s3:*"],
      "Resource": [
        "arn:aws:s3:::kotlin-backend-files/*",
        "arn:aws:s3:::kotlin-backend-files"
      ]
    }
  ]
}
```

**1.4. Persistência de Dados**

**Problema**: instância EC2 pode ser terminada (manutenção, falha, etc.)

**Solução 1: EBS Volume Separado**
```bash
# Criar volume de 50GB para arquivos
aws ec2 create-volume --size 50 --availability-zone us-east-1a --volume-type gp3

# Anexar ao EC2
aws ec2 attach-volume --volume-id vol-xxxxx --instance-id i-xxxxx --device /dev/sdf

# Montar no EC2
sudo mkfs -t ext4 /dev/sdf
sudo mount /dev/sdf /data/storage
echo '/dev/sdf /data/storage ext4 defaults,nofail 0 2' | sudo tee -a /etc/fstab
```

**Benefícios**:
- Volume persiste mesmo se instância morrer
- Snapshot diário automático via Data Lifecycle Manager
- Pode anexar em outra instância (disaster recovery)

**Solução 2: Migrar para S3 (melhor para produção)**
- Ver seção "Migração para S3" abaixo

---

### 🚀 2. Deploy em ECS Fargate (Opção Escalável)

**Por quê escolher Fargate**:
- Serverless (não gerenciar EC2)
- Escala automática (até 0 se não tiver tráfego)
- Paga apenas pelo que usa (por segundo)
- Multi-AZ por padrão (alta disponibilidade)

**Trade-offs**:
- Mais caro que EC2 para workload constante (~30% a mais)
- Cold start pode adicionar latência (2-3s para nova task)

#### Arquitetura ECS

```
Internet → ALB → ECS Service (2+ tasks) → S3
                      ↓
                  CloudWatch
```

**Componentes**:
1. **ECS Cluster**: agrupamento lógico de tasks
2. **Task Definition**: "receita" do container (imagem, CPU, RAM, variáveis)
3. **Service**: mantém N tasks rodando, integra com ALB
4. **ALB Target Group**: roteamento de tráfego

#### Task Definition (JSON)

```json
{
  "family": "kotlin-backend",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [{
    "name": "app",
    "image": "<account-id>.dkr.ecr.us-east-1.amazonaws.com/kotlin-backend:latest",
    "portMappings": [{"containerPort": 8080, "protocol": "tcp"}],
    "environment": [
      {"name": "SPRING_PROFILES_ACTIVE", "value": "aws"}
    ],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "/ecs/kotlin-backend",
        "awslogs-region": "us-east-1",
        "awslogs-stream-prefix": "ecs"
      }
    },
    "healthCheck": {
      "command": ["CMD-SHELL", "curl -f http://localhost:8080/metrics || exit 1"],
      "interval": 30,
      "timeout": 5,
      "retries": 3
    }
  }],
  "executionRoleArn": "arn:aws:iam::xxx:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::xxx:role/kotlin-backend-task-role"
}
```

**Diferença entre execution role e task role**:
- **Execution Role**: ECS usa para fazer pull da imagem, escrever logs
- **Task Role**: aplicação usa para acessar S3, CloudWatch, outros serviços

#### Deploy Command

```bash
# Criar cluster
aws ecs create-cluster --cluster-name kotlin-backend-prod

# Registrar task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json

# Criar service
aws ecs create-service \
  --cluster kotlin-backend-prod \
  --service-name kotlin-backend \
  --task-definition kotlin-backend:1 \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration '{
    "awsvpcConfiguration": {
      "subnets": ["subnet-xxx", "subnet-yyy"],
      "securityGroups": ["sg-xxxxx"],
      "assignPublicIp": "ENABLED"
    }
  }' \
  --load-balancers '[{
    "targetGroupArn": "arn:aws:elasticloadbalancing:...",
    "containerName": "app",
    "containerPort": 8080
  }]'
```

**Auto Scaling**:
```bash
# Escalar baseado em CPU
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/kotlin-backend-prod/kotlin-backend \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10

aws application-autoscaling put-scaling-policy \
  --policy-name cpu-scaling \
  --service-namespace ecs \
  --resource-id service/kotlin-backend-prod/kotlin-backend \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration '{
    "TargetValue": 70.0,
    "PredefinedMetricSpecification": {
      "PredefinedMetricType": "ECSServiceAverageCPUUtilization"
    }
  }'
```

**Quando CPU > 70%**: adiciona tasks (até 10)
**Quando CPU < 70%**: remove tasks (mínimo 2 para HA)

---

### 🗄️ 3. Migração para Amazon S3

**Por quê S3 em vez de filesystem local**:
- **Durabilidade**: 99.999999999% (11 noves)
- **Escalabilidade**: sem limite de storage
- **Custo**: $0.023/GB/mês (vs EBS $0.08/GB/mês)
- **Versionamento**: histórico de alterações
- **Lifecycle**: mover arquivos antigos para Glacier (mais barato)

#### Mudanças no Código

**Interface Repository** (abstração):
```kotlin
interface FileRepository {
    fun save(filename: String, content: String)
    fun read(filename: String): String
}
```

**Implementação S3**:
```kotlin
@Profile("aws")
@Repository
class S3FileRepository(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket}") private val bucketName: String
) : FileRepository {
    
    override fun save(filename: String, content: String) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build(),
            RequestBody.fromString(content)
        )
    }
    
    override fun read(filename: String): String {
        return s3Client.getObjectAsBytes(
            GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build()
        ).asUtf8String()
    }
}
```

**Configuração**:
```yaml
# application-aws.yml
aws:
  s3:
    bucket: kotlin-backend-files-prod
    region: us-east-1
```

**Criar bucket S3**:
```bash
aws s3api create-bucket \
  --bucket kotlin-backend-files-prod \
  --region us-east-1

# Habilitar versionamento
aws s3api put-bucket-versioning \
  --bucket kotlin-backend-files-prod \
  --versioning-configuration Status=Enabled

# Lifecycle: mover para Glacier após 90 dias
aws s3api put-bucket-lifecycle-configuration \
  --bucket kotlin-backend-files-prod \
  --lifecycle-configuration '{
    "Rules": [{
      "Id": "archive-old-files",
      "Status": "Enabled",
      "Transitions": [{
        "Days": 90,
        "StorageClass": "GLACIER"
      }]
    }]
  }'

# Criptografia em repouso
aws s3api put-bucket-encryption \
  --bucket kotlin-backend-files-prod \
  --server-side-encryption-configuration '{
    "Rules": [{
      "ApplyServerSideEncryptionByDefault": {
        "SSEAlgorithm": "AES256"
      }
    }]
  }'
```

---

### 📊 4. Integração com CloudWatch

**Objetivo**: Observabilidade completa - logs, métricas e alarmes

#### 4.1. CloudWatch Logs (Application Logs)

**Setup com Docker**:
```bash
docker run -d \
  --log-driver=awslogs \
  --log-opt awslogs-region=us-east-1 \
  --log-opt awslogs-group=/aws/kotlin-backend \
  --log-opt awslogs-stream-prefix=prod \
  kotlin-backend:latest
```

**Estrutura de logs**:
```
Log Group: /aws/kotlin-backend
├── Stream: prod/app/container-id-1
├── Stream: prod/app/container-id-2
└── Stream: prod/app/container-id-3
```

**Formato de log (JSON estruturado)**:
```kotlin
// No application.yml
logging:
  pattern:
    console: '{"timestamp":"%d{ISO8601}","level":"%level","thread":"%thread","class":"%logger{36}","message":"%msg"}%n'
```

**Por quê JSON**:
- CloudWatch Insights pode fazer queries SQL-like
- Fácil criar dashboards
- Integra com ferramentas de alerting

**Query útil (CloudWatch Insights)**:
```sql
fields @timestamp, level, message
| filter level = "ERROR"
| stats count() by bin(5m)
```

**Retenção**:
- Produção: 30 dias (compliance)
- Development: 7 dias (economizar)

```bash
aws logs put-retention-policy \
  --log-group-name /aws/kotlin-backend \
  --retention-in-days 30
```

---

#### 4.2. CloudWatch Metrics (Custom Metrics)

**Métricas implementadas no código**:
- `uploads`: total de arquivos salvos
- `reads`: total de leituras
- `errors`: total de erros (404, 500, etc.)

**Como enviar para CloudWatch**:

**Opção 1: SDK (código)**
```kotlin
@Service
class CloudWatchMetricsService(
    private val cloudWatchClient: CloudWatchClient
) {
    fun publishMetrics(uploads: Long, reads: Long, errors: Long) {
        cloudWatchClient.putMetricData(
            PutMetricDataRequest.builder()
                .namespace("KotlinBackend")
                .metricData(
                    MetricDatum.builder()
                        .metricName("Uploads")
                        .value(uploads.toDouble())
                        .unit(StandardUnit.COUNT)
                        .timestamp(Instant.now())
                        .dimensions(
                            Dimension.builder().name("Environment").value("prod").build(),
                            Dimension.builder().name("Service").value("file-storage").build()
                        )
                        .build(),
                    // Repetir para Reads e Errors
                )
                .build()
        )
    }
}

// Scheduled task: enviar a cada 1 minuto
@Scheduled(fixedRate = 60000)
fun publishToCloudWatch() {
    val snapshot = metricsService.getSnapshot()
    cloudWatchMetricsService.publishMetrics(
        snapshot.uploads,
        snapshot.reads,
        snapshot.errors
    )
}
```

**Opção 2: CloudWatch Agent (sem código)**
- Ler endpoint GET /metrics a cada 1 minuto
- Fazer parse do JSON e enviar para CloudWatch
- Vantagem: não acopla código com AWS SDK

**Métricas de sistema (EC2 apenas)**:
```bash
# Instalar CloudWatch Agent
sudo yum install amazon-cloudwatch-agent -y

# Configuração (cloudwatch-config.json)
{
  "metrics": {
    "namespace": "KotlinBackend/EC2",
    "metrics_collected": {
      "cpu": {"measurement": [{"name": "cpu_usage_idle"}], "totalcpu": false},
      "disk": {
        "measurement": [{"name": "used_percent"}],
        "resources": ["/data/storage"]
      },
      "mem": {"measurement": [{"name": "mem_used_percent"}]}
    }
  }
}

# Iniciar agent
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config -m ec2 -c file:cloudwatch-config.json -s
```

---

#### 4.3. CloudWatch Alarms

**Alarme 1: Taxa de Erro Alta**
```bash
aws cloudwatch put-metric-alarm \
  --alarm-name kotlin-backend-high-error-rate \
  --alarm-description "Error rate > 5%" \
  --metric-name Errors \
  --namespace KotlinBackend \
  --statistic Sum \
  --period 300 \
  --evaluation-periods 2 \
  --threshold 100 \
  --comparison-operator GreaterThanThreshold \
  --dimensions Name=Environment,Value=prod \
  --alarm-actions arn:aws:sns:us-east-1:xxx:ops-alerts
```

**Por quê 2 evaluation periods**:
- Evita alarmes falsos (1 spike não aciona)
- 2 períodos de 5min = 10min de erro consistente

**Alarme 2: Disk Usage Alto (EC2)**
```bash
aws cloudwatch put-metric-alarm \
  --alarm-name kotlin-backend-disk-usage \
  --metric-name disk_used_percent \
  --namespace KotlinBackend/EC2 \
  --statistic Average \
  --period 300 \
  --evaluation-periods 1 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --alarm-actions arn:aws:sns:us-east-1:xxx:ops-alerts
```

**Ação quando aciona**:
- SNS topic envia para Slack, PagerDuty, email
- Lambda pode ser triggered para fazer cleanup automático

**Alarme 3: CPU Alta (trigger de auto-scaling)**
```bash
aws cloudwatch put-metric-alarm \
  --alarm-name kotlin-backend-high-cpu \
  --metric-name CPUUtilization \
  --namespace AWS/EC2 \
  --statistic Average \
  --period 300 \
  --evaluation-periods 2 \
  --threshold 70 \
  --comparison-operator GreaterThanThreshold \
  --dimensions Name=InstanceId,Value=i-xxxxx \
  --alarm-actions arn:aws:autoscaling:us-east-1:xxx:scalingPolicy:xxx
```

---

#### 4.4. CloudWatch Dashboard

**Exemplo de dashboard**:
```json
{
  "widgets": [
    {
      "type": "metric",
      "properties": {
        "title": "File Operations",
        "metrics": [
          ["KotlinBackend", "Uploads"],
          [".", "Reads"],
          [".", "Errors"]
        ],
        "period": 300,
        "stat": "Sum",
        "region": "us-east-1"
      }
    },
    {
      "type": "metric",
      "properties": {
        "title": "Error Rate (%)",
        "metrics": [
          [{
            "expression": "m3 / (m1 + m2) * 100",
            "label": "Error Rate",
            "id": "e1"
          }],
          ["KotlinBackend", "Uploads", {"id": "m1", "visible": false}],
          [".", "Reads", {"id": "m2", "visible": false}],
          [".", "Errors", {"id": "m3", "visible": false}]
        ]
      }
    },
    {
      "type": "log",
      "properties": {
        "query": "SOURCE '/aws/kotlin-backend' | fields @timestamp, level, message | filter level = 'ERROR' | sort @timestamp desc | limit 20",
        "region": "us-east-1",
        "title": "Recent Errors"
      }
    }
  ]
}
```

**Como criar**:
```bash
aws cloudwatch put-dashboard \
  --dashboard-name kotlin-backend-prod \
  --dashboard-body file://dashboard.json
```

---

## 🔄 CI/CD Pipeline (Jenkins)

**Objetivo**: Automatizar build, test, deploy e rollback com segurança

### Arquitetura do Pipeline

```
Git Push → GitHub Webhook → Jenkins → Build → Test → Security Scan → 
  → Build Image → Push to ECR → Deploy to AWS → Smoke Tests → Monitor
```

**Nota**: Não vou escrever Jenkinsfile completo aqui (conforme PDF: "explanations are enough"), mas vou explicar como estruturaria o pipeline.

---

### Stages do Pipeline

#### **Stage 1: Build & Test**
- Executar `./gradlew clean build` para compilar aplicação
- Rodar `./gradlew test` para executar todos os testes
- Publicar relatórios de teste (JUnit XML + HTML report)
- **Gate**: se algum teste falhar, pipeline para aqui

#### **Stage 2: Security Scan**
- **SAST (Static Analysis)**: usar SonarQube ou Snyk para detectar vulnerabilidades no código
- **Dependency Check**: verificar bibliotecas com vulnerabilidades conhecidas (CVE)
- **Threshold**: falhar se encontrar vulnerabilidade HIGH ou CRITICAL
- **Por quê**: pegar problemas de segurança antes de chegar em produção

#### **Stage 3: Build Docker Image**
- Executar `docker build -t <ecr-repo>:$BUILD_NUMBER .`
- Taguear também como `:latest` para facilitar testes locais
- **Por quê taggear com BUILD_NUMBER**: permite rollback para versão específica

#### **Stage 4: Container Security Scan**
- Usar Trivy (ou Clair) para escanear imagem Docker
- Procurar vulnerabilidades no base image e dependências
- **Gate**: falhar se encontrar vulnerabilidade CRITICAL

#### **Stage 5: Push to ECR (Amazon Elastic Container Registry)**
- Fazer login no ECR via `aws ecr get-login-password`
- Push da imagem: `docker push <ecr-repo>:$BUILD_NUMBER`
- Push da tag latest: `docker push <ecr-repo>:latest`

#### **Stage 6: Deploy to AWS**
**Opção A: ECS Fargate**
- Executar `aws ecs update-service --force-new-deployment`
- ECS faz pull da nova imagem e substitui tasks antigas
- Rolling update: uma task de cada vez (zero downtime)

**Opção B: EC2**
- Usar AWS Systems Manager (SSM) Run Command para executar comandos no EC2
- Comandos: `docker pull`, `docker stop`, `docker rm`, `docker run`
- **Por quê SSM**: não precisa SSH, mais seguro, auditável

#### **Stage 7: Wait for Deployment**
- Executar `aws ecs wait services-stable` (ou polling manual no EC2)
- Aguardar até todos os health checks passarem
- **Timeout**: 5 minutos (se não estabilizar, algo está errado)

#### **Stage 8: Smoke Tests**
- Pegar URL do ALB (ou IP do EC2)
- Executar 3 testes básicos:
  1. `curl /metrics` → deve retornar 200 + JSON
  2. `POST /files` com arquivo de teste
  3. `GET /files/{filename}` → verificar conteúdo correto
- **Se falhar**: rollback automático (ver seção abaixo)

#### **Post-Pipeline Actions**
**Em caso de sucesso**:
- Notificar Slack/Teams: "✅ Deploy succeeded - Build #123"
- Atualizar Jira/Linear: mover ticket para "Deployed"

**Em caso de falha**:
- Notificar on-call: "❌ Deploy failed - Build #123"
- **Rollback automático**: reverter para task definition anterior no ECS
- Criar issue automática no Jira com logs do erro

---

### Configurações Adicionais

#### 1. Credenciais no Jenkins
- Usar Jenkins AWS Credentials Plugin para armazenar AWS keys
- **Melhor**: IAM Role attached ao EC2 onde Jenkins roda (não precisa armazenar keys)
- SonarQube token via Jenkins Credentials Store (não hardcode)

#### 2. Jenkins Agent Requirements
- Docker instalado (para build da imagem)
- AWS CLI instalado (para push ECR e deploy)
- Gradle instalado (ou usar Gradle wrapper do projeto)
- Recursos: mínimo 2 vCPU, 4GB RAM (build do Gradle consome memória)

#### 3. Blue/Green Deployment (Estratégia Avançada)

**Por quê usar**:
- **Zero downtime**: sempre tem um ambiente rodando
- **Rollback instantâneo**: só trocar peso do ALB (segundos)
- **Teste em prod**: testar nova versão antes de direcionar tráfego real

**Como funciona**:
1. **Blue** = ambiente atual (recebendo 100% do tráfego)
2. **Green** = nova versão (0% do tráfego)
3. Deploy para Green
4. Smoke tests no Green
5. Trocar ALB listener: 0% blue → 100% green (gradualmente: 10%, 50%, 100%)
6. Monitorar CloudWatch por 5-10min
7. Se tudo ok, Green vira Blue para próximo deploy

**Rollback**: se Green falhar, ALB já está apontando para Blue (instant rollback)

#### 4. Integração com GitHub

**Webhook**:
- GitHub → Settings → Webhooks
- URL: `https://jenkins.example.com/github-webhook/`
- Events: Push, Pull Request
- **Trigger**: cada push na main dispara pipeline automaticamente

**Branch Strategy**:
- `main` branch → deploy para produção (apenas se todos os testes passarem)
- `develop` branch → deploy para staging (ambiente de testes)
- Pull Requests → rodar build + test (não deploy, apenas validação)

---

### Rollback Strategies

**Cenário**: deploy passou no pipeline mas tem bug em produção detectado por usuários

**Opção 1: Re-executar Build Anterior (Jenkins)**
- Via Jenkins UI: clicar em build anterior e escolher "Rebuild"
- Via API: trigger build anterior via REST call
- **Tempo**: ~5-10min (precisa re-rodar todo pipeline)

**Opção 2: Rollback Direto no ECS (AWS CLI)**
- Listar task definitions para ver versões disponíveis
- Executar `aws ecs update-service` apontando para task definition anterior
- **Tempo**: ~2min (apenas muda ponteiro, não rebuilda)
- **Quando usar**: emergência, precisa voltar RÁPIDO

**Opção 3: Git Revert + Terraform**
- Reverter commit no Git: `git revert HEAD`
- Terraform aplica mudança automaticamente (via pipeline)
- **Tempo**: ~10min (mais lento mas mantém histórico auditável)
- **Quando usar**: mudança de infraestrutura que deu problema

---

## 🏗️ Infrastructure as Code (Terraform)

**Por quê Terraform** (vs CloudFormation):
- **Declarativo**: define estado desejado, Terraform calcula como chegar lá
- **State management**: rastreia recursos criados, detecta drift (mudanças manuais)
- **Multi-cloud**: funciona com AWS, GCP, Azure (flexibilidade futura)
- **Modules**: reutilização de código entre ambientes (dev/staging/prod)
- **Plan preview**: mostra exatamente o que vai mudar ANTES de aplicar (segurança)

**Nota**: Conforme PDF: "explanations are enough" - não vou escrever código Terraform (.tf) aqui, apenas explicar a estrutura e estratégia.

---

### Estrutura do Projeto Terraform

**Organização sugerida**:
```
terraform/
├── modules/              # Componentes reutilizáveis
│   ├── networking/       # VPC, Subnets, Internet Gateway, NAT
│   ├── ec2/              # EC2 instance, Security Group, EBS volume
│   ├── ecs/              # ECS Cluster, Service, Task Definition, ALB
│   ├── s3/               # S3 bucket (encryption, versioning, lifecycle)
│   └── monitoring/       # CloudWatch Alarms, Dashboards, SNS topics
├── environments/
│   ├── dev/              # Chama modules com valores de desenvolvimento
│   ├── staging/
│   └── prod/             # Chama modules com valores de produção
└── README.md
```

**Por quê essa estrutura**:
- **Modules**: escrever VPC uma vez, reusar em dev/prod com tamanhos diferentes
- **Environments**: configurações separadas (dev = t3.micro, prod = t3.small)
- **DRY (Don't Repeat Yourself)**: mudança no módulo reflete em todos ambientes

---

### State Management (Backend Remoto)

**Por quê state remoto** (não local):
- **Colaboração**: time inteiro acessa mesmo state (não fica na máquina de alguém)
- **Locking**: previne dois engenheiros aplicando mudanças simultâneas (via DynamoDB)
- **Backup automático**: S3 versioning permite recuperar state anterior se algo quebrar
- **Segurança**: state pode conter secrets (senhas, keys) - S3 tem encryption at rest

**Setup do backend**:
1. Criar bucket S3: `kotlin-backend-terraform-state`
2. Habilitar versioning no bucket
3. Criar tabela DynamoDB: `terraform-lock` (para state locking)
4. Configurar backend no Terraform para apontar para esse S3

**Estrutura do state**:
- `dev/terraform.tfstate` → state do ambiente de desenvolvimento
- `prod/terraform.tfstate` → state do ambiente de produção
- Separados para evitar mudar produção acidentalmente

---

### Módulos Terraform (Explicação Conceitual)

#### **Módulo: Networking**
**O que faz**:
- Criar VPC com CIDR block (ex: 10.0.0.0/16)
- Criar subnets públicas em múltiplas AZs (us-east-1a, us-east-1b) para alta disponibilidade
- Criar Internet Gateway para acesso à internet
- Configurar route tables associando subnets ao IGW

**Outputs**:
- `vpc_id`: ID da VPC criada (usado por outros módulos)
- `public_subnet_ids`: IDs das subnets (para colocar ALB, EC2, ECS)

**Por quê módulo separado**: VPC é usado por EC2, ECS, RDS, etc. Criar uma vez, reusar.

---

#### **Módulo: ECS Fargate**
**O que faz**:
- Criar ECS Cluster
- Definir Task Definition (imagem Docker, CPU, memória, variáveis de ambiente)
- Criar ECS Service (quantas tasks rodar, health checks, deployment strategy)
- Provisionar Application Load Balancer (ALB)
  - Target Group apontando para porta 8080
  - Health check em `/metrics`
  - Listener HTTP na porta 80
- Criar Security Groups:
  - ALB: aceita 80/443 de qualquer lugar (0.0.0.0/0)
  - ECS Tasks: aceita 8080 APENAS do ALB (princípio de least privilege)
- Criar IAM Roles:
  - **Execution Role**: ECS usa para pull da imagem do ECR, escrever logs
  - **Task Role**: aplicação usa para acessar S3, CloudWatch (permissões granulares)

**Inputs**:
- `vpc_id`, `subnet_ids` (do módulo networking)
- `ecr_repo`, `image_tag` (qual imagem Docker rodar)
- `desired_count` (quantas tasks, ex: 2 para HA)

**Outputs**:
- `alb_dns_name`: URL pública para acessar aplicação
- `ecs_cluster_name`, `ecs_service_name`: para usar no CI/CD (deploy)

---

#### **Módulo: S3**
**O que faz**:
- Criar bucket S3 com nome único (ex: `kotlin-backend-files-prod`)
- Habilitar versioning (histórico de arquivos)
- Configurar encryption at rest (AES-256 ou KMS)
- Lifecycle policy (mover arquivos antigos para Glacier após 90 dias)
- Block public access (bucket privado, apenas app acessa via IAM)

---

#### **Módulo: Monitoring**
**O que faz**:
- Criar CloudWatch Alarms:
  - Error rate > 5%
  - Target response time > 500ms
  - Unhealthy target count > 0
- Criar SNS Topic para notificações (email, Slack, PagerDuty)
- Criar CloudWatch Dashboard com métricas principais

---

### Composição de Ambientes

**Como usar os módulos** (ambiente de produção):

**Environment: `environments/prod/`**
- Chama módulo `networking` com VPC 10.0.0.0/16, 2 AZs
- Chama módulo `s3` com bucket `kotlin-backend-files-prod`
- Chama módulo `ecs` com:
  - VPC/subnets do módulo networking
  - Bucket S3 do módulo s3
  - 2 tasks (HA), CPU=512, Memory=1024
  - Imagem: latest do ECR
- Chama módulo `monitoring` com ARNs do ALB/Target Group

**Valores específicos de prod** (via `terraform.tfvars`):
- `aws_region = "us-east-1"`
- `desired_count = 2` (HA)
- `instance_type = "t3.small"` (se EC2)

**Environment: `environments/dev/`**
- Mesma estrutura, mas valores diferentes:
  - VPC menor (10.1.0.0/16)
  - 1 task apenas (economizar)
  - CPU=256, Memory=512 (menor)
  - Sem alarmes críticos (menos barulho)

---

### Workflow Terraform

**1. Inicializar** (primeira vez ou após clonar repo):
- `terraform init` baixa providers (AWS) e configura backend (S3)

**2. Planejar** (SEMPRE antes de aplicar):
- `terraform plan` mostra o que vai mudar:
  - `+` recursos que serão criados
  - `~` recursos que serão modificados
  - `-` recursos que serão deletados
- **Revisar cuidadosamente** antes de aplicar (evitar destruir produção acidentalmente)

**3. Aplicar**:
- `terraform apply` cria/modifica recursos na AWS
- Pede confirmação (digite "yes")
- Pode salvar plan: `terraform plan -out=tfplan` → `terraform apply tfplan` (pula confirmação)

**4. Ver outputs** (URLs, IDs):
- `terraform output` mostra valores exportados (ex: ALB URL, VPC ID)

**5. Atualizar imagem** (deploy novo):
- `terraform apply -var="image_tag=v1.2.3"` atualiza task definition no ECS

**6. Destruir** (cuidado, irreversível!):
- `terraform destroy` deleta TUDO (usar apenas em dev/staging)

---

### Boas Práticas Terraform

**1. Validação antes de commit**:
- `terraform fmt` (formatar código)
- `terraform validate` (checar sintaxe)
- Rodar `plan` em PR (ver mudanças antes de merge)

**2. Drift Detection**:
- Problema: alguém mudou recurso no console AWS manualmente
- Solução: `terraform plan` detecta drift (mostra mudanças não trackadas)
- Correção: `terraform apply` força estado desejado OU importar mudança manual

**3. Secrets Management**:
- NUNCA hardcode senhas/tokens no código Terraform
- Usar AWS Secrets Manager ou Parameter Store
- Terraform lê secret e injeta em runtime

**4. Módulos Versionados**:
- Publicar módulos em repo separado com tags (v1.0.0, v2.0.0)
- Ambientes usam versões específicas (prod = v1.0.0, dev = v2.0.0-beta)
- Evita quebrar prod com mudança experimental

---

### Integração com CI/CD (Jenkins)

**Pipeline com Terraform**:
1. Stage "Terraform Plan": rodar `plan` e salvar output
2. Stage "Manual Approval": engenheiro revisa mudanças
3. Stage "Terraform Apply": aplicar se aprovado
4. **Por quê approval manual**: mudança de infra pode ser destrutiva (deletar DB, etc.)

**Quando rodar**:
- Push na `main` → plan + apply (se aprovado)
- Pull Request → plan only (preview de mudanças, não aplica)

---

### 📊 Métricas de Produção e Por Quê Monitorá-las

**Métricas implementadas**:
1. **`uploads`**: total de arquivos salvos
2. **`reads`**: total de leituras
3. **`errors`**: total de erros (404, 500, etc.)

#### Por Quê Essas Métricas São Importantes

**1. `uploads` - Volume de Upload**

**O que mede**: quantidade de arquivos salvos no sistema

**Por quê monitorar**:
- **Capacity Planning**: se uploads crescem 20% ao mês, preciso provisionar mais storage
- **Detecção de anomalias**: spike súbito pode indicar:
  - Campanha de marketing (esperado)
  - Abuso/bot malicioso (investigar)
  - Bug no cliente fazendo upload duplicado
- **Business metrics**: correlaciona com adoção do produto

**Alarmes sugeridos**:
- `uploads > 10.000/hora` → alerta de tráfego alto (preparar para escalar)
- `uploads = 0 por 10min` → critical (serviço pode estar down)

**Exemplo real**: Se storage é 100GB e média de upload é 500 arquivos/dia de 10MB cada, em 20 dias vai encher. Alarme deve disparar quando disk usage > 80%.

---

**2. `reads` - Volume de Download**

**O que mede**: quantidade de arquivos lidos do sistema

**Por quê monitorar**:
- **Performance**: reads alto com latência alta = gargalo de IO (considerar cache/CDN)
- **Hotspot detection**: 80% dos reads em 20% dos arquivos? Usar CloudFront (CDN)
- **Usage patterns**: reads >> uploads = workload read-heavy (otimizar para leitura)

**Alarmes sugeridos**:
- `reads > 50.000/hora` → alerta para considerar CDN
- `reads = 0 por 10min` + `uploads > 0` → problema no endpoint GET (bug crítico)

**Exemplo real**: Se P95 latency de reads > 200ms e `reads > 10k/min`, usuários vão ter experiência ruim. Solução: cache no CloudFront (reduz reads no backend em 90%).

---

**3. `errors` - Taxa de Erro**

**O que mede**: total de erros (404 file not found, 500 server error, 400 bad request)

**Por quê monitorar**:
- **Saúde do sistema**: errors > 5% do tráfego total = algo muito errado
- **UX impact**: erro = usuário frustrado = churn
- **Triagem**: separar 404 (normal, usuário buscou arquivo que não existe) de 500 (bug)

**Métrica derivada importante**:
```
Error Rate (%) = (errors / (uploads + reads)) * 100
```

**Por quê usar %**:
- 1000 erros com 1M requests = 0.1% (ok)
- 1000 erros com 2000 requests = 50% (crítico!)
- % normaliza pelo volume de tráfego

**Alarmes sugeridos**:
- `error_rate > 5%` → alerta (investigar logs)
- `error_rate > 10%` → critical (possível outage, acionar on-call)
- `errors de tipo 500 > 10` → critical (bug no código)

**Exemplo real**: Deploy novo → error rate sobe de 0.5% para 8% → rollback automático + investigar commit.

---

#### Métricas Adicionais para Considerar (Não Implementadas)

**Se fosse produção real, eu adicionaria**:

**4. Latência (P50, P95, P99)**
- **Por quê**: 1% dos usuários (P99) pode estar com experiência péssima mesmo com P50 baixo
- **SLA**: "99% dos requests < 200ms" = monitorar P99
- **Alertar**: P95 > 500ms = degradação

**5. Disk Usage** (se usar filesystem local)
- **Por quê**: disk full = serviço para de funcionar completamente
- **Alarme**: > 80% = alerta, > 90% = cleanup automático ou escalar storage

**6. Request Rate (RPS - Requests Per Second)**
- **Por quê**: detectar DDoS, traffic spike, ou correlacionar com custo (AWS cobra por request no ALB)

**7. Tamanho Médio de Arquivo**
- **Por quê**: se média sobe de 5MB para 50MB, uploads vão demorar mais (ajustar timeout)

---

#### Como Visualizar no CloudWatch Dashboard

**Dashboard exemplo**:
```
┌─────────────────────────────┐  ┌─────────────────────────────┐
│  File Operations (Sum)      │  │  Error Rate (%)             │
│  ─ uploads: 1,234           │  │  ─ 2.3%                     │
│  ─ reads: 5,678             │  │  (threshold: 5%)            │
│  ─ errors: 23               │  │                             │
└─────────────────────────────┘  └─────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Uploads vs Reads Over Time (Last 24h)                      │
│  [Line graph showing trends]                                │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Recent Errors (Logs)                                       │
│  [Table: timestamp | level | message]                       │
└─────────────────────────────────────────────────────────────┘
```

**Como isso ajuda operações**:
- **On-call**: dashboard único mostra saúde do sistema
- **Postmortem**: correlacionar deploy com spike de erros
- **Budgeting**: `uploads * avg_size = storage cost`

---

## 🧪 Testes

### Rodar testes
```bash
./gradlew test        # Todos os testes (unit + integration)
```

---

## 🛡️ Segurança

### Implementado
- Path traversal protection (validação de filename)
- Non-root container user
- Input validation (400 Bad Request)

### Produção (não implementado agora)
- Rate limiting (ex: 100 uploads/min por IP)
- Autenticação (JWT via API Gateway)
- Criptografia em trânsito (TLS)
- Criptografia em repouso (S3 com KMS)

---

## 📝 Decisões Técnicas

### Arquitetura
- **3 camadas**: Controller → Service → Repository
- **Por quê não Clean Architecture completa**: overengineering para 3 endpoints
- **Trade-off**: simplicidade vs inversão de dependência total

### Thread-Safety
- **AtomicLong** para métricas (concorrência segura)
- **Alternativa descartada**: synchronized blocks (mais verbose, menos performático)

### Validação de Filename
- **Regex**: `^[a-zA-Z0-9._-]+$` (conservador)
- **Por quê**: evitar path traversal, injeções, problemas cross-platform
- **Limitação**: não aceita Unicode (ex: "fichário.txt") - ok para MVP

### Storage Local vs S3
- **Local agora**: requisito do PDF (simples, sem SDK)
- **S3 em produção**: durabilidade, versioning, lifecycle policies

---

## 🚨 Troubleshooting

### Container não inicia
```bash
docker logs <container-id>
# Verificar se porta 8080 já está em uso
lsof -i :8080
```

### Arquivo não é salvo
```bash
# Verificar permissões do /storage
docker exec <container-id> ls -la /app/storage
```

### Métricas zeradas após restart
**Esperado**: métricas são in-memory (não persistem)
**Produção**: enviar para CloudWatch antes de shutdown graceful

---

## 👤 Autor
[Seu Nome] - Staff Backend Engineer
```

---

## 8. Checklist Final de Aceitação

**Rodar ANTES de commitar**:

### ✅ Funcionalidade
```bash
# 1. Build
./gradlew clean build

# 2. Rodar localmente
./gradlew bootRun &
sleep 5

# 3. Testar POST
curl -X POST http://localhost:8080/files \
  -H "Content-Type: application/json" \
  -d '{"filename":"test.txt","content":"Hello Nike"}'
# Deve retornar 200

# 4. Testar GET
curl http://localhost:8080/files/test.txt
# Deve retornar "Hello Nike"

# 5. Testar GET 404
curl -I http://localhost:8080/files/naoexiste.txt
# Deve retornar 404

# 6. Verificar métricas
curl http://localhost:8080/metrics
# Deve mostrar {"uploads":1,"reads":1,"errors":1}

# 7. Testar filename inválido
curl -X POST http://localhost:8080/files \
  -H "Content-Type: application/json" \
  -d '{"filename":"../etc/passwd","content":"hack"}'
# Deve retornar 400

pkill -f kotlin-backend
```

### ✅ Testes
```bash
# 8. Rodar todos os testes
./gradlew test
# Deve passar todos os testes
```

### ✅ Docker
```bash
# Build da imagem
docker build -t kotlin-backend .

# Rodar container
docker run -d -p 8080:8080 --name test-backend kotlin-backend
sleep 5

# Testar no container
curl http://localhost:8080/metrics

# Verificar logs
docker logs test-backend
# Não deve ter erros

# Cleanup
docker stop test-backend
docker rm test-backend
```

### ✅ Código
```bash
# 9. Verificar que /storage não está no Git
git status
# Não deve aparecer arquivos em storage/

# 10. Verificar README
cat README.md
# Deve ter seções completas de AWS, CI/CD, IaC, Métricas
```

### ✅ Git
```bash
# 11. Commitar tudo
git add .
git commit -m "feat: implement file storage service with metrics"

# 12. Verificar timestamp do commit
git log -1 --format="%ai"
# Deve estar dentro da janela de 30min

# 13. Push para repositório público
git remote add origin <seu-repo-github>
git push -u origin main
```

---

## 9. Plano de Execução em 30 Minutos

**Objetivo**: entregar 100% funcional dentro do timebox

### ⏱️ Timebox Breakdown

#### **0-5min: Setup**
1. ✅ Criar projeto Spring Boot via start.spring.io
   - Dependencies: Spring Web, Kotlin
   - Gradle, Java 17
2. ✅ Criar estrutura de pastas
3. ✅ Criar `application.yml` básico
4. ✅ Criar DTOs (3 classes simples)
5. ✅ Criar custom exceptions (2 classes)

**Entrega**: projeto compila

---

#### **5-15min: Implementação Core (10min)**
**Prioridade 1** (não negociável):
1. ✅ `LocalFileRepository` (5min)
   - Métodos write/read
   - Criar diretório storage
   - **Testar manualmente** (não escrever unit test ainda)

2. ✅ `MetricsService` (2min)
   - 3 AtomicLong + métodos
   - Trivial, sem teste por ora

3. ✅ `FileService` (3min)
   - Validação de filename (regex)
   - Delegar para repository
   - Incrementar métricas
   - **Pular tratamento de erro sofisticado por ora**

**Entrega**: lógica de negócio completa

---

#### **15-20min: Controllers e Exception Handler (5min)**
1. ✅ `FileController` (3min)
   - POST e GET endpoints
   - Mapeamento básico

2. ✅ `MetricsController` (1min)
   - Retornar MetricsResponse

3. ✅ `GlobalExceptionHandler` (1min)
   - Mapear 400, 404, 500
   - Incrementar errors no handler

**Testar com curl**:
```bash
./gradlew bootRun &
curl -X POST localhost:8080/files -d '{"filename":"t.txt","content":"hi"}' -H "Content-Type: application/json"
curl localhost:8080/files/t.txt
curl localhost:8080/metrics
```

**Entrega**: endpoints funcionando

---

#### **20-22min: Docker (2min)**
1. ✅ Criar Dockerfile (2min)
   - Usar multi-stage build do plano
   - Testar build rápido: `docker build -t kotlin-backend .`

2. ✅ Criar docker-compose.yml (incluído no tempo)
   - Volume para /storage
   - Porta 8080

**Entrega**: Docker builda

---

#### **22-28min: README (6min) - CRÍTICO**
⚠️ **README é "Very Important" no PDF - não pule ou apresse!**

1. ✅ Estrutura básica (1min)
   - Título, descrição, como rodar

2. ✅ Deploy na AWS (3min)
   - EC2: passos principais, EBS, Security Group, por quê escolher
   - S3: como migrar local→S3, vantagens (durabilidade, custo)
   - CloudWatch: métricas custom, alarmes, logs

3. ✅ CI/CD com Jenkins (1min)
   - **Explicar stages**: Build → Test → Security Scan → Docker → Deploy → Smoke Tests
   - Rollback strategies (3 opções)
   - Blue/Green deployment
   - **Não escrever Jenkinsfile completo** (conforme PDF: "explanations are enough")
   
4. ✅ IaC com Terraform (1min)
   - **Explicar estrutura**: módulos (networking, ECS, S3, monitoring)
   - Backend S3 + DynamoDB para state
   - Workflow: init → plan → apply
   - Boas práticas: drift detection, secrets management
   - **Não escrever código .tf** (conforme PDF: "explanations are enough")

5. ✅ Métricas de Produção (incluído acima)
   - Por quê uploads, reads, errors
   - Alarmes sugeridos

**Entrega**: README completo com raciocínio técnico sênior

---

#### **28-30min: Testes + Git (2min)**
1. ✅ Escrever 1-2 testes essenciais (1min)
   - `FileServiceTest.should_reject_path_traversal` OU
   - `IntegrationTest.upload_and_download_flow`
   - Se não der tempo, PULE os testes (README > testes)

2. ✅ Git commit (1min)
   ```bash
   git init
   git add .
   git commit -m "feat: implement file storage service with metrics"
   git remote add origin <repo>
   git push -u origin master
   ```

**Entrega**: tudo commitado, pronto para avaliação

---

### 🎯 O Que Cortar Se Ficar Sem Tempo

**Prioridade Absoluta** (NUNCA corte):
1. ✅ POST/GET/Metrics endpoints funcionando
2. ✅ Dockerfile buildando
3. ✅ **README completo** (é "Very Important" no PDF - peso altíssimo na avaliação)

**Se restar 20min** (perdeu 10min):
- ❌ Todos os testes (foque em funcionalidade + README)
- ❌ docker-compose.yml (só Dockerfile)
- ❌ Multi-stage build (Dockerfile simples em 1 stage)
- ❌ GlobalExceptionHandler (deixar Spring lidar com erros default)
- ✅ Validação de filename (manter - é segurança básica)

**Se restar 15min** (perdeu 15min - situação crítica):
- ❌ Todos os testes
- ❌ docker-compose.yml  
- ❌ GlobalExceptionHandler
- ❌ Validação complexa de filename (só rejeitar `..`)
- ⚠️ README AINDA É OBRIGATÓRIO mas pode ser mais curto (bullets ao invés de parágrafos)

**Estratégia de README em modo emergência** (se sobrar <5min):
- Seção "Como Rodar": básico (docker build/run)
- Deploy AWS: bullets dos serviços (EC2 + S3 + CloudWatch) com 1 linha cada
- CI/CD: listar stages do Jenkins (Build → Test → Deploy)
- Terraform: listar módulos principais (EC2, ECS, monitoring)
- Métricas: 1 parágrafo dizendo por quê uploads/reads/errors importam

---

### 🧠 Dicas Mentais para Executar

1. **README é prioridade máxima**: PDF marca como "Very Important" - reserve 6min COMPLETOS para ele
2. **Não otimize prematuramente**: código funcionando > código perfeito
3. **Testar manualmente com curl é mais rápido**: unit tests são desejáveis mas não críticos
4. **Use o template do plano**: copie as seções do README deste documento e adapte
5. **Docker deve buildar**: `docker build -t kotlin-backend .` sem erros é mandatório
6. **Demonstre raciocínio sênior no README**: não só "o quê" mas "por quê" (ex: "EC2 t3.small porque custo previsível e suficiente para ~100 req/s")
7. **Timebox rigoroso**: se passar 3min em uma tarefa, PARE e vá para próxima

---

## 📐 Diagrama de Arquitetura (Resumo Visual)

```
┌─────────────────────────────────────────────────────────┐
│                    HTTP Requests                        │
└────────────────────┬────────────────────────────────────┘
                     │
         ┌───────────┴────────────┐
         │                        │
         ▼                        ▼
  ┌─────────────┐        ┌──────────────┐
  │FileController│        │MetricsController│
  └──────┬───────┘        └──────┬────────┘
         │                        │
         │ validates              │ reads
         │                        │
         ▼                        ▼
  ┌─────────────┐        ┌──────────────┐
  │ FileService │───────▶│MetricsService│
  │             │  incr  │(AtomicLong)  │
  └──────┬──────┘        └──────────────┘
         │
         │ delegates IO
         │
         ▼
  ┌──────────────────┐
  │LocalFileRepository│
  │   (java.nio)     │
  └────────┬──────────┘
           │
           ▼
     /storage/*.txt
```

---

## ✅ Resumo Executivo

**O que você vai construir**:
- API REST com 3 endpoints (POST/GET files, GET metrics)
- Armazenamento local em `/storage` (simula S3)
- Métricas thread-safe in-memory (`uploads`, `reads`, `errors`)
- Validação de filename (anti path-traversal)
- Testes essenciais (se der tempo)
- Dockerfile multi-stage + docker-compose
- **README extenso e detalhado**:
  - Deploy AWS (EC2, ECS, S3, CloudWatch) - passos e comandos
  - CI/CD Jenkins - **explicação dos stages** (não Jenkinsfile completo)
  - Terraform - **explicação da estrutura** (não código .tf completo)
  - Métricas - por quê monitorar cada uma

**Por quê essa arquitetura**:
- **Simplicidade**: 3 camadas (Controller → Service → Repository), ~7 arquivos principais
- **Pragmática**: sem Clean Architecture completa (overkill para 3 endpoints)
- **Segura**: validação de input, non-root container
- **Production-minded**: README demonstra raciocínio sênior sobre AWS/DevOps

**Trade-offs conscientes**:
- Arquitetura 3 camadas vs Clean Architecture completa: simplicidade para 30min
- Métricas in-memory vs persistidas: suficiente para desafio, CloudWatch em prod
- Storage local vs S3: requisito do PDF, README explica migração
- Testes mínimos vs cobertura alta: README > testes no peso da avaliação
- Regex conservadora: sem Unicode mas seguro contra path traversal

**Priorização para 30min**:
1. **Funcionalidade**: 3 endpoints funcionando (15min)
2. **README**: completo e detalhado (6min) - "Very Important"
3. **Docker**: buildando (2min)
4. **Testes**: 1-2 testes se sobrar tempo (1min)

**Pronto para executar**: siga o timebox rigorosamente, README é prioridade máxima.

---

## 📝 Principais Ajustes Feitos no Plano

**Conforme seu feedback, o plano foi otimizado para parecer um teste real em 30min**:

### ✅ Removido (overengineering)
- ❌ Observabilidade adicional não pedida (latência P95, disk usage, etc.)
- ❌ JaCoCo e gates de cobertura (não pede, consome tempo)
- ❌ Clean Architecture completa (mantida estrutura 3 camadas simples)

### ✅ Mantido (essencial)
- ✅ Estrutura de pastas pragmática (Controller → Service → Repository)
- ✅ Estratégia de testes realista (foco em path traversal + integration test)
- ✅ Dockerfile multi-stage + docker-compose
- ✅ Métricas apenas as 3 pedidas: uploads, reads, errors

### ✅ Expandido (crítico conforme PDF)
- 🚀 **README MUITO DETALHADO** (seção "Very Important" do PDF)
  - Deploy EC2: passos detalhados, user-data script, IAM roles, EBS, Security Groups
  - Deploy ECS Fargate: arquitetura, task definition, ALB, auto-scaling
  - Migração para S3: código Kotlin de exemplo, configuração
  - CloudWatch: logs estruturados, custom metrics, alarmes, dashboard
  - Jenkins: **explicação dos stages** (Build → Test → Deploy → Smoke), rollback, Blue/Green
  - Terraform: **explicação da estrutura** (módulos, backend, workflow), sem código .tf
  - Métricas: explicação detalhada do POR QUÊ monitorar cada uma (uploads/reads/errors)
- 🎯 Timebox ajustado: 6min para README (vs 3min antes) - é prioridade #1
- ⚠️ **Conforme PDF**: "explanations are enough" - não incluir código Terraform/Jenkinsfile completo

### ✅ Decisões Técnicas
- **Terraform** como solução de IaC (não CloudFormation)
- **Jenkins** como solução de CI/CD
- **README ~1200 linhas** demonstrando raciocínio Staff Engineer
  - Não só "como fazer" mas "por quê fazer"
  - Trade-offs explicados (EC2 vs Fargate, EBS vs S3, etc.)
  - Comandos AWS CLI quando necessário (setup EC2, deploy ECS)
  - **IMPORTANTE**: Jenkins e Terraform = EXPLICAÇÕES (stages, módulos, workflow), não código completo
  - Conforme PDF: "You do not need to write Terraform, CloudFormation, or Jenkins pipelines, explanations are enough"
  - Exemplos de código: apenas Kotlin (S3FileRepository) e shell scripts (user-data.sh)

### ⚡ Priorização para 30min
1. **Funcionalidade** (15min): 3 endpoints + validação
2. **README** (6min): completo, demonstra seniority
3. **Docker** (2min): multi-stage build
4. **Git** (1min): commit + push
5. **Testes** (1min): se sobrar tempo

### 📝 Correção Final (Importante!)
**Conforme feedback do usuário**: O PDF diz *"You do not need to write Terraform, CloudFormation, or Jenkins pipelines, explanations are enough"*

**Ajuste no README**:
- ✅ Jenkins: explicar STAGES e estratégia (não escrever Jenkinsfile completo)
- ✅ Terraform: explicar ESTRUTURA e módulos (não escrever código .tf completo)
- ✅ Foco em explicações conceituais, arquitetura, decisões técnicas
- ✅ Código apenas quando essencial: Kotlin (S3FileRepository), shell scripts (user-data.sh)

**Resultado**: plano executável em 30min que maximiza pontuação nas áreas avaliadas (Kotlin, Backend Design, AWS Mindset, Observability, DevOps, Seniority), com README focado em EXPLICAÇÕES ao invés de código IaC/CI/CD.

---

**🚀 Aguardando sua aprovação para iniciar a implementação!**
