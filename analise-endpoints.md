# Análise de Recursos e Endpoints

Realizando a análise dos recursos e endpoints necessários para atender aos requisitos do desafio, temos:


## Cadastrar nova pauta

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

## Abrir nova seção de votação

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

## Buscar pautas por seção

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

## Votar em uma pauta

- HTTP Method: POST
- Path: /api/v1/agendas/{id}/votes
- Request Body: Enviado dinamicamente pelo clique do item na tela do aplicativo.

```json
{
  "agendaId": 1,
  "choice": "SIM",
  "cpf": "19839091069"
}
```

## Contabilizar votos e resultado da votação

- HTTP Method: GET
- Path: /api/v1/vote/{id}/results
- Response Body:

```json
{
  "agendas": [
    {
      "id": 1,
      "title": "Pauta 1",
      "totalVotes": 10,
      "yesVotes": 5,
      "noVotes": 5
    },
    {
      "id": 2,
      "name": "Pauta 2",
      "totalVotes": 20,
      "yesVotes": 10,
      "noVotes": 10
    }
  ]
}
```