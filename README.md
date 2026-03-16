# epaviste.tn — Car Parts Marketplace

A full-stack marketplace platform connecting buyers and sellers of car parts in Tunisia.

- **Backend:** Spring Boot 3.2 · Java 17 · PostgreSQL · JWT Authentication
- **Frontend:** Angular 19 · Bootstrap 5
- **Infrastructure:** Docker Compose

---

## Table of Contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start with Docker Compose](#quick-start-with-docker-compose)
- [Manual Setup](#manual-setup)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Environment Variables](#environment-variables)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [API Overview](#api-overview)

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Docker Compose                        │
│                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │  PostgreSQL  │◄───│   Backend    │◄───│   Frontend   │  │
│  │  Port: 5432  │    │  Port: 8080  │    │  Port: 4200  │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| Docker | 24+ | https://docs.docker.com/get-docker/ |
| Docker Compose | 2.x | Bundled with Docker Desktop |
| Java | 17+ | https://adoptium.net/ (for manual setup) |
| Maven | 3.9+ | https://maven.apache.org/ (for manual setup) |
| Node.js | 18+ | https://nodejs.org/ (for manual setup) |
| npm | 9+ | Bundled with Node.js |

---

## Quick Start with Docker Compose

The easiest way to run the entire application stack.

```bash
# Clone the repository
git clone https://github.com/ahmedelmemmi/epaviste.git
cd epaviste

# Start all services (PostgreSQL + Backend + Frontend)
docker compose up --build

# Or run in detached mode
docker compose up --build -d
```

Once running, access the application at:

| Service | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

To stop all services:
```bash
docker compose down

# To also remove the database volume (wipes all data)
docker compose down -v
```

---

## Manual Setup

### Backend

**Prerequisites:** Java 17, Maven 3.9+, PostgreSQL 15

#### 1. Set up PostgreSQL

```bash
# Using Docker for just the database
docker run -d \
  --name epaviste-db \
  -e POSTGRES_DB=epaviste \
  -e POSTGRES_USER=epaviste \
  -e POSTGRES_PASSWORD=epaviste123 \
  -p 5432:5432 \
  postgres:15-alpine
```

Or create the database manually in an existing PostgreSQL instance:
```sql
CREATE DATABASE epaviste;
CREATE USER epaviste WITH ENCRYPTED PASSWORD 'epaviste123';
GRANT ALL PRIVILEGES ON DATABASE epaviste TO epaviste;
```

#### 2. Configure environment (optional — defaults work with the Docker DB above)

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/epaviste
export SPRING_DATASOURCE_USERNAME=epaviste
export SPRING_DATASOURCE_PASSWORD=epaviste123
export APP_JWT_SECRET=epaviste-secret-key-for-jwt-token-generation-must-be-long-enough
export APP_CORS_ALLOWED_ORIGINS=http://localhost:4200
```

#### 3. Build and run

```bash
cd backend
mvn spring-boot:run
```

Or to build a JAR and run it:
```bash
cd backend
mvn clean package -DskipTests
java -jar target/epaviste-backend-0.0.1-SNAPSHOT.jar
```

The backend will start on **http://localhost:8080**.

---

### Frontend

**Prerequisites:** Node.js 18+, npm 9+

```bash
cd frontend
npm install --legacy-peer-deps
npm start
```

The frontend development server will start on **http://localhost:4200** with hot-reload enabled.

For a production build:
```bash
cd frontend
npm run build
```
The build output will be in `frontend/dist/`.

---

## Environment Variables

### Backend

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/epaviste` | PostgreSQL JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | `epaviste` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `epaviste123` | Database password |
| `APP_JWT_SECRET` | `epaviste-secret-key-...` | Secret key for signing JWT tokens (change in production!) |
| `APP_JWT_EXPIRATION` | `86400000` | JWT token expiry in milliseconds (default: 24 hours) |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Comma-separated list of allowed CORS origins |
| `APP_COMMISSION_RATE` | `0.10` | Platform commission rate (10%) |

---

## API Documentation (Swagger)

Interactive API documentation is available via Swagger UI when the backend is running:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON spec:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML spec:** http://localhost:8080/v3/api-docs.yaml

### Authenticating in Swagger UI

1. Call `POST /api/auth/login` with valid credentials
2. Copy the `token` value from the response
3. Click the **Authorize 🔒** button at the top of the Swagger UI
4. Enter `Bearer <your-token>` in the `bearerAuth` field
5. Click **Authorize** — all subsequent requests will include the JWT token

---

## API Overview

All API endpoints are prefixed with `/api`.

### Authentication (`/api/auth`) — Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new buyer or seller account |
| `POST` | `/api/auth/login` | Log in and receive a JWT token |

### RFQs — Requests For Quotes (`/api/rfqs`) — 🔒 Auth Required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/rfqs` | Create a new RFQ (Buyer) |
| `GET` | `/api/rfqs` | List all open RFQs (paginated) |
| `GET` | `/api/rfqs/{id}` | Get a specific RFQ by ID |
| `GET` | `/api/rfqs/my` | Get the authenticated buyer's RFQs |
| `DELETE` | `/api/rfqs/{id}` | Cancel an RFQ (Buyer) |

### Quotes (`/api/quotes`) — 🔒 Auth Required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/quotes` | Submit a quote for an RFQ (Seller) |
| `GET` | `/api/quotes/rfq/{rfqId}` | Get all quotes for a specific RFQ |
| `GET` | `/api/quotes/my` | Get the authenticated seller's quotes |
| `PUT` | `/api/quotes/{id}/accept` | Accept a quote (Buyer) |
| `PUT` | `/api/quotes/{id}/reject` | Reject a quote (Buyer) |

### Orders (`/api/orders`) — 🔒 Auth Required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/orders/my` | Get the authenticated user's orders |
| `GET` | `/api/orders/{id}` | Get a specific order by ID |
| `PUT` | `/api/orders/{id}/confirm-delivery` | Confirm delivery (Buyer) |
| `PUT` | `/api/orders/{id}/ship` | Mark order as shipped (Seller) |

### Payments (`/api/payments`) — 🔒 Auth Required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/payments/process` | Process payment for an order |
| `GET` | `/api/payments/order/{orderId}` | Get payment status by order ID |

### Reviews (`/api/reviews`) — 🔒 Auth Required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/reviews` | Submit a review for a seller (Buyer) |
| `GET` | `/api/reviews/seller/{sellerId}` | Get all reviews for a seller |

### Notifications (`/api/notifications`) — 🔒 Auth Required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/notifications` | Get the authenticated user's notifications (paginated) |
| `PUT` | `/api/notifications/{id}/read` | Mark a notification as read |
| `PUT` | `/api/notifications/read-all` | Mark all notifications as read |
