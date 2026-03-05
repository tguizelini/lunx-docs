# Etapas

Digamos que seja para atuar implementando novas features em projetos existentes no dia-a-dia e/ou corrigir bugs. atividades essas cujo os passos sao:

1. entender o padrão do projeto a nivel de codigo e arquitetura

2. entender a tarefa ou bug descrito na tarefa do JIRA

3. unir essa compreensao do Projeto (item 1) x Descricao da tarefa (item 2), criar um doc de explicando o que deve ser feito e recomendar um plano de solução

4. após minha revisão da solução, se aprovada, implementa-lo no codigo / codifica-lo.

### O workflow ideal (prático) para o seu dia a dia
*** Passo A: *** “Project Snapshot” (1x por repo ou quando muda muito)

Use Claude e gere um resumo padrão do projeto:
- arquitetura e fluxo
- convenções
- lugares mais comuns para mudanças
- como rodar/testar

***Guarde isso (Notion/README interno). Toda task fica mais rápida.***

*** Passo B ***: Para cada JIRA

- GPT: transformar JIRA em requisitos + dúvidas + critérios de aceite
- Claude: gerar 1-pager + plano de implementação (com base no snapshot)

***Você revisa***
- Copilot: codar no IDE
- Claude: review final + checklist
  
### Checklist rápido por etapa (bem objetivo)
***Etapa 1 (Claude)***

- Identificou entry points?
- Identificou camadas e dependências?
- Identificou padrão de erro/log/test?

***Etapa 2 (GPT)***

- Requisitos claros?
- Critérios de aceite?
- Edge cases?
- Dúvidas listadas?

***Etapa 3 (Claude)***

- Solução compatível com padrão do projeto?
- Plano incremental (commits pequenos)?
- Testes e rollout?
- Riscos e rollback?

***Etapa 4 (Copilot + Codex)***

- Implementação e testes cobrindo cenários do ticket?
- Sem quebrar contratos?
- Logs/metrics ok?
- PR description alinhada ao doc?

### Prompt pronto (curto) pra você copiar e usar

1) GPT (JIRA -> requisitos)

“Vou colar um ticket do JIRA. Reescreva como requisitos claros, liste ambiguidades e perguntas para PO/QA, derive critérios de aceite e sugira casos de teste. Seja objetivo.”

2) Claude (snapshot + ticket -> doc/plano)

“Vou colar um resumo do projeto + um ticket clarificado. Gere um doc curto com proposta de solução e plano passo-a-passo, indicando quais arquivos/camadas mudar, impactos, testes e rollout/rollback.”

3) Claude (review pós-PR)

“Revise este diff/arquivos alterados. Ache bugs, violações de padrão do projeto, edge cases não cobertos, e sugira melhorias. Produza um checklist de itens para corrigir.”

```
    JIRA ambíguo → ChatGPT
    Arquitetura + Plano → Claude
    Implementação manual → Copilot
    Implementação multi-arquivo → GPT-Code
    Review final → Claude
```