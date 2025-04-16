# MarlowBank ATM Backend System

A backend system simulating ATM operations like **Deposit**, **Withdraw**, and **Balance Check**, built with **Scala (
Play Framework)** and integrated with **PostgreSQL** for data persistence. Designed with RESTful APIs and Docker support
for easy deployment.

---

## Features

- Account deposit & withdrawal with balance checks
- Password authentication for secure transactions
- Concurrency-safe updates using **optimistic locking**
- RESTful APIs with JSON input/output
- Dockerized for containerized deployment

---

## Tech Stack

- **Scala (Play Framework)**
- **PostgreSQL** – database for account/transaction data
- **Slick** – ORM for database interactions
- **Docker** – containerized deployment

---

## Prerequisites

Make sure the following are installed:

- Java 11+
- sbt (Scala Build Tool)
- PostgreSQL
- Docker (for container deployment)

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/CharumathiRajaram/marlowbank.git
cd marlowbank
```

### 2. Configure Database (Edit `conf/application.conf`)

```hocon
db{
host="localhost"
port=5432
database="atm"
uri="jdbc:postgresql://"${db.host}":"${db.port}"/"${db.database}
user="user_1"
secret="Admin@159"
maximum_pool_size = "10"
minimum_idle_connection = "3"
}
```

> ⚠️ Make sure PostgreSQL is running and the `atm` database exists.

### 3. Run the App

```bash
sbt run
```

App will be accessible at: [http://localhost:9000](http://localhost:9000)

---

[//]: # (## 📄 Swagger UI)

[//]: # ()

[//]: # (If Swagger is enabled, access the API docs:)

[//]: # ()

[//]: # (📍 [http://localhost:9000/docs]&#40;http://localhost:9000/docs&#41;)

---

## Sample API Requests

### POST `/withdraw`

```json
{
  "account_id": "11234567",
  "password": "1234",
  "amount": 1000
}
```

### `/deposit`

```json
{
  "account_id": "14567890",
  "password": "1234",
  "amount": 2000
}
```

### `/balance-check`

```json
{
  "account_id": "34569789",
  "password": "1234"
}
```

---

## Docker Instructions

### Build Docker Image

```bash
sbt stage
docker build -t marlowbank-app .
```

### Run Container

```bash
docker run -p 9000:9000 marlowbank-app
```

---

## Project Structure

```
├── app
│   ├── controllers       // HTTP handlers
│   ├── models            // DTOs & request models
│   ├── repository        // DB access logic (Slick)
│   ├── services          // Business logic
│   └── utils             //other helper logic
├── conf
│   └── application.conf
├── Dockerfile
├── build.sbt
└── README.md
```

---

## Contributors

Charumathi Rajaram