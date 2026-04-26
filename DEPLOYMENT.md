# Free Deployment Guide — Epaviste

This guide walks you through deploying the full stack for **free** using Render (both frontend and backend) and Neon for the database.

| Service | Platform | Cost |
|---------|----------|------|
| Frontend (Angular) | [Render](https://render.com) static site | ✅ Free |
| Backend (Spring Boot) | [Render](https://render.com) Docker web service | ✅ Free |
| Database (PostgreSQL) | [Neon.tech](https://neon.tech) serverless DB | ✅ Free |

> **Note — free tier cold starts:** Render free web services spin down after 15 minutes of inactivity.
> The first request after a period of inactivity may take **30–50 seconds** while the container restarts.

---

## Step 1 — Create a free PostgreSQL database on Neon

1. Go to [https://neon.tech](https://neon.tech) and sign up (no credit card needed).
2. Create a new **Project** (e.g. `epaviste`).
3. Neon auto-creates a database. In the **Connection Details** panel:
   - Select the **JDBC** connection string format.
   - Copy the full string — it looks like:
     ```
     jdbc:postgresql://ep-xxxx.us-east-2.aws.neon.tech/neondb?sslmode=require
     ```
4. Also note the **Username** and **Password** shown in that panel.

Keep these three values — you will need them in Step 3.

---

## Step 2 — Deploy both services via Render Blueprint

1. Go to [https://render.com](https://render.com) and sign in (or sign up — no credit card needed for free services).
2. Click **New → Blueprint**.
3. Connect your GitHub account and select the `ahmedelmemmi/epaviste` repository.
4. Render reads `render.yaml` automatically and creates two services:
   - **epaviste-backend** — Docker web service (Spring Boot)
   - **epaviste-frontend** — Static site (Angular)
5. Click **Apply** to start the initial deploy.
   - The backend build takes ~5 minutes on the first run.
   - The backend will stay in a **failed/unhealthy** state until you set the secret env vars in Step 3.

---

## Step 3 — Set secret environment variables for the backend

After the Blueprint is applied, open the **epaviste-backend** service in the Render dashboard:

1. Go to **Environment → Environment Variables**.
2. Add (or update) the following variables marked `sync: false` in `render.yaml`:

   | Key | Value |
   |-----|-------|
   | `SPRING_DATASOURCE_URL` | The JDBC URL from Neon (Step 1), e.g. `jdbc:postgresql://ep-xxxx.../neondb?sslmode=require` |
   | `SPRING_DATASOURCE_PASSWORD` | Neon password |
   | `APP_JWT_SECRET` | A random string of **at least 32 characters** |

   > **Tip**: Use Render's **Secret File** or mark each value as a secret to avoid leaking it in logs.

   The following variables are already set by `render.yaml` and do not need to be changed unless your Neon username differs:

   | Key | Default in render.yaml |
   |-----|------------------------|
   | `SPRING_DATASOURCE_USERNAME` | `neondb_owner` |
   | `APP_JWT_EXPIRATION` | `86400000` |
   | `APP_CORS_ALLOWED_ORIGINS` | `https://epaviste-frontend.onrender.com` |
   | `APP_COMMISSION_RATE` | `0.10` |
   | `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
   | `SPRING_PROFILES_ACTIVE` | `prod` |

3. Click **Save Changes** — Render will automatically trigger a new deploy for the backend.

---

## Step 4 — Verify your deployment

Once both services show **Live** in the Render dashboard:

| Service | URL |
|---------|-----|
| Frontend | `https://epaviste-frontend.onrender.com` |
| Backend | `https://epaviste-backend.onrender.com` |
| API (via Render proxy) | `https://epaviste-frontend.onrender.com/api/` |

Open the frontend URL in your browser and confirm the app loads and API calls work.

---

## Troubleshooting

### Backend does not start / stays unhealthy
- Check the **Logs** tab of the **epaviste-backend** service in Render.
- Spring Boot needs up to 60 seconds to start on the free instance — wait for the health check to pass.
- Verify all environment variables (especially `SPRING_DATASOURCE_URL` and `APP_JWT_SECRET`) are set correctly.

### CORS errors in the browser
- Make sure `APP_CORS_ALLOWED_ORIGINS` is exactly `https://epaviste-frontend.onrender.com` (no trailing slash).

### Neon DB connection refused
- Ensure the JDBC URL starts with `jdbc:postgresql://` (not `postgresql://`).
- The URL must include `?sslmode=require`.
- Check that `SPRING_DATASOURCE_USERNAME` matches the username shown in the Neon dashboard.

### First request is very slow (~30–50 s)
- This is normal for Render free-tier services after a period of inactivity. The container needs to cold-start.
- Subsequent requests within the same active session will be fast.

### Render rewrite not proxying to backend
- The `/api/*` → `https://epaviste-backend.onrender.com/api/:splat` rewrite is already configured in `render.yaml`.
- If you renamed the backend service, update the `destination` URL in `render.yaml`, commit, and trigger a new frontend deploy.

