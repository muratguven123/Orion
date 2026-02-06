# Orion

Orion is a microservices-based financial platform built with Spring Boot and Spring Cloud. It provides user account management, payment processing, and investment operations through a distributed architecture with service discovery, an API gateway, and centralized monitoring.

## Architecture

```
                        ┌──────────────┐
                        │  API Gateway │
                        │   (:8222)    │
                        └──────┬───────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
     ┌────────▼──────┐ ┌──────▼───────┐ ┌──────▼───────┐
     │ Account Svc   │ │ Payment Svc  │ │ Invest Svc   │
     │   (:8081)     │ │   (:8082)    │ │   (:8083)    │
     └───────────────┘ └──────────────┘ └──────────────┘
              │                │                │
              └────────────────┼────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Discovery Server   │
                    │  (Eureka :8761)     │
                    └─────────────────────┘
```

## Tech Stack

- **Java 17**
- **Spring Boot 3.x** — Application framework
- **Spring Cloud** — Microservices coordination (Eureka, Gateway, Config, OpenFeign)
- **PostgreSQL** — Relational database
- **Spring Security & JWT** — Authentication and authorization
- **Swagger / OpenAPI** — API documentation
- **Prometheus & Grafana** — Monitoring and dashboards
- **Docker & Docker Compose** — Containerization
- **Lombok** — Boilerplate reduction
- **Maven** — Build tool

## Services

| Service | Port | Description |
|---|---|---|
| **Discovery Server** | 8761 | Eureka service registry for dynamic service discovery |
| **API Gateway** | 8222 | Single entry point that routes requests to backend services |
| **Account Service** | 8081 | User account management, balance operations, account status |
| **Payment Service** | 8082 | Payment transactions, transfers, balance updates |
| **Invest Service** | 8083 | Investment operations (stocks, crypto), portfolio management |
| **Config Server** | — | Centralized configuration management for all services |
| **Orion App** | 8080 | Main application handling authentication, notifications, and orchestration |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL 17
- Docker & Docker Compose (optional)

### Environment Variables

Create a `.env` file in the project root with the following variables:

```env
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_BOT_USERNAME=your_telegram_bot_username
```

### Running with Docker Compose

```bash
cd discovery-server
docker compose up -d
```

This starts the main application, PostgreSQL, Prometheus, and Grafana.

### Running Manually

1. **Start PostgreSQL** and create the required databases:
   - `orion_account_db`
   - `orion_payment_db`
   - `invest_orion_db`

2. **Start Discovery Server** first:
   ```bash
   cd discovery-server
   mvn spring-boot:run
   ```

3. **Start the remaining services** (in any order):
   ```bash
   cd account-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   cd invest-service && mvn spring-boot:run
   cd api-gateway && mvn spring-boot:run
   ```

4. **Start the main application**:
   ```bash
   mvn spring-boot:run
   ```

### Build

```bash
mvn clean install
```

## Monitoring

| Tool | URL | Description |
|---|---|---|
| Eureka Dashboard | http://localhost:8761 | Registered services and health |
| Prometheus | http://localhost:9090 | Metrics collection |
| Grafana | http://localhost:3000 | Monitoring dashboards |
| Swagger UI | http://localhost:8080/swagger-ui.html | API documentation |

## Key Features

- **Authentication** — JWT-based auth with OTP verification, email and SMS login strategies
- **Account Management** — Create, update, and manage user accounts and balances
- **Payments** — Process transactions, internal transfers, and track payment status
- **Investments** — Portfolio management with stock and crypto operations
- **Notifications** — Event-driven notifications via email, SMS, and Telegram
- **Service Discovery** — Automatic registration and discovery with Eureka
- **API Gateway** — Centralized routing and load balancing
- **Monitoring** — Prometheus metrics with Grafana dashboards
