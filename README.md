# 🔗 URL Shortener - Spring Boot + Java 21

Encurtador de URLs desenvolvido com **Spring Boot 3**, **Java 21**, **MySQL**, **Redis** e **Docker**.  
Permite criar links encurtados com tempo de expiração e contagem de cliques.

---

## 🚀 Tecnologias

- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Validation
- Redis (cache + contagem de cliques)
- MySQL
- Docker & Docker Compose
- JUnit + MockMvc

---

## 📌 Funcionalidades

- Criar URL encurtada
- Redirecionamento automático
- Expiração por tempo (TTL)
- Contagem de cliques
- Cache com Redis
- Limpeza automática de URLs expiradas
- Testes automatizados
- Pronto para deploy em nuvem

---

## 🐳 Subindo o projeto com Docker

```bash
docker-compose up --build