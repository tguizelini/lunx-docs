# Kotlin Backend Challenge - File Storage Service

## 📋 About the Project

Backend service built with Kotlin/Spring Boot that simulates a file storage system (S3-style) with observability metrics (CloudWatch-style).

**Goal**: Demonstrate how a local service can be mapped to a production AWS architecture, including deployment considerations, CI/CD, and infrastructure as code.

---

## 🚀 How to Run Locally

### Prerequisites
- Docker installed
- OR Java 17+ and Gradle 8+ (to run without Docker)

### Option 1: With Docker (Recommended)
```bash
docker build -t kotlin-backend .
docker run -p 8080:8080 kotlin-backend
```

### Option 2: With Docker Compose
```bash
docker-compose up --build
```

### Option 3: Without Docker (Development)
```bash
./gradlew bootRun
```

---

## 📡 Endpoints

### POST /files
File upload
```bash
curl -X POST http://localhost:8080/files \
  -H "Content-Type: application/json" \
  -d '{"filename":"test.txt","content":"Hello Nike"}'
```

### GET /files/{filename}
File download
```bash
curl http://localhost:8080/files/test.txt
# Returns: Hello Nike
```

### GET /metrics
Observability metrics
```bash
curl http://localhost:8080/metrics
# Returns: {"uploads":1,"reads":1,"errors":0}
```

---

## ☁️ AWS Deployment Strategy

### 🎯 Architecture Overview

**Current service (local)**:
- Docker container running Spring Boot
- Filesystem storage (`/storage`)
- In-memory metrics

**Production AWS architecture**:
- **Compute**: EC2 (simple) or ECS Fargate (scalable)
- **Storage**: Amazon S3 (99.999999999% durability)
- **Observability**: CloudWatch (Logs + Metrics + Alarms)
- **Networking**: VPC, ALB, Security Groups
- **Security**: IAM Roles, Secrets Manager, non-root containers

---

### 📦 1. EC2 Deployment (Simple Option)

**Why choose EC2**:
- ✅ Full control over environment
- ✅ Predictable cost (~$15/month for t3.small)
- ✅ Ideal for steady-state workload (no large spikes)
- ✅ Easier debugging (direct SSH access)
- ❌ Single point of failure (mitigate with Auto Scaling Group)

#### Deployment Steps

**1.1. Provision EC2 Instance**
- **Type**: t3.small (2 vCPU, 2GB RAM) - sufficient for ~100 req/s
- **AMI**: Amazon Linux 2023 (Docker pre-installed)
- **EBS Volume**: 20GB gp3 (for OS) + 50GB additional (for `/data/storage`)
- **Security Group**: 
  - Inbound: port 8080 (or 80/443 if using ALB)
  - Outbound: all traffic (for docker pull from ECR)
- **IAM Instance Profile**: permissions for ECR (pull images), CloudWatch (logs/metrics)

**Why t3.small**: 
- Sufficient to start
- Burstable CPU (uses credits for spikes)
- Scale up: migrate to t3.medium or use Auto Scaling Group

**1.2. User Data Script (Automatic Bootstrap)**

Create script that runs on first instance initialization:

```bash
#!/bin/bash
# Install Docker (if not pre-installed)
yum update -y
yum install -y docker
systemctl start docker
systemctl enable docker

# Create storage directory
mkdir -p /data/storage
chown -R ec2-user:ec2-user /data/storage

# Login to ECR (Amazon Container Registry)
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Run container
docker run -d \
  --name kotlin-backend \
  --restart unless-stopped \
  -p 8080:8080 \
  -v /data/storage:/app/storage \
  --log-driver=awslogs \
  --log-opt awslogs-region=us-east-1 \
  --log-opt awslogs-group=/aws/ec2/kotlin-backend \
  <account-id>.dkr.ecr.us-east-1.amazonaws.com/kotlin-backend:latest
```

**Explanation**:
- `--restart unless-stopped`: container restarts automatically if it crashes
- `-v /data/storage:/app/storage`: persists files on EBS (survives container restart)
- `--log-driver=awslogs`: sends logs to CloudWatch automatically

**1.3. Data Persistence (Avoid Loss)**

**Problem**: EC2 instance can be terminated (maintenance, failure, update)

