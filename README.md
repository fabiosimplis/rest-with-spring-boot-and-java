# Projeto para estudo Spring Boot RestFull
[![NPM](https://img.shields.io/npm/l/react)](https://github.com/fabiosimplis/DEV-dscommerce-back-end/blob/main/LICENSE) 

[![Java CI with Maven](https://github.com/fabiosimplis/rest-with-spring-boot-and-java/actions/workflows/continuous-integration.yml/badge.svg)](https://github.com/fabiosimplis/rest-with-spring-boot-and-java/actions/workflows/continuous-integration.yml)

# Sobre o projeto

Projeto simples de Para catalogar Pessoas e Livros

## Modelo conceitual
### Person
| var | type |
| ----------- | ----------- |
| id | Long |
| firstName | String |
| lastName | String |
| address | String |
| gender | String |
| enabled | boolean |

### Book
| var | type |
| ----------- | ----------- |
| id | Long |
| author | String |
| launchDate | Date |
| price | Double |
| title | String |


# Tecnologias utilizadas
## Back end
- Java
- Spring Boot
- Spring Data
- Swagger
- Flyaway
- Content negotiation, JSON, XML and YAML
- HATEOAS
- Mokito
- JPA / Hibernate
- SQL
- Maven
- JWT
- Spring Security
- Rest Assured
- Testcontainers
- JUnit 5
- Upload e Download de Arquivos
- Docker
- Github Actions
- AWS RDS
- AWS ECR
- AWS ECS


# Como executar o projeto

## Back end
Pré-requisitos: Java 17, docker e docker compose

```bash
# clonar repositório
git clone https://github.com/fabiosimplis/rest-with-spring-boot-and-java

# entrar na pasta do projeto back end
cd rest-with-spring-boot-and-java

# executar o projeto
docker compose up -d --build
```



# Autor

Fábio S. S. Júnior

https://www.linkedin.com/in/fabio-simplis/

