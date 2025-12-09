API Monitoring & Observability Platform

A complete API performance monitoring, log analytics, alerts, and incident management platform built using:

Spring Boot (Kotlin) — backend + collector

Next.js — frontend dashboard

MongoDB (Dual DB: logsdb + metadb)

JWT Authentication

This system captures logs from microservices, analyzes performance, and provides a modern dashboard for observability.

⭐ Features Overview
🔧 Backend (Spring Boot / Kotlin)

Dual MongoDB Connection

logsdb → high-volume logs

metadb → users, alerts, incidents

Custom API Logging Interceptor

Built-in Rate Limiter (per service)

JWT Authentication (HS256)

Alert engine (5xx, slow requests, rate-limit)

Incident workflow

Modular Services + Repos

🎨 Frontend (Next.js)

JWT-based authentication

Modern dashboard (Recharts)

Logs Explorer with filters

Alerts Center

Incident Management UI

Protected routes

🧩 Architecture
Microservices 
     ↓ (interceptor sends logs)
Collector API  →  logsdb (API Logs + Rate Limit Hits)
     ↓
Dashboard APIs → metadb (Users, Alerts, Incidents)
     ↓
Next.js Frontend Dashboard

📁 Project Structure
Backend (Spring Boot — Kotlin)
src/main/kotlin/com/example/demo/
│── config/         (DB configs, security, dotenv, seeder)
│── controller/     (Auth, Logs, Alerts, Incidents, Dashboard)
│── interceptor/    (ApiTrackingInterceptor.kt)
│── filter/         (JwtFilter.kt)
│── service/        (Auth, Log, Alert, Incident, Dashboard)
│── repository/     (logs/* , meta/*)
│── model/          (ApiLog, User, Alert, Incident, RateLimit*)
│── util/           (JwtUtil.kt)

Frontend (Next.js)
app/
│── login/
│── dashboard/
│── logs/
│── alerts/
│── incidents/
components/
lib/
public/

🔐 Authentication
Login API
POST /api/auth/login


Body

{
  "username": "admin",
  "password": "admin123"
}


Flow

Validate against metadb

bcrypt password check

Generate JWT (HS256)

Frontend stores token in: localStorage.token

Protected Endpoints
/api/dashboard/**
/api/logs/**
/api/alerts/**
/api/incidents/**

🗄 Database Design
logsdb
Collection	Description
api_logs	All API request logs
rate_limits	Rate limit hit events
metadb
Collection	Description
users	Auth users (bcrypt)
alerts	Alerts raised by engine
incidents	Dev-managed incidents
config	Future overrides
📡 Collector Log Format (Microservices → Backend)

Every microservice should POST logs to:

POST /collector/log


Example Payload

{
  "service": "orders",
  "path": "/api/orders",
  "method": "POST",
  "status": 200,
  "requestSize": 512,
  "responseSize": 1200,
  "latencyMs": 140,
  "timestamp": "2025-12-09T10:00:00Z"
}

⚡ Rate Limiter

Configurable per service:

monitoring:
  rateLimit:
    service: orders
    limit: 100


When exceeded → stored as rate-limit-hit event.

📊 Dashboard Modules
Widgets

Slow Requests

Broken Requests (5xx)

Average Latency

Top 5 Slow Endpoints

Error Rate (last 60 mins)

Logs Explorer

Filter by:

Service

Endpoint

Status

Date

Slow requests

Rate-limit events

Alerts

Automatically created when:

Status: 5xx

Latency: > 500ms

Rate limit hit

Incidents

Create incidents from alerts

Mark as resolved

🟩 Environment Setup
Frontend .env.local
NEXT_PUBLIC_API_BASE=http://localhost:8080

Backend .env

Replace YOUR_IP with LAN IP (e.g., 192.168.1.10):

LOGS_DB_URI=mongodb://user:password@YOUR_IP:27017/logsdb?authSource=admin
META_DB_URI=mongodb://user:password@YOUR_IP:27017/metadb?authSource=admin
JWT_SECRET=supersecretkey

▶️ Running the Project
Backend
./gradlew bootRun


Available at:

Backend: http://localhost:8080

Health Check: http://localhost:8080/actuator/health

Frontend
npm install
npm run dev


Accessible at:
http://localhost:3000

👤 Default Login

Created via UserSeeder:

username: admin
password: admin123


Remove UserSeeder.kt after first login in production.

✔ Non-Functional Achievements

Thread-safe & modular services

Works with two MongoDB clusters

Decoupled logs & incidents

JWT-secured APIs

Rich visual dashboard