**Solution: Separate EBS Volume**
- Create 50GB EBS volume separate from instance
- Mount at `/data/storage`
- Benefits:
  - Volume persists even if instance dies
  - Can attach to another instance (disaster recovery)
  - Automatic daily snapshots via Data Lifecycle Manager
  
**Commands**:
```bash
# On EC2, mount additional volume
sudo mkfs -t ext4 /dev/xvdf
sudo mount /dev/xvdf /data/storage
echo '/dev/xvdf /data/storage ext4 defaults,nofail 0 2' | sudo tee -a /etc/fstab
```

**1.4. Security**

**IAM Instance Profile** (required):
- ECR permissions: `GetAuthorizationToken`, `BatchGetImage`
- CloudWatch permissions: `CreateLogGroup`, `PutLogEvents`
- No hardcoded credentials (IAM role attached to instance)

**Security Group** (firewall):
- Inbound: port 8080 from 0.0.0.0/0 (or only from ALB)
- Outbound: all traffic (to pull images, send metrics)

**Best practices**:
- Don't expose SSH (use AWS Systems Manager Session Manager)
- Use ALB for SSL/TLS termination
- Update AMI regularly (security patches)

---

### 🚀 2. ECS Fargate Deployment (Scalable Option)

**Why choose Fargate**:
- ✅ Serverless (no EC2 management)
- ✅ Auto-scaling (from 0 to N tasks)
- ✅ Multi-AZ by default (high availability)
- ✅ Pay only for what you use (per second of CPU/RAM)
- ❌ ~30% more expensive than EC2 for constant workload
- ❌ Cold start can add 2-3s latency

#### ECS Architecture

```
Internet → ALB → ECS Service (2+ tasks) → S3 (storage)
                      ↓
                  CloudWatch
```

**Components**:
1. **ECS Cluster**: logical grouping of tasks
2. **Task Definition**: container "recipe" (image, CPU, RAM, variables)
3. **ECS Service**: maintains N running tasks, integrates with ALB
4. **ALB**: distributes traffic across tasks, health checks

#### Task Definition (Container Configuration)

**Resources**:
- **CPU**: 512 (0.5 vCPU)
- **Memory**: 1024 MB (1 GB)
- **Network Mode**: awsvpc (each task gets its own IP)

**Why these values**:
- Application is lightweight (IO-bound, not CPU-bound)
- 512/1024 is sufficient to start
- Can scale horizontally (more tasks) if needed

**Container Configuration**:
- **Image**: `<ecr-repo>:latest`
- **Port**: 8080
- **Environment Variables**: 
  - `SPRING_PROFILES_ACTIVE=aws` (to use S3 instead of local storage)
- **Health Check**: `curl -f http://localhost:8080/metrics || exit 1`
  - Interval: 30s
  - Timeout: 5s
  - Retries: 3
  - Start Period: 60s (warm-up time)

**Logging**:
- Driver: `awslogs`
- Log Group: `/ecs/kotlin-backend`
- Stream: `ecs/task-id`

**IAM Roles** (Two distinct roles):
1. **Execution Role**: ECS uses to pull image from ECR and write logs
   - Policies: `AmazonECSTaskExecutionRolePolicy`
2. **Task Role**: Application uses to access S3, CloudWatch
   - Policies: custom (e.g., `s3:GetObject`, `s3:PutObject` on specific bucket)

**Why two roles**: principle of least privilege (separation of responsibilities)

#### ECS Service (Orchestration)

**Configuration**:
- **Desired Count**: 2 (high availability)
- **Launch Type**: FARGATE
- **Deployment**: Rolling update (one task at a time)
- **Load Balancer**: ALB Target Group on port 8080
- **Auto Scaling**: based on CPU > 70%

**Auto Scaling Policy**:
- Min: 2 tasks (always 2 running for HA)
- Max: 10 tasks
- Trigger: CPU > 70% for 2 periods of 5min → add task
- Scale down: CPU < 70% for 10min → remove task

**Why 2 tasks minimum**: 
- If one task dies, another continues serving traffic (zero downtime)
- Distribution across multiple AZs (us-east-1a, us-east-1b)

#### Application Load Balancer (ALB)

