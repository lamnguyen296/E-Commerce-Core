# CommerceFlow - High-Concurrency E-Commerce Microservices Platform

An event-driven e-commerce microservices platform architected to handle high-concurrency order placement and distributed inventory stock reservations.

---

## 🏗 System Architecture

The platform follows the **Database-per-Service** pattern, decoupling the core domain services via **Apache Kafka** event streaming and securing stock operations with **Redis Distributed Lock (Redisson)**.

```
                      +-------------------+
                      |   Client / Web    |
                      +---------+---------+
                                |
                   (HTTP POST /v1/orders)
                                v
               +---------------------------------+
               |          order-service          | <---> [MySQL: order_db]
               +----------------+----------------+
                                |
             (Event: order_created via Apache Kafka)
                                v
               +---------------------------------+
               |         product-service         | <---> [Redis: Redisson RLock]
               +----------------+----------------+ <---> [MySQL: product_db]
                                |
            (Event: product_locked via Apache Kafka)
                                v
               +---------------------------------+
               |          order-service          | (Update Status: CONFIRMED)
               +---------------------------------+
```

---

## 🚀 Key Features

- **Asynchronous Event-Driven Messaging**: Decoupled inter-service communication between `order-service` and `product-service` using **Apache Kafka** with non-blocking retry mechanisms (`@RetryableTopic`).
- **Distributed Locking with Redis (Redisson)**: High-concurrency stock reservation preventing race conditions and inventory overselling during flash-sale traffic spikes.
- **Deadlock Prevention**: Deterministic product ID sorting algorithm ensuring concurrent multi-item transactions never deadlock across distributed threads.
- **Stateless Authentication**: Secured with **JWT**, managing refresh token rotation and logout token blacklisting in **Redis**.
- **Containerized Infrastructure**: Fully automated local setup using **Docker Compose** for Kafka, Zookeeper, Redis, MySQL, Kafka UI, and RedisInsight.

---

## 🛠 Tech Stack

- **Backend**: Java 21, Spring Boot 3.x, Spring Data JPA, Spring Kafka, Spring Validation
- **Distributed Middleware**: Apache Kafka, Redis 7 (Redisson 3.24.3)
- **Databases**: MySQL 8.0
- **DevOps & Monitoring**: Docker, Docker Compose, Kafka UI, RedisInsight
- **Documentation**: Swagger / OpenAPI 3

---

## ⚡ Quick Start

### 1. Start Infrastructure via Docker Compose
```bash
docker compose up -d
```
Infrastructure services will be available at:
- **Kafka Broker**: `localhost:9092`
- **Kafka UI**: `http://localhost:8085` (Username: `admin`, Password: `ChangeThisPassword123!`)
- **Redis Server**: `localhost:6379` (Password: `SecureRedisPassword123!`)
- **RedisInsight UI**: `http://localhost:8001`
- **MySQL**: `localhost:3306` (root / root)

### 2. Run Services
#### Product Service (Port: 8081)
```bash
cd product-service
./mvnw spring-boot:run
```

#### Order Service (Port: 8080)
```bash
cd order-service
./mvnw spring-boot:run
```
