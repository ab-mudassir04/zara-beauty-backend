# 💄 Zara Beauty — Backend

A production-oriented **RESTful backend API** for the Zara Beauty e-commerce application, developed using **Java, Spring Boot, Spring Security, Hibernate/JPA, and MySQL**.

The backend provides secure APIs for authentication, products, categories, customers, orders, reviews, coupons, newsletter subscriptions, payments, and administration.

The application is containerized with **Docker** and deployed on **Render**, with **Aiven Cloud MySQL** used as the production database.

---

## 🌐 Deployment

**Backend API:**

https://zara-beauty-backend.onrender.com

**Health Check:**

https://zara-beauty-backend.onrender.com/actuator/health

Expected response:

```json
{
  "status": "UP"
}
```

---

## 📌 Project Overview

Zara Beauty Backend is the server-side component of the Zara Beauty full-stack e-commerce application.

It provides REST APIs consumed by the React frontend.

The backend is responsible for:

* Authentication
* Authorization
* Business logic
* Product management
* Customer management
* Order processing
* Payment processing
* Review management
* Coupon management
* Newsletter management
* Database operations
* API security

---

## ✨ Features

### 🔐 Authentication & Authorization

* User registration
* User login
* JWT authentication
* Role-based authorization
* Customer authorization
* Admin authorization
* BCrypt password hashing
* Stateless Spring Security

### 🛍️ Product Management

* Create product
* Get products
* Get product by ID
* Update product
* Delete product
* Category management
* Product availability management

### 👥 User Management

* User registration
* Login
* Profile retrieval
* Customer management
* Admin user management

### 📦 Order Management

* Place orders
* Retrieve customer orders
* Retrieve order details
* Admin order management
* Update order status
* Update payment status
* Order history

### ⭐ Review Management

* View reviews
* Add reviews
* Update reviews
* Delete reviews
* Admin review management

### 🎟️ Coupon Management

* Create coupons
* Manage coupons
* Apply discount logic
* Update coupons
* Delete coupons

### 📧 Newsletter

* Newsletter subscription
* Admin subscriber management
* Delete subscribers

### 💳 Payments

* Razorpay integration
* Payment order creation
* Payment verification
* Payment status management
* Refund processing
* Admin payment management

---

## 🧑‍💻 Technology Stack

| Technology        | Purpose                        |
| ----------------- | ------------------------------ |
| Java 17           | Backend programming            |
| Spring Boot 4.0.6 | Backend framework              |
| Spring Web        | REST APIs                      |
| Spring Data JPA   | Database access                |
| Hibernate         | ORM                            |
| Spring Security   | Authentication & authorization |
| JWT               | Token-based authentication     |
| BCrypt            | Password hashing               |
| MySQL             | Relational database            |
| Maven             | Dependency/build management    |
| Lombok            | Boilerplate reduction          |
| Docker            | Containerization               |
| Postman           | API testing                    |
| Render            | Backend deployment             |
| Aiven             | Cloud MySQL                    |
| Razorpay          | Payment gateway                |

---

## 🏗️ Architecture

```text
                   React Frontend
                         │
                         │ HTTPS / REST
                         ▼
              ┌──────────────────────┐
              │   Spring Boot API    │
              │        Render        │
              └──────────┬───────────┘
                         │
              ┌──────────┴──────────┐
              │                     │
              ▼                     ▼
      ┌───────────────┐     ┌───────────────┐
      │   MySQL       │     │   Razorpay    │
      │ Aiven Cloud   │     │ Payment API   │
      └───────────────┘     └───────────────┘
```

---

## 📁 Project Structure

```text
zara-beauty-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── zara/
│   │   │           └── backend/
│   │   │
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               ├── service/
│   │   │               └── BackendApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── .mvn/
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
└── README.md
```

---

## 🔐 Security Architecture

The application uses **Spring Security with JWT authentication**.

Authentication flow:

```text
User
 │
 ▼
Login API
 │
 ▼
Validate Credentials
 │
 ▼
Generate JWT
 │
 ▼
Frontend stores token
 │
 ▼
Authorization: Bearer <token>
 │
 ▼
JWT Authentication Filter
 │
 ▼
Spring Security
 │
 ▼
Controller
```

### Roles

The application supports role-based authorization, including:

```text
USER
ADMIN
```

Administrative endpoints require:

```text
ROLE_ADMIN
```

---

## 🔑 Password Security

User passwords are protected using:

```text
BCryptPasswordEncoder
```

Passwords are never stored as plain text.

---

## 🌐 CORS

The backend is configured to allow requests from the frontend application.

Development origins include:

```text
http://localhost:5173
http://localhost:5174
http://localhost:4173
```

The production Netlify URL should also be configured in the backend CORS settings.

---

## 🗄️ Database

Production database:

```text
MySQL
```

Cloud provider:

```text
Aiven
```

The application uses:

* Spring Data JPA
* Hibernate
* MySQL Connector/J
* HikariCP connection pooling

Database credentials are supplied through environment variables and are **not stored in source control**.

---

## ⚙️ Environment Variables

The backend uses environment variables for production configuration.

