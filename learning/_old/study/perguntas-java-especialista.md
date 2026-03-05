_____ Como você utilizou Docker e Kubernetes para orquestrar e gerenciar containers em projetos anteriores?Esta pergunta é obrigatória*

Docker é praticamente um default a ser utilizado para não termos o famoso "na minha máquina funciona" e garantir que a aplicação rode em qualquer ambiente.

Orquestração de containers com Kubernates, não tive experiencia/oportunidades que considero relevante.

Mas entendo o conceito, a importancia e estou aberto a me aprofundar sobre o assunto.

_____ Descreva sua experiência no desenvolvimento de aplicações em um ambiente AWS. Quais serviços da AWS você utilizou com mais frequência e por quê?Esta pergunta é obrigatória*

Já utilizei varios serviços AWS: S3, EC2, Dynamo, RDS, Fargate com ECS, SQS, SNS, etc.

O que é mais comum de utilizar é:

- Fargate com ECS ou EC2 para publicar os services, utilizando Docker para garantir o mesmo ambiente 

- S3 como Storage para subir arquivos ou publicar frontends

- e Dynamo como banco de dados noSQL e/ou algum banco relacional com RDS.

_____ Descreva brevemente projetos recentes em que atuou na criação de Web APIEsta pergunta é obrigatória

São vários. 

O projeto atual faz a comunicação com o frontend fornecendo endpoints de diversos tipos/verbos para comunicação. 

De acordo com a solicitação e/ou ação, as informações são oriundas de diversas fontes:

- banco de dados diretamente utilizando SpringData

- de forma assincrona, quando trata-se de um processo onde não precisamos dar a resposta para o usuário/client/front na mesma hora, comunicando services via messageria (RabbitMQ) 

- buscando informações em APIs de parceiros, via REST, via Clients (FeignClient) 

- mesmo escutando/consumindo dados/eventos que são de interesse de várias aplicações, de forma assincrona, utilizando Kafka.