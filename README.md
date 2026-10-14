# Online Job Portal (Java Spring Boot + TiDB Cloud + React)

A full-stack job portal built with **Java Spring Boot 3**, **TiDB Cloud (Serverless MySQL)**, and **React (Vite)**.

## Project Structure
- `backend/`: Spring Boot 3, Java 17, TiDB/MySQL Connector, Spring Security + JWT, Hibernate JPA
- `frontend/`: React 18, Vite, responsive UI with seeker & employer flows
- `database/`: `schema.sql` database initialization script
- `render.yaml`: Render Blueprint for one-click backend deployment

---

## 1. TiDB Cloud Configuration

TiDB Cloud Serverless provides fully managed, secure MySQL-compatible cloud storage.

> **Important Database Note**: 
> In MySQL and TiDB, `sys` is a reserved system schema that restricts table creation. The application uses the `jobportal` database on your TiDB cluster. The tables (`users`, `job`, `application`) are automatically created and seeded by Hibernate on startup.

### Connection Parameters:
- **Host**: `gateway01.ap-northeast-1.prod.aws.tidbcloud.com`
- **Port**: `4000`
- **Username**: `2xnUMpj84YDghCh.root`
- **Password**: `UHP8Ei1Uy6H0ERPI`
- **Database**: `jobportal`
- **JDBC URL**:
  ```text
  jdbc:mysql://gateway01.ap-northeast-1.prod.aws.tidbcloud.com:4000/jobportal?sslMode=VERIFY_IDENTITY&useSSL=true&requireSSL=true&serverTimezone=UTC
  ```
- **SSL / CA Note**: TiDB Serverless TLS certificates are signed by standard public CAs (Let's Encrypt / ISRG Root X1), which are built directly into Java 17+ and the Docker runtime image. No manual CA file download is needed for Java/JDBC when using `sslMode=VERIFY_IDENTITY`.

---

## 2. Deploy Backend on Render

You can deploy the backend using either **Render Blueprints** (recommended) or manually via the Render Dashboard.

### Option A: Using Render Blueprints (Automatic)
1. Push this repo to GitHub.
2. Go to [Render Dashboard](https://dashboard.render.com/) -> **Blueprints**.
3. Click **New Blueprint Instance** and connect your GitHub repository `OnlineJobPortal`.
4. Render will read `render.yaml` and configure the service automatically.
5. Click **Apply**.

### Option B: Manual Web Service Setup
1. Go to [Render Dashboard](https://dashboard.render.com/) -> **New +** -> **Web Service**.
2. Connect your GitHub repository: `https://github.com/nikhilteja9336-ui/OnlineJobPortal.git`.
3. Configure the settings:
   - **Name**: `online-job-portal-backend`
   - **Root Directory**: `backend`
   - **Runtime**: `Docker`
   - **Instance Type**: `Free`
4. Add the following **Environment Variables**:
   | Key | Value |
   |---|---|
   | `PORT` | `8080` |
   | `DB_URL` | `jdbc:mysql://gateway01.ap-northeast-1.prod.aws.tidbcloud.com:4000/jobportal?sslMode=VERIFY_IDENTITY&useSSL=true&requireSSL=true&serverTimezone=UTC` |
   | `DB_USER` | `2xnUMpj84YDghCh.root` |
   | `DB_PASS` | `UHP8Ei1Uy6H0ERPI` |
   | `JWT_SECRET` | Any random 32+ character string (e.g. `my-super-secret-key-job-portal-tidb-render-vercel-32chars`) |
   | `CORS_ORIGINS` | `*` (or your Vercel URL once deployed) |
5. Click **Deploy Web Service**.
6. When deployment finishes, visit your Render URL (e.g., `https://online-job-portal-backend.onrender.com/`). You should see:
   ```text
   Job Portal API is running
   ```

---

## 3. Deploy Frontend on Vercel

1. Go to [Vercel Dashboard](https://vercel.com/dashboard) -> **Add New...** -> **Project**.
2. Import your GitHub repository: `nikhilteja9336-ui/OnlineJobPortal`.
3. Configure the project:
   - **Framework Preset**: `Vite`
   - **Root Directory**: Click *Edit* and select `frontend`
   - **Build Command**: `npm run build` (default)
   - **Output Directory**: `dist` (default)
4. Add **Environment Variable**:
   - **Name**: `VITE_API_URL`
   - **Value**: Your Render Backend URL without a trailing slash (e.g. `https://online-job-portal-backend.onrender.com`)
5. Click **Deploy**.
6. Vercel will build and assign a domain (e.g., `https://online-job-portal.vercel.app`).

### (Optional) Lock down CORS on Render:
Once your Vercel URL is live, you can update `CORS_ORIGINS` in your Render Web Service environment variables to:
`https://your-app.vercel.app` (or keep `*` to allow all origins).

---

## 4. Run Locally

### Backend:
```bash
cd backend
mvn spring-boot:run
```
*(The backend connects directly to your TiDB Cloud database on port 8080).*

### Frontend:
```bash
cd frontend
npm install
npm run dev
```
*(Opens at http://localhost:5173)*

### Seed / Demo Credentials:
- **Employer**: `employer@demo.com` / `demo123`
- **Job Seeker**: `seeker@demo.com` / `demo123`

