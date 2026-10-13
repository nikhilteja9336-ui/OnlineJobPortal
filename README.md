# Online Job Portal (Java Spring Boot + MySQL + React)

Folders: `backend/` (Spring Boot 3, Java 17, MySQL, JWT) · `frontend/` (React + Vite) · `database/schema.sql`

Features: register/login (Job Seeker or Employer), JWT security, search jobs by title/company/location/type,
job details, apply with cover letter (no duplicates), seeker application tracking, employer post/delete jobs,
view applicants, shortlist/reject. Demo users (seeded): employer@demo.com and seeker@demo.com, password demo123.

## 1. Run locally in VS Code
Install: JDK 17, Maven, Node 18+, MySQL 8. VS Code extensions: "Extension Pack for Java", "Spring Boot Extension Pack".
1. MySQL: run `CREATE DATABASE jobportal;` (or the file in /database). Default login in application.properties is root/root - change DB_USER / DB_PASS env vars if yours differ.
2. Backend: `cd backend && mvn spring-boot:run` -> http://localhost:8080
3. Frontend (new terminal): `cd frontend && cp .env.example .env && npm install && npm run dev` -> http://localhost:5173

## 2. Push to GitHub
```
cd online-job-portal
git init && git add . && git commit -m "Online job portal"
git branch -M main
git remote add origin https://github.com/<you>/online-job-portal.git
git push -u origin main
```

## 3. MySQL in the cloud (Render has no managed MySQL)
Create a free MySQL on Aiven, TiDB Cloud, Railway or FreeSQLDatabase. Note host, port, db, user, password.
JDBC URL format: `jdbc:mysql://HOST:PORT/DBNAME?useSSL=true&serverTimezone=UTC`

## 4. Deploy backend on Render
New > Web Service > connect GitHub repo. Root Directory: `backend`. Runtime: Docker. Add env vars:
DB_URL (JDBC url above), DB_USER, DB_PASS, JWT_SECRET (32+ random chars), CORS_ORIGINS (your Vercel URL, no trailing slash; add after step 5).
Deploy, then open the Render URL: it should say "Job Portal API is running".

## 5. Deploy frontend on Vercel
Add New > Project > import repo. Root Directory: `frontend`. Framework: Vite. Env var: VITE_API_URL = your Render URL (no trailing slash). Deploy.
Then go back to Render and set CORS_ORIGINS to the Vercel URL and redeploy. (Free Render services sleep; first request can take ~50s.)