**Configuration**:
- **Scheme**: internet-facing (public)
- **Subnets**: 2+ subnets in different AZs
- **Security Group**: accepts 80/443 from 0.0.0.0/0
- **Listener**: HTTP:80 → forward to Target Group

**Target Group**:
- **Protocol**: HTTP:8080
- **Target Type**: IP (Fargate uses IPs, not instance IDs)
- **Health Check**:
  - Path: `/metrics`
  - Healthy threshold: 2 consecutive successful checks
  - Unhealthy threshold: 3 consecutive failed checks
  - Interval: 30s
  - Matcher: HTTP 200

**Why use ALB**:
- SSL/TLS termination (certificate managed by ACM)
- Automatically distributes traffic across tasks
- Health checks remove unhealthy tasks from pool
- Integrates with WAF (DDoS protection)

---

### 🗄️ 3. S3 Migration

**Why S3 instead of local filesystem**:
- ✅ Durability: 99.999999999% (11 nines) - virtually impossible to lose data
- ✅ Scalability: unlimited storage (vs limited EBS)
- ✅ Cost: $0.023/GB/month (vs EBS $0.08/GB/month)
- ✅ Versioning: change history (file rollback)
- ✅ Lifecycle: move old files to Glacier (99% cheaper)
- ✅ Multi-AZ by default: data replicated across 3+ zones

#### Code Changes

**Create interface for abstraction**:
```kotlin
interface FileRepository {
    fun save(filename: String, content: String)
    fun read(filename: String): String
}
```

**S3 Implementation**:
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
                .contentType("text/plain")
                .build(),
            RequestBody.fromString(content, StandardCharsets.UTF_8)
        )
    }
    
    override fun read(filename: String): String {
        try {
            return s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build()
            ).asUtf8String()
        } catch (e: NoSuchKeyException) {
            throw FileNotFoundException("File not found: $filename")
        }
    }
}
```

**Configuration** (`application-aws.yml`):
```yaml
aws:
  s3:
    bucket: kotlin-backend-files-prod
    region: us-east-1
