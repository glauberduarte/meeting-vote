# meeting-vote

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você
deve criar uma solução para dispositivos móveis para gerenciar e participar dessas sessões de votação.

## Objetivos:

Essa solução visa ser executada em nuvem e promover as principais funcionalidades descritas abaixo, através de
uma API REST:

- Cadastrar uma nova pauta
- Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por um tempo determinado na chamada de
  abertura ou 1 minuto por default)
- Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado é identificado por um id único
  e pode votar apenas uma vez por pauta)
- Contabilizar os votos e dar o resultado da votação na pauta

As pautas e os votos sejam persistidos e que não sejam perdidos com o restart da aplicação.

Para fins de exercício, a segurança das interfaces pode ser abstraída e qualquer chamada para as interfaces pode ser
considerada como autorizada. A solução está construída em java, usando Spring-boot.

O foco desse serviço é exercitar a comunicação entre o backend e o aplicativo mobile. Essa comunicação é feita através
de mensagens no formato JSON, onde essas mensagens serão interpretadas pelo cliente para montar as telas onde o usuário
vai interagir com o sistema. A aplicação cliente não faz parte da avaliação, apenas os componentes do servidor.

## Tarefas a considerar

### Tarefa Bônus 1 - Integração com sistemas externos

- Integrar com um sistema que verifique, a partir do CPF do associado, se ele pode votar
- GET https://user-info.herokuapp.com/users/{cpf}
  Classificação da informação: Uso Interno- Caso o CPF seja inválido, a API retornará o HTTP Status 404 (Not found).
  Você pode usar geradores de CPF para gerar CPFs válidos
- Caso o CPF seja válido, a API retornará se o usuário pode (ABLE_TO_VOTE) ou não pode (UNABLE_TO_VOTE) executar a
  operação. Essa operação retorna resultados aleatórios, portanto um mesmo CPF pode funcionar em um teste e não
  funcionar no outro.

### Tarefa Bônus 2 - Performance

- Imagine que sua aplicação possa ser usada em cenários que existam centenas de milhares de votos. Ela deve se comportar
  de maneira performática nesses cenários
- Testes de performance são uma boa maneira de garantir e observar como sua aplicação se comporta

### Tarefa Bônus 3 - Versionamento da API

- Como você versionaria a API da sua aplicação? Que estratégia usar?

## Utilização

### Cadastrar nova pauta

- HTTP Method: POST
- Path: /api/v1/agendas
- Request Body: {"title": "Pauta 1"}
- Response Body:

```json
{
  "id": 1,
  "title": "Pauta 1"
}
```

### Abrir nova seção de votação

- HTTP Method: POST
- Path: /api/v1/agendas/{id}/sessions
- Request Body: {"durationInMinutes": 5} (se nulo, assume 1 minuto)

```json
{
  "title": "Assembleia Geral de Investimentos",
  "durationInMinutes": 5,
  "agendas": [
    {
      "id": 1,
      "title": "Pauta 1"
    },
    {
      "id": 2,
      "title": "Pauta 2"
    }
  ]
}
```

Validar alteração para suportar a estrutura

```json
{
  "campo1": "valor1",
  "campo2": 123,
  "idCampoTexto": "Texto",
  "idCampoNumerico": 999,
  "idCampoData": "01/01/2000"
}
```

### Buscar pautas por seção

- HTTP Method: GET
- Path: /api/v1/agendas/{id}/session

```json
{
  "agendas": [
    {
      "id": 1,
      "title": "Pauta 1"
    },
    {
      "id": 2,
      "title": "Pauta 2"
    }
  ]
}
```

### Votar em uma pauta

- HTTP Method: POST
- Path: /api/v1/agendas/{id}/votes
- Request Body: Enviado dinamicamente pelo clique do item na tela do aplicativo.

```json
{
  "voteChoice": "SIM",
  "cpf": "19839091069"
}
```

### Contabilizar votos e resultado da votação

- HTTP Method: GET
- Path: /api/v1/agendas/{id}/sessions
- Response Body:

```json
{
  "assemblyIsFinished": true,
  "agendas": [
    {
      "id": 1,
      "title": "Pauta 1",
      "quantityVotes": 10,
      "percentageVotes": 25
    },
    {
      "id": 2,
      "name": "Pauta 2",
      "quantityVotes": 20,
      "percentageVotes": 75
    }
  ],
  "agendaSelected": {
    "id": 2
  }
}
```

## Swagger

http://localhost:8080/swagger-ui/index.html

## Execução Local

#### 1. Subir banco local:

```cmd
$ docker compose up -d
```

#### 2. Rodar aplicação localmente:

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

#### Exemplo de executar testes para AgendaControllerTest

```cmd
./gradlew :infrastructure:test --tests "com.coop.voting.infrastructure.controller.v1.AgendaControllerTest"
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
        "title": "Pauta 1"
      },
      {
        "id": 2,
        "title": "Pauta 2"
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
      "title": "Aprovação do Balanço Financeiro 2025"
    }
  ]
}
```

#### Exemplo de chamada para votar em uma pauta

```cmd
curl -X POST http://localhost:8080/api/v1/agendas/1/votes \
-H "Content-Type: application/json" \
-d '{
  "voteChoice": "SIM",
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
curl -X GET http://localhost:8080/api/v1/session/1/agendas
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

### Ordem de implementação

- Criação do Repositorio
- Configuração da infraestrutura básica
- Pautas
    - Criação de objetos de Dominio, persistencia e testes de Banco de dados
    - criação de pautas
    - ajustes swagger
    - documentar estrategia de versionamento
- Seção
    - Criação de objetos de Dominio, persistencia e testes de Banco de dados
    - criar nova seção associando pautas
    - buscar pautas por seção
    - ajustes swagger
    - documentar estrategia de versionamento
- Votação
    - votação de pautas
    - Contabilizar resultados
    - Criação de objetos de Dominio, persistencia e testes de Banco de dados
    - ajustes swagger
    - documentar estrategia de versionamento
- Integração com sistemas externos de validação de CPF
  - teste: https://www.cpfhub.io/blog/melhores-apis-gratuitas-consulta-cpf-desenvolvedores
  - prod: https://user-info.herokuapp.com/users/{cpf}
- Tratar performance (cache local) e monitoramento
- Revisão documentação e testes unitários e documentar testes de homologação

