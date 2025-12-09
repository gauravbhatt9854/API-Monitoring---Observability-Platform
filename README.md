API Monitoring & Observability Platform
Spring Boot (Kotlin) + Next.js + Dual MongoDB + JWT Auth

A complete backend + dashboard system for tracking API performance, log collection, real-time error analytics, alerts, and incident management.

⭐ Features
🔧 Backend (Spring Boot)

Dual MongoDB (logsdb + metadb)

Custom API Logging Interceptor

Built-in Rate Limiter (per service)

JWT Authentication

Alerts & Incident Engine

Modular Repository Structure

🎨 Frontend (Next.js)

Secure Login (JWT)

Dashboard with charts (Recharts)

Logs Explorer

Alerts Center

Incident Management

🧩 System Architecture
Microservices → Interceptor → Collector Service → MongoDB(logsdb)
                                       ↓
                                  Dashboard APIs → MongoDB(metadb)
                                       ↓
                                     Frontend

📦 Project Structure
C:.
│   .env
│   build.gradle.kts
│   settings.gradle.kts
│   gradlew
│   gradlew.bat
│
├───src
│   ├───main
│   │   ├───kotlin
│   │   │   └───com
│   │   │       └───example
│   │   │           └───demo
│   │   │               │   DemoApplication.kt
│   │   │
│   │   │               ├───config
│   │   │               │       DotenvConfig.kt
│   │   │               │       LogsDbConfig.kt
│   │   │               │       MetaDbConfig.kt
│   │   │               │       RateLimitProperties.kt
│   │   │               │       SecurityConfig.kt
│   │   │               │       UserSeeder.kt
│   │   │               │       WebConfig.kt
│   │   │
│   │   │               ├───controller
│   │   │               │       AuthController.kt
│   │   │               │       AlertController.kt
│   │   │               │       DashboardController.kt
│   │   │               │       IncidentController.kt
│   │   │               │       LogController.kt
│   │   │               │       RateLimitController.kt
│   │   │               │       TestController.kt
│   │   │
│   │   │               ├───dto
│   │   │               │       SearchParams.kt
│   │   │
│   │   │               ├───filter
│   │   │               │       JwtFilter.kt
│   │   │
│   │   │               ├───interceptor
│   │   │               │       ApiTrackingInterceptor.kt
│   │   │
│   │   │               ├───model
│   │   │               │       User.kt
│   │   │               │       Alert.kt
│   │   │               │       Incident.kt
│   │   │               │       ApiLog.kt
│   │   │               │       RateLimitConfig.kt
│   │   │               │       RateLimitHit.kt
│   │   │
│   │   │               ├───repository
│   │   │               │   ├───logs
│   │   │               │   │       ApiLogRepository.kt
│   │   │               │   │       RateLimitHitRepository.kt
│   │   │               │   └───meta
│   │   │               │           UserRepository.kt
│   │   │               │           AlertRepository.kt
│   │   │               │           IncidentRepository.kt
│   │   │               │           RateLimitConfigRepository.kt
│   │   │
│   │   │               ├───service
│   │   │               │       AuthService.kt
│   │   │               │       AlertService.kt
│   │   │               │       IncidentService.kt
│   │   │               │       LogService.kt
│   │   │               │       LogQueryService.kt
│   │   │               │       RateLimitService.kt
│   │   │               │       DashboardService.kt
│   │   │
│   │   │               └───util
│   │   │                       JwtUtil.kt
│   │   │
│   │   └───resources
│   │           application.yaml
│   │           application.properties
│
└───test
        DemoApplicationTests.kt


C:.
│   .env
│   .gitignore
│   components.json
│   eslint.config.mjs
│   next-env.d.ts
│   next.config.ts
│   package-lock.json
│   package.json
│   postcss.config.mjs
│   README.md
│   tsconfig.json
│
├───app
│   │   globals.css
│   │   layout.tsx
│   │   page.tsx
│   │
│   ├───alerts
│   │       page.tsx
│   │
│   ├───dashboard
│   │       page.tsx
│   │
│   ├───incidents
│   │       page.tsx
│   │
│   ├───login
│   │       page.tsx
│   │
│   └───logs
│           page.tsx
│
├───components
│       DashboardWidget.tsx
│       FiltersPanel.tsx
│       LogsModal.tsx
│       LogsTable.tsx
│       Navbar.tsx
│       Pagination.tsx
│       RequireAuth.tsx
│       Sidebar.tsx
│
├───lib
│   │   api.ts
│   │   auth.ts
│   │   utils.ts
│   │
│   └───hooks
│           useDebounce.ts
│
└───public
        file.svg
        globe.svg
        next.svg
        vercel.svg
        window.svg


✅ Frontend .env.local
NEXT_PUBLIC_API_BASE=http://localhost:8080

✅ Backend .env

Replace YOUR_IP with your machine’s LAN IP (e.g., 192.168.1.10).

LOGS_DB_URI=mongodb://user:password@YOUR_IP:27017/logsdb?authSource=admin
META_DB_URI=mongodb://user:password@YOUR_IP:27017/metadb?authSource=admin
JWT_SECRET=supersecretkey

🗄 Database Design
logsdb (DB1 — High volume)
Collection	Description
api_logs	All request logs
rate_limits	Rate limit hits
metadb (DB2 — Metadata)
Collection	Description
users	Login users (bcrypt hashed passwords)
incidents	Slow/broken endpoints
alerts	Alerts generated from logs
config	Future overrides
🔐 Authentication
Login
POST /api/auth/login


Body:

{
  "username": "admin",
  "password": "admin123"
}


Backend flow:

Fetch user from MetaDB

Compare bcrypt hash

Generate JWT (HS256, secure key)

Return token

Frontend stores token in:

localStorage.token

Protected routes:
/api/dashboard/**
/api/logs/**
/api/alerts/**
/api/incidents/**

🔍 Logging Interceptor (Microservices)

Each microservice includes a custom interceptor that captures:

Path

Method

Request size / response size

Status

Latency

Timestamp

Service name

Sends data to:

POST /collector/log

⚡ Rate Limiter

Per-service configurable:

monitoring:
  rateLimit:
    service: orders
    limit: 100


If exceeded → log "rate-limit-hit" event.

📊 Dashboard
Widgets

Slow Requests

Broken Requests (5xx)

Avg Latency

Top 5 Slow Endpoints

Error Rate Chart

Plots last 60 minutes of errors.

Logs Explorer

Filter by service, endpoint, date, status

Shows slow & rate-limit-hit logs

Alerts

Generated automatically when:

Status 5xx

Latency > 500ms

Rate limit exceeded

Incidents

Developer can mark issues as Resolved.

▶️ Running the Backend
1. Start backend
./gradlew bootRun

2. Default URLs
Backend: http://localhost:8080
Actuator: http://localhost:8080/actuator/health
Login API: http://localhost:8080/api/auth/login

▶️ Running the Frontend
npm install
npm run dev


Runs at:

http://localhost:3000

👤 Default Login User

Created with UserSeeder:

username: admin  
password: admin123


After first successful login → remove the seeder file.

📌 Non-Functional Requirements Completed

✔ Modular code
✔ Concurrency safe
✔ Works with two MongoDB clusters
✔ JWT security
✔ Rich dashboard
✔ Logs & incidents stored independently