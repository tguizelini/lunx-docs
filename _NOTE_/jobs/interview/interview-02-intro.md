# INTRODUCTION

I’m a Senior Software Engineer with over 13 years of experience building and scaling cloud-native applications. 

My main focus is backend and distributed systems, but I also have solid hands-on experience with React on the frontend.

On the backend side, I've worked extensively with Java and Kotlin on Spring Boot, as well as Node.js with Express and NestJS, designing microservices, event-driven systems, and serveless solutions on AWS.

I've been involved in projects where scalability, reliability, and observability were critical, using services like Lambda, SQS, EventBridge, API Gateway, RDS and DynamoDB.

I also have experience supporting CI/CD pipelines and infrastructure provisioning using tools like Gihub Actions, Jenkins, Gitlab Action, CircleCI and Terraform.

Overall, I enjoy working close to architecture and solving complex backend problems, while still being comfortable contributing to the frontend when needed.

##### How do you usually use AWS Lambda? #####

I usually use AWS Lambda for stateless business logic in event-driven or request-based workflows.

Most commonly, it’s triggered by API Gateway for HTTP APIs, SQS for asynchronous processing, or EventBridge for scheduled jobs and integrations.

I like Lambda when I need fast scalability, low operational overhead, and clear separation of responsibilities between services.

... It works really well for background processing, integrations, data transformations, and lightweight APIs.

##### When would you avoid serverless? #####

I usually avoid serverless when the workload is long-running, stateful, or requires tight control over execution time and resources.

For example, heavy batch processing, complex streaming consumers, or workloads that would hit Lambda timeouts or cold start constraints are usually better suited for containers.

... In those cases, I prefer ECS or Kubernetes, depending on the team and the operational model.

##### What kind of systems have you designed? #####

I’ve designed and worked on distributed systems focused on scalability and resilience, such as microservices and event-driven architectures.

Examples include platforms that ingest and process telemetry or business events, backend systems consumed by web applications, and serverless workflows integrating multiple AWS services.

... In these systems, I usually focus on clear service boundaries, asynchronous communication, and observability, using queues, events, metrics, and logs to keep the system reliable and maintainable.

# PROJETO VOLKSWAGEN

*** NIVEL 1 ***

I’ve designed distributed, event-driven systems focused on scalability and resilience.

One example is a platform I worked on at Volkswagen to ingest and process telemetry data from electric vehicles. The system had to integrate with multiple external partners that didn’t provide real-time data.

We designed an event-driven architecture on AWS, using scheduled Lambdas to pull data, queues to decouple processing, and backend services to handle normalization and persistence.

The architecture mixed serverless and container-based services, using each where it made the most sense.

*** NIVEL 1 ***

For ingestion and lightweight processing, we used AWS Lambda with Node.js and NestJS.

For heavier business logic and long-running workloads, we used Spring Boot services running on containers.

..........

5️⃣ Seu ponto MAIS forte (use conscientemente)

Essa parte aqui é ouro 👇
Use quando perguntarem sobre decisões técnicas:

“We intentionally avoided Lambda for heavy, long-running workloads and used containers instead.
This helped us balance scalability with performance and operational control.”

Isso mostra:

maturidade

bom senso

experiência real (não tutorial)

6️⃣ Como FECHAR a resposta (muito importante)

Sempre feche assim:

“That project is a good example of the kind of systems I usually work on: distributed, event-driven, and designed to scale, using different technologies depending on the workload.”

Ou então:

“I can go deeper into any part of the architecture if you’d like.”

Isso devolve o controle para o entrevistador.