```

**Add dependency** (`build.gradle`):
```groovy
implementation 'software.amazon.awssdk:s3:2.20.0'
```

#### S3 Bucket Setup

**Security Settings**:
- **Block Public Access**: ENABLED (private bucket)
- **Versioning**: ENABLED (file history)
- **Encryption**: AES-256 or KMS (encryption at rest)
- **Lifecycle Policy**: move to Glacier after 90 days (cost savings)

**IAM Policy for Task Role**:
```json
{
  "Effect": "Allow",
  "Action": [
    "s3:GetObject",
    "s3:PutObject",
    "s3:DeleteObject",
    "s3:ListBucket"
  ],
  "Resource": [
    "arn:aws:s3:::kotlin-backend-files-prod/*",
    "arn:aws:s3:::kotlin-backend-files-prod"
  ]
}
```

**Why not implement S3 now**: 
- PDF requires local-only (simulate AWS)
- In real production, S3 is mandatory (durability and scalability)

---

### 📊 4. CloudWatch Integration

**Goal**: Complete observability - logs, metrics, and alarms

#### 4.1. CloudWatch Logs (Application Logs)

**Setup with Docker** (already configured in user-data script):
- Log driver: `awslogs`
- Log group: `/aws/kotlin-backend`
- Stream: automatic per container

**Recommended log format** (structured JSON):
```json
{
  "timestamp": "2026-02-10T15:30:00Z",
  "level": "ERROR",
  "thread": "http-nio-8080-exec-1",
  "class": "FileService",
  "message": "File not found: test.txt"
}
```

**Why JSON**:
- CloudWatch Insights can run SQL-like queries
- Easy to create dashboards and filters
- Integrates with alerting tools

**Useful query (CloudWatch Insights)**:
```sql
fields @timestamp, level, message
| filter level = "ERROR"
| stats count() by bin(5m)
```

**Retention**:
- Production: 30 days (compliance)
- Development: 7 days (cost savings ~$0.50/GB/month)

---

#### 4.2. CloudWatch Metrics (Custom Metrics)

**Metrics implemented in code**:
- `uploads`: total files saved
- `reads`: total reads
- `errors`: total errors (404, 500, etc.)

**How to send to CloudWatch**:

**Option 1: SDK in code (recommended)**
- Add CloudWatch client
- Scheduled task: publish metrics every 1 minute
- Namespace: `KotlinBackend`
- Dimensions: `Environment=prod`, `Service=file-storage`

**Option 2: CloudWatch Agent (no code)**
- Agent polls `/metrics` endpoint every 1min
- Parses JSON and sends to CloudWatch
- Advantage: doesn't couple code with AWS SDK

**System metrics (EC2 only)**:
- CPU Utilization (native EC2)
- Memory Usage (via CloudWatch Agent)
- Disk Usage of `/data/storage` (via CloudWatch Agent)
- Network In/Out

---

#### 4.3. CloudWatch Alarms

**Alarm 1: High Error Rate**
- Metric: `Errors`
- Statistic: Sum
- Period: 5 minutes
- Evaluation Periods: 2 (10min consecutive)
- Threshold: > 100 errors
- Action: SNS → Slack/PagerDuty

**Why 2 periods**: avoids false alarms (1 spike doesn't trigger)

**Alarm 2: High Disk Usage (EC2)**
- Metric: `disk_used_percent`
- Threshold: > 80%
- Action: SNS → notify ops to cleanup or increase volume

**Alarm 3: High CPU (Auto Scaling Trigger)**
- Metric: `CPUUtilization`
- Threshold: > 70% for 2 periods of 5min
- Action: Auto Scaling Policy → add EC2/task

**Alarm 4: Unhealthy Target Count (ALB)**
- Metric: `UnHealthyHostCount`
- Threshold: > 0 for 2 minutes
- Action: SNS → critical alert (no healthy tasks)

---

#### 4.4. CloudWatch Dashboard

**Suggested widgets**:
1. **File Operations (Line Graph)**: uploads, reads, errors over time
2. **Error Rate %**: `errors / (uploads + reads) * 100`
3. **System Metrics**: CPU, Memory, Disk Usage
4. **Recent Errors (Log Widget)**: last 20 errors from logs
5. **ALB Metrics**: Target Response Time, Request Count

**Why single dashboard**:
- On-call visualizes system health in one place
- Correlate deployment with error spikes (postmortem)
- Demonstrate health to stakeholders

---

## 🔄 CI/CD Pipeline (Jenkins)

**Goal**: Automate build, test, deploy, and rollback safely

### Pipeline Stages (Conceptual Explanation)

**Note**: Per PDF requirements, not writing complete Jenkinsfile, just explaining the strategy.

#### **Stage 1: Build & Test**
- Execute `./gradlew clean build` to compile application
- Run `./gradlew test` to execute all tests
- Publish test reports (JUnit XML + HTML report)
- **Gate**: if any test fails, pipeline stops here

#### **Stage 2: Security Scan**
- **SAST (Static Analysis)**: use SonarQube or Snyk to detect code vulnerabilities
- **Dependency Check**: verify libraries for known vulnerabilities (CVE)
- **Threshold**: fail if finding HIGH or CRITICAL vulnerability
- **Why**: catch security issues before production

#### **Stage 3: Build Docker Image**
- Execute `docker build -t <ecr-repo>:$BUILD_NUMBER .`
- Also tag as `:latest` for easier local testing
- **Why tag with BUILD_NUMBER**: enables rollback to specific version

#### **Stage 4: Container Security Scan**
- Use Trivy (or Clair) to scan Docker image
- Look for vulnerabilities in base image and dependencies
- **Gate**: fail if finding CRITICAL vulnerability

#### **Stage 5: Push to ECR (Amazon Elastic Container Registry)**
- Login to ECR via `aws ecr get-login-password`
- Push image: `docker push <ecr-repo>:$BUILD_NUMBER`
- Push latest tag: `docker push <ecr-repo>:latest`

#### **Stage 6: Deploy to AWS**

**Option A: ECS Fargate**
- Execute `aws ecs update-service --force-new-deployment`
- ECS pulls new image and replaces old tasks
- Rolling update: one task at a time (zero downtime)

**Option B: EC2**
- Use AWS Systems Manager (SSM) Run Command to execute commands on EC2
- Commands: `docker pull`, `docker stop`, `docker rm`, `docker run`
- **Why SSM**: no SSH needed, more secure, auditable

#### **Stage 7: Wait for Deployment**
- Execute `aws ecs wait services-stable` (or manual polling for EC2)
- Wait until all health checks pass
- **Timeout**: 5 minutes (if not stable, something is wrong)

#### **Stage 8: Smoke Tests**
- Get ALB URL (or EC2 IP)
- Execute 3 basic tests:
  1. `curl /metrics` → must return 200 + JSON
  2. `POST /files` with test file
  3. `GET /files/{filename}` → verify correct content
- **If fails**: automatic rollback (see section below)

#### **Post-Pipeline Actions**

**On success**:
- Notify Slack/Teams: "✅ Deploy succeeded - Build #123"
- Update Jira/Linear: move ticket to "Deployed"

**On failure**:
- Notify on-call: "❌ Deploy failed - Build #123"
- **Automatic rollback**: revert to previous task definition on ECS
- Create automatic issue in Jira with error logs

---

### Jenkins Configuration

#### Credentials
- Use Jenkins AWS Credentials Plugin to store AWS keys
- **Better**: IAM Role attached to Jenkins EC2 instance (no key storage needed)
- SonarQube token via Jenkins Credentials Store (no hardcode)

#### Agent Requirements
- Docker installed (for image build)
- AWS CLI installed (for ECR push and deploy)
- Gradle installed (or use project's Gradle wrapper)
- Resources: minimum 2 vCPU, 4GB RAM (Gradle build consumes memory)

#### Blue/Green Deployment (Advanced Strategy)

**Why use**:
- **Zero downtime**: always have one environment running
- **Instant rollback**: just switch ALB weight (seconds)
- **Test in prod**: test new version before directing real traffic

**How it works**:
1. **Blue** = current environment (receiving 100% traffic)
2. **Green** = new version (0% traffic)
3. Deploy to Green
4. Smoke tests on Green
5. Switch ALB listener: 0% blue → 100% green (gradually: 10%, 50%, 100%)
6. Monitor CloudWatch for 5-10min
7. If all OK, Green becomes Blue for next deploy

**Rollback**: if Green fails, ALB is still pointing to Blue (instant rollback)

#### GitHub Integration

**Webhook**:
- GitHub → Settings → Webhooks
- URL: `https://jenkins.example.com/github-webhook/`
- Events: Push, Pull Request
- **Trigger**: each push to main automatically triggers pipeline

