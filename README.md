# GasStation - Mapa de Diretórios (DDD)

Estrutura sugerida para evoluir o projeto com DDD, separando **domínio**, **aplicação** e **infraestrutura**:

```text
src/                                         # raiz do código-fonte
├── main/                                    # código de produção
│   ├── java/com/br/manager/                 # pacote base da aplicação
│   │   ├── domain/                          # regras de negócio puras (sem Spring/JPA/API)
│   │   │   ├── stock/                       # bounded context de estoque
│   │   │   │   ├── model/                   # modelo de domínio do contexto
│   │   │   │   │   ├── aggregate/           # raízes de agregado e consistência transacional
│   │   │   │   │   ├── entity/              # entidades de domínio
│   │   │   │   │   ├── valueobject/         # objetos de valor imutáveis
│   │   │   │   │   └── enum/                # enumerações de negócio
│   │   │   │   ├── repository/              # interfaces/ports do domínio (contratos)
│   │   │   │   ├── service/                 # domain services (regras entre agregados)
│   │   │   │   └── exception/               # exceções de negócio do contexto
│   │   │   ├── shift/                       # bounded context de turnos/abastecimento
│   │   │   │   ├── model/                   # modelo de domínio do contexto
│   │   │   │   ├── ****repository/              # interfaces/ports do domínio (contratos)
│   │   │   │   ├── service/                 # domain services do contexto
│   │   │   │   └── exception/               # exceções de negócio do contexto
│   │   │   └── self/                        # bounded context da empresa/configuração
│   │   │       ├── model/                   # modelo de domínio do contexto
│   │   │       ├── repository/              # interfaces/ports do domínio (contratos)
│   │   │       ├── service/                 # domain services do contexto
│   │   │       └── exception/               # exceções de negócio do contexto
│   │   │
│   │   ├── application/                     # casos de uso (orquestra domínio + portas)
│   │   │   ├── stock/                       # casos de uso do contexto stock
│   │   │   │   ├── usecase/                 # serviços de aplicação (create/update/find/delete)
│   │   │   │   ├── dto/                     # DTOs de entrada/saída dos casos de uso
│   │   │   │   └── mapper/                  # mapeamento entre DTO <-> domínio
│   │   │   ├── shift/                       # casos de uso do contexto shift
│   │   │   │   ├── usecase/                 # serviços de aplicação do contexto
│   │   │   │   ├── dto/                     # DTOs do contexto
│   │   │   │   └── mapper/                  # mapeamentos do contexto
│   │   │   └── self/                        # casos de uso do contexto self
│   │   │       ├── usecase/                 # serviços de aplicação do contexto
│   │   │       ├── dto/                     # DTOs do contexto
│   │   │       └── mapper/                  # mapeamentos do contexto
│   │   │
│   │   ├── infra/                           # implementações concretas e detalhes técnicos
│   │   │   ├── api/                         # camada HTTP (entrada externa)
│   │   │   │   ├── controller/              # endpoints REST
│   │   │   │   ├── exception/               # tratamento de erro da API
│   │   │   │   └── security/                # CORS, filtros e segurança HTTP
│   │   │   ├── persistence/                 # acesso a dados e banco********
│   │   │   │   ├── jpa/                     # tecnologia de persistência escolhida (JPA)
│   │   │   │   │   ├── entity/              # entidades JPA (modelo de persistência)
│   │   │   │   │   ├── repository/          # interfaces Spring Data (concretas da infra)
│   │   │   │   │   └── adapter/             # implementação dos ports do domain.repository
│   │   │   │   └── mapper/                  # mapeamento persistência <-> domínio
│   │   │   ├── multitenant/                 # resolução de tenant e datasource dinâmico
│   │   │   └── flyway/                      # bootstrap/configuração de migrações
│   │   │
│   │   ├── config/                          # beans e configuração técnica global
│   │   └── common/                          # código compartilhado transversal
│   │
│   └── resources/                           # arquivos de configuração e SQL
│       ├── db/migration/                    # scripts versionados do Flyway
│       ├── application.properties           # propriedades da aplicação
│       └── log4j2.xml                       # configuração de logs
│
└── test/                                    # testes automatizados
    └── java/com/br/manager/
        ├── application/                     # testes de casos de uso
        ├── domain/                          # testes de regras de negócio puras
        └── infra/                           # testes de API/persistência/integração
```

## Regras práticas

1. `domain` não depende de Spring, JPA, API ou MapStruct.
2. `application` orquestra casos de uso e conversa com portas do domínio.
3. `infra` implementa detalhes técnicos (HTTP, banco, multitenancy, migrações).
4. DTO de entrada/saída de API fica fora de `domain`.
5. Repositório do domínio é interface; implementação concreta fica em `infra/persistence`.