Example:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
DB_SSL
JPA_DDL_AUTO
JPA_SHOW_SQL
JWT_SECRET
JWT_EXPIRATION
RAZORPAY_KEY_ID
RAZORPAY_KEY_SECRET
PORT
```

Example local configuration:

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=zara_beauty
DB_USERNAME=root
DB_PASSWORD=your-password
DB_SSL=false

JPA_DDL_AUTO=update
JPA_SHOW_SQL=false

JWT_SECRET=your-development-secret
JWT_EXPIRATION=86400000

RAZORPAY_KEY_ID=your-key
RAZORPAY_KEY_SECRET=your-secret
```

> Never commit real passwords, JWT secrets, database credentials, or Razorpay secret keys to GitHub.

---

## 🚀 Local Development

### Requirements

Install:

* Java 17
* Maven
* MySQL
* Git

Docker can also be used.

---

### 1. Clone repository

```bash
git clone https://github.com/ab-mudassir04/zara-beauty-backend.git
```

### 2. Navigate to backend

```bash
cd zara-beauty-backend
```

### 3. Configure environment variables

Configure the required database and application environment variables.

### 4. Build application

Windows:

```cmd
.\mvnw.cmd clean package -DskipTests
```

Linux/macOS:

```bash
./mvnw clean package -DskipTests
```

### 5. Run application

Windows:

```cmd
.\mvnw.cmd spring-boot:run
```

The backend normally runs on:

```text
http://localhost:8080
```

---

## 🐳 Docker

The backend includes Docker support for production deployment.

Build the image:

```bash
docker build -t zara-beauty-backend .
```

Run the container:

```bash
docker run -p 8080:8080 zara-beauty-backend
```

Check running containers:

```bash
docker ps
```

---

## ☁️ Render Deployment

The backend is deployed as a Docker-based Web Service on Render.

Production architecture:

```text
GitHub
   │
   ▼
Render
   │
   ▼
Docker Container
   │
   ▼
Spring Boot
   │
   ▼
Aiven MySQL
```

Render environment variables should contain the production database and application configuration.

The application reads the Render-provided:

```text
PORT
```

environment variable.

---

## ❤️ Health Monitoring

Spring Boot Actuator provides a health endpoint:

```text
GET /actuator/health
```

Production URL:

```text
https://zara-beauty-backend.onrender.com/actuator/health
```

This endpoint is useful for:

* Deployment verification
* Service health monitoring
* Render health checks
* Database connectivity verification

---

## 🔗 API Endpoints

### Authentication

```text
POST /auth/register
POST /auth/login
```

### Products

```text
GET    /products
GET    /products/{id}
POST   /products
PUT    /products/{id}
DELETE /products/{id}
```

### Categories

```text
GET    /categories
POST   /categories
PUT    /categories/{id}
DELETE /categories/{id}
```

### Orders

```text
POST /orders
GET  /orders/user/{userId}
GET  /orders
GET  /orders/{id}
PUT  /orders/{id}/status
PUT  /orders/{id}/payment-status
```

### Reviews

```text
GET    /reviews
POST   /reviews
PUT    /reviews/{id}
DELETE /reviews/{id}
```

### Users

```text
GET /users/profile
GET /users/customers
GET /users/customers/{id}
```

### Newsletter

```text
POST   /newsletter/subscribe
GET    /newsletter/subscribers
DELETE /newsletter/subscribers/{id}
```

### Payments

```text
POST /payments/create/{orderId}
POST /payments/verify
GET  /payments/order/{orderId}
POST /payments/refund/{paymentId}
GET  /payments
GET  /payments/{id}
```

> Exact endpoint availability depends on the current controller implementation.

---

## 🧪 API Testing

The APIs can be tested using **Postman**.

Example:

```http
GET https://zara-beauty-backend.onrender.com/products
```

For protected APIs:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 📦 Build

Create the production JAR:

```bash
./mvnw clean package -DskipTests
```

Windows:

```cmd
.\mvnw.cmd clean package -DskipTests
```

The generated JAR is created inside:

```text
target/
```

---

## 🔄 Development Workflow

```text
Code
 │
 ▼
Test locally
 │
 ▼
Postman API testing
 │
 ▼
Maven build
 │
 ▼
Docker build
 │
 ▼
Git commit
 │
 ▼
GitHub
 │
 ▼
Render deployment
 │
 ▼
Production API
```

---

## 🔒 Production Security Practices

The application follows production-oriented practices including:

* JWT authentication
* Role-based authorization
* BCrypt password hashing
* Stateless sessions
* CORS configuration
* Environment-based secrets
* HTTPS production API
* Database SSL
* HikariCP connection pooling
* Actuator health monitoring
* Restricted admin APIs
* No credentials committed to GitHub

---

## 📊 Production Configuration

The production application is configured to use:

```text
Java 17
Spring Boot 4.0.6
MySQL 8.4.x
Docker
Render
Aiven
```

The backend is configured to listen on the port supplied by the hosting platform.

---

## 👨‍💻 Developer

**Abdul Mudassir**

B.Sc. Computer Science
Java Full Stack Developer

GitHub:

https://github.com/ab-mudassir04

---

## 📄 License

This project is developed for educational and portfolio purposes.