**Branch Strategy**:
- `main` branch → deploy to production (only if all tests pass)
- `develop` branch → deploy to staging (test environment)
- Pull Requests → run build + test only (no deploy, just validation)

---

### Rollback Strategies

**Scenario**: deploy passed pipeline but has bug detected by users in production

**Option 1: Re-run Previous Build (Jenkins)**
- Via Jenkins UI: click previous build and choose "Rebuild"
- Via API: trigger previous build via REST call
- **Time**: ~5-10min (needs to re-run entire pipeline)

**Option 2: Direct ECS Rollback (AWS CLI)**
- List task definitions to see available versions
- Execute `aws ecs update-service` pointing to previous task definition
- **Time**: ~2min (just changes pointer, no rebuild)
- **When to use**: emergency, need to rollback FAST

**Option 3: Git Revert + Terraform**
- Revert commit in Git: `git revert HEAD`
- Terraform applies change automatically (via pipeline)
- **Time**: ~10min (slower but maintains auditable history)
- **When to use**: infrastructure change that went wrong

---

## 🏗️ Infrastructure as Code (Terraform)

**Why Terraform** (vs CloudFormation):
- **Declarative**: define desired state, Terraform figures out how to get there
- **State management**: tracks created resources, detects drift (manual changes)
- **Multi-cloud**: works with AWS, GCP, Azure (future flexibility)
- **Modules**: code reuse across environments (dev/staging/prod)
- **Plan preview**: shows exactly what will change BEFORE applying (safety)

**Note**: Per PDF requirements ("explanations are enough"), explaining structure and strategy, not writing complete Terraform code.

---

### Terraform Project Structure

**Suggested organization**:
```
terraform/
├── modules/              # Reusable components
│   ├── networking/       # VPC, Subnets, Internet Gateway, NAT
│   ├── ec2/              # EC2 instance, Security Group, EBS volume
│   ├── ecs/              # ECS Cluster, Service, Task Definition, ALB
│   ├── s3/               # S3 bucket (encryption, versioning, lifecycle)
│   └── monitoring/       # CloudWatch Alarms, Dashboards, SNS topics
├── environments/
│   ├── dev/              # Calls modules with dev values
│   ├── staging/
│   └── prod/             # Calls modules with prod values
└── README.md
```

