# meeting-vote

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você
deve criar uma solução para dispositivos móveis para gerenciar e participar dessas sessões de votação.

## Objetivos:

Objetivos do projeto podem ser lidos no projeto [Votação em Assembléia](./desafio.md)

## Mapeamento dos Endpoints requisitados 

  Foi realizado o mapeamento dos endpoints que deveriam ser implementados no documento [Análise de Endpoints](./analise-endpoints.md)

## Recursos e Tecnologias Utilizadas

Projeto desenvolvido em Java 21 utilizando Spring Boot, na versão v1 foi usado SpringMVC com JPA e na versão v2 Webflux utilizando Cache e conexão JdbcTemplate.
Jdbc foi utilizado pois o Hibernate produz vários inserts, e uma das formas mais eficientes de evitar falhas por concorrência é a persistencia em Batch. 
O banco de dados utilizado é o PostgreSQL e testes unitários utilizando JUnit e Mockito. 
A aplicação é configurada para ser executada em ambiente local utilizando Docker Compose para o banco de dados, e pode ser facilmente adaptada para execução em nuvem.
O serviço de Validação de CPF foi integrado utilizando a API https://api.cpfhub.io/cpf/{cpf}, mais detalhes no link [Validação CPF](#validação-cpf)

## Validação CPF
Como não consegui utilizar o heroku, utilizei a API https://api.cpfhub.io/cpf/{cpf}, porém para ser utilizada é necessário cadastro no site e gerar um token, para testar o serviço é necessário setar a variável de ambiente API_KEY com o token gerado no site.
Nesse caso a validação ficou um pouco divergente ao solicitado, mas o serviço está preparado para resolver a validação utilizando a API do heroku, caso seja necessário.

## Swagger
Para uma documentação mais detalhada dos endpoints, incluindo exemplos de requisições e respostas, a aplicação conta com uma interface Swagger, que pode ser acessada através do link:
    
http://localhost:8080/swagger-ui/index.html

## Roteiro de implementação
De forma a organizar a implementação, foi criado um roteiro, onde as tarefas foram organizadas de forma a garantir uma evolução consistente do projeto, e facilitar a validação dos requisitos solicitados. O roteiro completo pode ser encontrado no link [Roteiro de Implementação](./roteiro-implementacao.md)

## Como Executar o Projeto Localmente

#### Subir banco local:

```cmd
$ docker compose up -d
```

#### Exemplo de executar testes para AgendaControllerTest

```cmd
./gradlew :infrastructure:test --tests "com.coop.voting.infrastructure.controller.v1.AgendaControllerTest"
```

#### Rodar aplicação localmente:

```cmd
$ ./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### Exemplo de chamada para criar uma nova pauta

```cmd
curl -X POST http://localhost:8080/api/v1/agendas \
-H "Content-Type: application/json" \
-d '{"title": "Aprovação do Balanço Financeiro 2025"}'
```

#### Exemplo de resposta para criação de nova pauta

```json
{
  "id": 1,
  "title": "Aprovação do Balanço Financeiro 2025"
}
```
```json
{
  "id": 2,
  "title": "Aprovação rateio central"
}
```

#### Exemplo de chamada para abrir nova seção de votação

```cmd
curl -X POST http://localhost:8080/api/v1/session \
-H "Content-Type: application/json" \
-d '{
  "title": "Assembleia Geral de Investimentos",
  "durationInMinutes": 5,
  "agendas": [
      {
        "id": 1,
        "title": "Aprovação do Balanço Financeiro 2025"
      },
      {
        "id": 2,
        "title": "Aprovação rateio central"
      }
  ]
}'
```

#### Exemplo de resposta para abrir nova seção de votação

```json
{
  "id": 1,
  "title": "Assembleia Geral de Investimentos",
  "openingTime": "2024-06-30T14:00:00Z",
  "closingTime": "2024-06-30T14:05:00Z"
}
```


#### Exemplo de chamada para buscar pautas por seção

```cmd
  curl -X GET http://localhost:8080/api/v1/session/1/agendas
```

#### Exemplo de resposta para buscar pautas por seção

```json
{
  "agendas": [
    {
      "id": 1,
      "title": "Aprovação do Balanço Financeiro 2025"
    },
    {
      "id": 2,
      "title": "Aprovação rateio central"
    }
  ]
}
```

#### Exemplo de chamada para votar em uma pauta

```cmd
curl -X POST http://localhost:8080/api/v1/vote/1 \
-H "Content-Type: application/json" \
-d '{
  "agendaId": 1,
  "choice": "SIM",
  "cpf": "19839091069"
}'
```

#### Exemplo de resposta para votar em uma pauta

```json
{
  "message": "Voto registrado com sucesso"
}
```

#### Exemplo de chamada para contabilizar votos e resultado da votação

```cmd
curl -X GET http://localhost:8080/api/v1/vote/1/results
```

#### Exemplo de resposta para contabilizar votos e resultado da votação

```json
{
  "agendas": [
    {
      "id": 1,
      "title": "Aprovação do Balanço Financeiro 2025",
      "quantityVotes": 10,
      "percentageVotes": 25
    },
    {
      "id": 2,
      "title": "Aprovação do Balanço Financeiro 2026",
      "quantityVotes": 20,
      "percentageVotes": 75
    }
  ]
}
```

