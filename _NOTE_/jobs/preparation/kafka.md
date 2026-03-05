# 1) Conceitos e arquitetura

***Pergunta***: O que é Kafka e por que usar?
***Resposta sênior (objetiva)***: Kafka é uma plataforma distribuída de streaming e log de eventos. Eu uso quando preciso de desacoplamento entre produtores e consumidores, alta taxa de throughput, retenção de eventos e reprocessamento. Não é só “fila”: é log particionado com consumidores lendo por offset.

***Pergunta***: Topic, partition, offset e consumer group.
***Resposta***: Topic é o “canal” lógico. Partitions são shards ordenados dentro do topic. Offset é a posição do evento na partition. Consumer group permite escalar consumo: cada partition é consumida por no máximo 1 consumer dentro do mesmo grupo, garantindo paralelismo sem duplicar trabalho.

***Pergunta***: Ordenação é garantida?
***Resposta***: Só dentro da mesma partition. Se eu preciso de ordenação por chave (ex.: idPedido), eu uso keying consistente para garantir que a mesma chave caia sempre na mesma partition.

***Pergunta***: Qual o papel dos brokers e do controller/metadata?
***Resposta***: Brokers armazenam e servem logs. O cluster mantém metadata e liderança de partitions (hoje com KRaft em clusters novos, antes era Zookeeper). Para entrevista: o importante é entender liderança/replicação, ISR e como isso impacta disponibilidade.

# 2) Garantias de entrega e duplicidade

***Pergunta***: At-least-once, at-most-once, exactly-once.
***Resposta***:

<At-most-once>: pode perder mensagem (commit antes de processar ou auto-commit mal configurado).

<At-least-once>: não perde, mas pode duplicar (processa e falha antes do commit). É o padrão mais comum.

<Exactly-once>: reduz duplicidade no pipeline Kafka (idempotent producer + transactions, e do lado consumidor com Kafka Streams/transactional consume-produce). Ainda assim, em integrações com banco/serviços externos normalmente eu trato como “efetivamente uma vez” usando idempotência.

***Pergunta***: Como você lida com duplicidade?
***Resposta sênior***: Eu assumo duplicidade possível e faço o processamento idempotente: chave de idempotência (eventId), tabela de dedup/unique constraint, ou upsert. E controlo commit manual: só commit depois do processamento.

# 3) Producer: acks, idempotência, retries

***Pergunta***: O que significa acks=0/1/all?
***Resposta***:

***acks***=0: producer não espera confirmação, maior risco de perda.

***acks***=1: espera líder gravar.

***acks***=all: espera ISR (replicação mínima), mais durável. Em produção crítica, normalmente acks=all e min.insync.replicas configurado.

***Pergunta***: Como evitar mensagens duplicadas no producer?
***Resposta***: enable.idempotence=true (idempotent producer) e retries configurados. Para exactly-once em pipelines, uso transactions (transactional.id) quando aplicável.

***Pergunta***: Key vs sem key?
***Resposta***: Com key eu controlo roteamento para partition e preservo ordenação por chave. Sem key, distribui round-robin e eu ganho throughput mas perco ordenação por entidade.

# 4) Consumer: commit, rebalance, lag

***Pergunta***: Como funciona commit de offset?
***Resposta***: Offset é “ponteiro” do consumer group. Eu prefiro commit manual/síncrono após processar para garantir at-least-once. Auto-commit é ok só para casos simples e sem efeito colateral crítico.

***Pergunta***: Rebalance e impactos?
***Resposta sênior***: Rebalance acontece quando muda membership do grupo ou partitions. Pode pausar consumo e causar duplicidade se offsets não foram commitados. Eu mitigo com session/heartbeat adequados, processamento rápido por poll, e coop rebalancing quando possível, além de reduzir trabalho dentro do loop (ou usar async + backpressure com cuidado).

***Pergunta***: Consumer lag: o que é e como resolve?
***Resposta***: Lag é a diferença entre o último offset produzido e o offset consumido. Resolvo escalando consumidores (aumentando partitions ou consumers no grupo), otimizando processamento, ajustando batch/poll, e garantindo que downstream não seja gargalo. Sempre monitoro lag, taxa de consumo e tempo de processamento.

# 5) Particionamento, throughput e sizing

***Pergunta***: Como você escolhe número de partitions?
***Resposta sênior***: Eu escolho baseado no paralelismo necessário e taxa de eventos, lembrando que partitions são a unidade de paralelismo por consumer group. Evito partitions demais (overhead de metadata, file handles, rebalance lento). Penso no crescimento e em como farei expansão.

***Pergunta***: Impacto de aumentar partitions depois?
***Resposta***: Aumentar partitions muda distribuição de chaves (hash), então ordenação por chave pode “quebrar” para alguns cenários e pode complicar reprocessamento. Eu planejo keying e partitions pensando em evolução.

# 6) Retenção, compactação e reprocessamento

***Pergunta***: Retention e compacted topics.
***Resposta***: Retention por tempo/tamanho mantém histórico para replay. Compaction mantém “último valor por key”, bom para estado (ex.: profile atual). Eu escolho conforme necessidade de replay e modelo de leitura.

***Pergunta***: Como reprocessar eventos?
***Resposta***: Eu posso resetar offsets do consumer group (ou usar outro group id), ou ler desde um timestamp. Precisa governança: idempotência e cuidado com efeitos colaterais.

# 7) Erros, DLQ e observabilidade

***Pergunta***: O que você faz quando dá erro no processamento?
***Resposta sênior***: Eu separo erros recuperáveis (retry com backoff) de não recuperáveis (manda para DLQ com contexto). Não deixo a mesma mensagem travar a partition para sempre. Registro métricas (taxa de erro, lag, retries, DLQ), trace/correlation id e logs estruturados.

***Pergunta***: DLQ vs retry topic?
***Resposta***: Retry topic (com delay/backoff) para transitórios. DLQ para mensagens “envenenadas” ou violações de contrato. Em ambos, guardo payload + headers + motivo + stack trace/código de erro.

# 8) Garantias com banco de dados e side effects

***Pergunta***: Como garantir consistência entre DB e Kafka?
***Resposta sênior***: Sem magia: eu uso padrões. Para publicar eventos a partir de transação do DB, uso Outbox pattern (grava evento na mesma transação e um relay publica no Kafka). Para consumir e aplicar no DB, uso idempotência e, se for necessário encadear consume-produce, considero transações Kafka/Streams, mas para DB externo sigo com outbox/dedup.

# 9) Schema, evolução e compatibilidade

***Pergunta***: Como lidar com evolução de mensagens?
***Resposta***: Eu uso schema registry (Avro/Protobuf/JSON Schema) e aplico regras de compatibilidade (backward/forward). Nunca quebro consumidores: adiciono campos opcionais, evito renomear/remover sem versionamento. Também valido contrato e tenho testes de compatibilidade.

# 10) Segurança

***Pergunta***: Como proteger Kafka?
***Resposta***: TLS em trânsito, SASL (SCRAM/OAUTH) para autenticação, ACLs para autorização por topic/group. Em ambientes regulados, também criptografia em repouso e segregação de clusters/tenants.