**Why this structure**:
- **Modules**: write VPC once, reuse in dev/prod with different sizes
- **Environments**: separate configurations (dev = t3.micro, prod = t3.small)
- **DRY (Don't Repeat Yourself)**: module change reflects in all environments

---

### State Management (Remote Backend)

**Why remote state** (not local):
- **Collaboration**: entire team accesses same state (not on someone's machine)
- **Locking**: prevents two engineers applying changes simultaneously (via DynamoDB)
- **Automatic backup**: S3 versioning allows recovering previous state if something breaks
- **Security**: state may contain secrets (passwords, keys) - S3 has encryption at rest

**Backend setup**:
1. Create S3 bucket: `kotlin-backend-terraform-state`
2. Enable versioning on bucket
3. Create DynamoDB table: `terraform-lock` (for state locking)
4. Configure Terraform backend to point to this S3

**State structure**:
- `dev/terraform.tfstate` → dev environment state
- `prod/terraform.tfstate` → prod environment state
- Separated to avoid accidentally changing production

---

### Terraform Modules (Conceptual Explanation)

#### **Module: Networking**

**What it does**:
- Create VPC with CIDR block (e.g., 10.0.0.0/16)
- Create public subnets in multiple AZs (us-east-1a, us-east-1b) for high availability
- Create Internet Gateway for internet access
- Configure route tables associating subnets with IGW

**Outputs**:
- `vpc_id`: VPC ID created (used by other modules)
- `public_subnet_ids`: Subnet IDs (to place ALB, EC2, ECS)

**Why separate module**: VPC is used by EC2, ECS, RDS, etc. Create once, reuse.

---

#### **Module: ECS Fargate**

**What it does**:
- Create ECS Cluster
- Define Task Definition (Docker image, CPU, memory, environment variables)
- Create ECS Service (how many tasks to run, health checks, deployment strategy)
- Provision Application Load Balancer (ALB)
  - Target Group pointing to port 8080
  - Health check on `/metrics`
  - HTTP Listener on port 80
- Create Security Groups:
  - ALB: accepts 80/443 from anywhere (0.0.0.0/0)
  - ECS Tasks: accepts 8080 ONLY from ALB (least privilege principle)
- Create IAM Roles:
  - **Execution Role**: ECS uses to pull image from ECR, write logs
  - **Task Role**: application uses to access S3, CloudWatch (granular permissions)

**Inputs**:
- `vpc_id`, `subnet_ids` (from networking module)
- `ecr_repo`, `image_tag` (which Docker image to run)
- `desired_count` (how many tasks, e.g., 2 for HA)

**Outputs**:
- `alb_dns_name`: public URL to access application
- `ecs_cluster_name`, `ecs_service_name`: for use in CI/CD (deployment)

---

#### **Module: S3**

**What it does**:
- Create S3 bucket with unique name (e.g., `kotlin-backend-files-prod`)
- Enable versioning (file history)
- Configure encryption at rest (AES-256 or KMS)
- Lifecycle policy (move old files to Glacier after 90 days)
- Block public access (private bucket, only app accesses via IAM)

---

#### **Module: Monitoring**

**What it does**:
- Create CloudWatch Alarms:
  - Error rate > 5%
  - Target response time > 500ms
  - Unhealthy target count > 0
- Create SNS Topic for notifications (email, Slack, PagerDuty)
- Create CloudWatch Dashboard with main metrics

---

### Environment Composition

**How to use modules** (production environment):

**Environment: `environments/prod/`**
- Calls `networking` module with VPC 10.0.0.0/16, 2 AZs
- Calls `s3` module with bucket `kotlin-backend-files-prod`
- Calls `ecs` module with:
  - VPC/subnets from networking module
  - S3 bucket from s3 module
  - 2 tasks (HA), CPU=512, Memory=1024
  - Image: latest from ECR
- Calls `monitoring` module with ALB/Target Group ARNs

**Prod-specific values** (via `terraform.tfvars`):
- `aws_region = "us-east-1"`
- `desired_count = 2` (HA)
- `instance_type = "t3.small"` (if EC2)

**Environment: `environments/dev/`**
- Same structure, but different values:
  - Smaller VPC (10.1.0.0/16)
  - 1 task only (cost savings)
  - CPU=256, Memory=512 (smaller)
  - No critical alarms (less noise)

---

### Terraform Workflow

**1. Initialize** (first time or after cloning repo):
- `terraform init` downloads providers (AWS) and configures backend (S3)

**2. Plan** (ALWAYS before applying):
- `terraform plan` shows what will change:
  - `+` resources to be created
  - `~` resources to be modified
  - `-` resources to be deleted
- **Review carefully** before applying (avoid accidentally destroying production)

**3. Apply**:
- `terraform apply` creates/modifies resources in AWS
- Asks for confirmation (type "yes")
- Can save plan: `terraform plan -out=tfplan` → `terraform apply tfplan` (skips confirmation)

**4. View outputs** (URLs, IDs):
- `terraform output` shows exported values (e.g., ALB URL, VPC ID)

**5. Update image** (new deploy):
- `terraform apply -var="image_tag=v1.2.3"` updates task definition on ECS

**6. Destroy** (caution, irreversible!):
- `terraform destroy` deletes EVERYTHING (use only in dev/staging)

---

### Terraform Best Practices

**1. Validation before commit**:
- `terraform fmt` (format code)
- `terraform validate` (check syntax)
- Run `plan` in PR (see changes before merge)

**2. Drift Detection**:
- Problem: someone manually changed resource in AWS console
- Solution: `terraform plan` detects drift (shows untracked changes)
- Fix: `terraform apply` forces desired state OR import manual change

**3. Secrets Management**:
- NEVER hardcode passwords/tokens in Terraform code
- Use AWS Secrets Manager or Parameter Store
- Terraform reads secret and injects at runtime

**4. Versioned Modules**:
- Publish modules in separate repo with tags (v1.0.0, v2.0.0)
- Environments use specific versions (prod = v1.0.0, dev = v2.0.0-beta)
- Avoids breaking prod with experimental change

---

### CI/CD Integration (Jenkins)

**Pipeline with Terraform**:
1. Stage "Terraform Plan": run `plan` and save output
2. Stage "Manual Approval": engineer reviews changes
3. Stage "Terraform Apply": apply if approved
4. **Why manual approval**: infra change can be destructive (delete DB, etc.)

**When to run**:
- Push to `main` → plan + apply (if approved)
- Pull Request → plan only (preview changes, don't apply)

---

## 📊 Production Metrics and Why Monitor Them

**Implemented metrics**:
1. **`uploads`**: total files saved
2. **`reads`**: total reads
3. **`errors`**: total errors (404, 500, etc.)

### Why These Metrics Are Important

#### **1. `uploads` - Upload Volume**

**What it measures**: number of files saved in system

**Why monitor**:
- **Capacity Planning**: if uploads grow 20% per month, need to provision more storage
- **Anomaly Detection**: sudden spike can indicate:
  - Marketing campaign (expected)
  - Malicious abuse/bot (investigate)
  - Client bug uploading duplicates
- **Business metrics**: correlates with product adoption

**Suggested alarms**:
- `uploads > 10,000/hour` → alert for high traffic (prepare to scale)
- `uploads = 0 for 10min` → critical (service may be down)

**Real example**: If storage is 100GB and average upload is 500 files/day of 10MB each, in 20 days it will fill up. Alarm should trigger when disk usage > 80%.

---

#### **2. `reads` - Download Volume**

**What it measures**: number of files read from system

**Why monitor**:
- **Performance**: high reads with high latency = IO bottleneck (consider cache/CDN)
- **Hotspot detection**: 80% of reads on 20% of files? Use CloudFront (CDN)
- **Usage patterns**: reads >> uploads = read-heavy workload (optimize for reads)

**Suggested alarms**:
- `reads > 50,000/hour` → alert to consider CDN
- `reads = 0 for 10min` + `uploads > 0` → problem with GET endpoint (critical bug)

**Real example**: If P95 latency of reads > 200ms and `reads > 10k/min`, users will have poor experience. Solution: CloudFront cache (reduces backend reads by 90%).

---

#### **3. `errors` - Error Rate**

**What it measures**: total errors (404 file not found, 500 server error, 400 bad request)

**Why monitor**:
- **System health**: errors > 5% of total traffic = something very wrong
- **UX impact**: error = frustrated user = churn
- **Triage**: separate 404 (normal, user searched for non-existent file) from 500 (bug)

**Important derived metric**:
```
Error Rate (%) = (errors / (uploads + reads)) * 100
```

**Why use %**:
- 1000 errors with 1M requests = 0.1% (ok)
- 1000 errors with 2000 requests = 50% (critical!)
- % normalizes by traffic volume

**Suggested alarms**:
- `error_rate > 5%` → alert (investigate logs)
- `error_rate > 10%` → critical (possible outage, page on-call)
- `500 errors > 10` → critical (code bug)

**Real example**: New deploy → error rate rises from 0.5% to 8% → automatic rollback + investigate commit.

---

### Additional Recommended Metrics (For Real Production)

**If this were real production, I would add**:

**4. Latency (P50, P95, P99)**
- **Why**: 1% of users (P99) may have terrible experience even with low P50
- **SLA**: "99% of requests < 200ms" = monitor P99

**5. Disk Usage** (if using local filesystem)
- **Why**: disk full = service stops working completely
- **Alarm**: > 80% = alert, > 90% = automatic cleanup

**6. Request Rate (RPS - Requests Per Second)**
- **Why**: detect DDoS, traffic spike, or correlate with cost

---

## 🧪 Tests

### Run tests locally
```bash
./gradlew test
```

### Implemented tests
- **Unit Tests**: `FileServiceTest` (filename validation, path traversal)
- **Integration Tests**: `FileApiIntegrationTest` (complete E2E flow)

---

## 🛡️ Security

### Implemented
- ✅ Path traversal protection (filename validation with regex)
- ✅ Non-root container user (Dockerfile)
- ✅ Input validation (400 Bad Request for invalid filename)

### Production (not implemented now, but recommended)
- Rate limiting (e.g., 100 uploads/min per IP) - use AWS WAF on ALB
- Authentication (JWT via API Gateway)
- Encryption in transit (TLS/HTTPS) - certificate on ALB
- Encryption at rest (S3 with KMS)
- Audit logs (CloudTrail to track who accessed what)

---

## 📝 Technical Decisions

### Architecture
- **3 layers**: Controller → Service → Repository
- **Why not full Clean Architecture**: overengineering for 3 simple endpoints
- **Trade-off**: simplicity vs full dependency inversion (pragmatism)

### Thread-Safety
- **AtomicLong** for metrics (concurrency-safe)
- **Discarded alternative**: synchronized blocks (more verbose, less performant)

### Filename Validation
- **Regex**: `^[a-zA-Z0-9._-]+$` (conservative)
- **Why**: prevent path traversal, injections, cross-platform issues
- **Limitation**: doesn't accept Unicode (e.g., "arquivo.txt") - ok for MVP

### Local Storage vs S3
- **Local now**: PDF requirement (simple, no external SDK)
- **S3 in production**: durability 11 nines, versioning, lifecycle policies, infinite scalability

---

## 🚨 Troubleshooting

### Container won't start
```bash
docker logs <container-id>
# Check if port 8080 is already in use
lsof -i :8080
```

### File not saved
```bash
# Check /storage permissions
docker exec <container-id> ls -la /app/storage
# Should belong to user appuser
```

### Metrics reset after restart
**Expected**: metrics are in-memory (don't persist between restarts)

**Production**: send to CloudWatch periodically (every 1min) before graceful shutdown

---

## 📚 Technologies Used

- **Kotlin 1.9.20** + **Spring Boot 3.2.0**
- **Gradle 8.5** (Groovy)
- **Docker** + **Docker Compose**
- **JUnit 5** + **Mockito** (testing)

---

## 👤 Author

Developed as a technical challenge to demonstrate:
- Kotlin + Spring Boot (backend)
- Clean code and pragmatic architecture
- AWS mindset (EC2, ECS, S3, CloudWatch)
- DevOps (Docker, CI/CD, Terraform)
- Observability (metrics, logs, alarms)
- Seniority (justified technical decisions, conscious trade-offs)
