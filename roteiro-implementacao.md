## Roteiro de Implementação

O roteiro de implementação serve de guia para auxiliar a organizar as tarefas de forma a garantir uma evolução consistente do projeto, e facilitar a validação dos requisitos solicitados.

## Ordem de implementação

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
- Tratar performance (cache local & webflux)
- Revisão documentação, testes unitários, padronização de mensagens de erro, e documentar testes de homologação

## Proposta implementada de evolução do projeto - Performance

Com a ideia de sermos capaz de lidarmos com centenas de milhares de votos, optamos por seguir três estratégias:
- Utilização do webflux de forma a termos respostas não bloqueantes.
- Utilização de cache local para armazenar os votos e estado da assembleia e evitar consultas frequentes ao banco de dados
- Utilização de batch em banco de dados com JdbcTemplate e ON CONFLICT para inserção de votos, evitando bloqueios e lentidão em cenários de alta concorrência.
- Criar uma versão V2 da persistencia de votos para atender os recursos técnicos