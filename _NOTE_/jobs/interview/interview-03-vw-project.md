# INTRODUCTION

##### ENGLISH ######
****************************************************************************************************
*** A distributed, event-driven platform using AWS, Node.js, and Java, each where it made sense. ***
****************************************************************************************************

*** Project example – Volkswagen (Fleet Master) ***

At Volkswagen, I worked on a project called Fleet Master, a platform designed to help fleet owners manage electric trucks and buses.

The system collects telemetry data from sensors installed in the vehicles, such as battery health and energy consumption, and alerts fleet managers when something requires attention.

One of the main challenges was that most of the sensors were not owned by Volkswagen, but by external partners like Omnlink and Itura. 

These partners did not send data in real time, so we had to actively pull data from their APIs.

*** Arquitetura e AWS ***

To solve this, we designed an event-driven architecture using AWS.

We created Lambda functions scheduled every five minutes using EventBridge to pull data from each partner’s API.

The raw data was first stored in DynamoDB as a buffer layer, following a data engineering approach with SOR tables for raw data and SOT as the source of truth.

After storing the raw data, the Lambda published messages to SQS, which triggered another service responsible for normalizing the data and persisting it in the final relational database, running on RDS Postgres.

###### ###### ###### ###### ###### ###### ###### ###### ######
*** Para Node ***

The ingestion and event-driven parts were primarily built with Node.js and NestJS on Lambda.

*** Para Java *** 

The core business services and heavy processing were implemented in Java with Spring Boot running on containers.

*** Para Cloud / Platform *** 

The architecture was designed around AWS and event-driven patterns, using different stacks depending on the workload characteristics.
###### ###### ###### ###### ###### ###### ###### ###### ######

*** Stack e decisões técnicas ***

Both the pulling Lambdas and the normalization services were implemented using Node.js with NestJS.

We chose NestJS because most of the backend team had a Java background, and its architecture and dependency injection model are very similar to Spring Boot, which helped with maintainability and onboarding.

For some partners, instead of pulling data, we exposed webhook endpoints using API Gateway to trigger the normalization flow.

*** Quando NÃO usar Lambda (ponto forte) ***

For heavier business logic, such as aggregating large volumes of data, generating reports, feeding dashboards, and calculating efficiency comparisons between diesel and electric vehicles, we used Spring Boot services running on containers.

These workloads were more CPU-intensive and long-running, which made Lambda a poor fit. This separation helped us use serverless where it made sense and containers where more control was required.

*** Escala e comunicação ***

Volkswagen operates multiple platforms, usually one or two per continent. 

Applications inside the same platform communicated asynchronously via SQS. Between platforms, we used Kafka to enable cross-region and cross-platform event distribution.

*** Frontend (fecha bem) ***

The frontend was built from scratch using React.

I introduced Atomic Design principles to build a reusable design system, not only for the project itself, but at a company level, which made building new screens much faster and more consistent.