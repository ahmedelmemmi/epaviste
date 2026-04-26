# Free Deployment Guide — Epaviste

This guide walks you through deploying the full stack for **free**:

| Service | Platform | Cost |
|---------|----------|------|
| Frontend (Angular) | [Render](https://render.com) static site | ✅ Free |
| Backend (Spring Boot) | [Koyeb](https://koyeb.com) free nano instance | ✅ Free |
| Database (PostgreSQL) | [Neon.tech](https://neon.tech) serverless DB | ✅ Free |

---

## Step 1 — Create a free PostgreSQL database on Neon

1. Go to [https://neon.tech](https://neon.tech) and sign up (no credit card needed).
2. Create a new **Project** (e.g. `epaviste`).
3. Neon auto-creates a database. In the **Connection Details** panel:
   - Select the **JDBC** connection string format.
   - Copy the full string — it looks like:
     ```
     jdbc:postgresql://ep-xxxx.us-east-2.aws.neon.tech/epaviste?sslmode=require
     ```
4. Also note the **Username** and **Password** shown in that panel.

Keep these three values — you will need them in Step 2.

---

## Step 2 — Deploy the backend on Koyeb

1. Go to [https://app.koyeb.com](https://app.koyeb.com) and sign up (no credit card needed for the free tier).
2. Click **Create Service → GitHub**.
3. Connect your GitHub account and select the `ahmedelmemmi/epaviste` repository.
4. Set the following options:
   - **Branch**: `main` (or your deployment branch)
   - **Build type**: Dockerfile
   - **Dockerfile path**: `backend/Dockerfile`
   - **Docker build context**: `backend`
5. Under **Instance**, choose **Free (nano)**.
6. Under **Ports**, add port `8080` (HTTP).
7. Under **Environment variables**, add:

   | Key | Value |
   |-----|-------|
   | `SPRING_DATASOURCE_URL` | The JDBC URL from Neon (Step 1) |
   | `SPRING_DATASOURCE_USERNAME` | Neon username |
   | `SPRING_DATASOURCE_PASSWORD` | Neon password |
   | `APP_JWT_SECRET` | A random string of at least 32 characters |
   | `APP_JWT_EXPIRATION` | `86400000` |
   | `APP_CORS_ALLOWED_ORIGINS` | `https://epaviste-frontend.onrender.com` |
   | `APP_COMMISSION_RATE` | `0.10` |
   | `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
   | `SPRING_PROFILES_ACTIVE` | `prod` |

   > **Tip**: Mark `SPRING_DATASOURCE_PASSWORD` and `APP_JWT_SECRET` as **Secret** in Koyeb.

8. Click **Deploy**. Wait for the service to go **Healthy** (first build takes ~5 min).
9. Copy your service URL — it will look like:
   ```
   https://epaviste-backend-<your-org>.koyeb.app
   ```

---

## Step 3 — Update render.yaml with your Koyeb URL

Open `render.yaml` in this repository and replace `KOYEB_APP_URL` with the hostname from Step 2 (without `https://`):

```yaml
routes:
  - type: rewrite
    source: /api/*
    destination: https://epaviste-backend-<your-org>.koyeb.app/api/:splat
```

Commit and push this change.

---

## Step 4 — Deploy the frontend on Render

1. Go to [https://render.com](https://render.com) and sign in.
2. Click **New → Blueprint**.
3. Connect your GitHub account and select `ahmedelmemmi/epaviste`.
4. Render reads `render.yaml` automatically and creates the **epaviste-frontend** static site.
5. Click **Apply**.
6. Wait for the build to finish. Your frontend will be live at:
   ```
   https://epaviste-frontend.onrender.com
   ```

---

## Summary of URLs

| Service | URL |
|---------|-----|
| Frontend | `https://epaviste-frontend.onrender.com` |
| Backend | `https://epaviste-backend-<org>.koyeb.app` |
| API (via Render proxy) | `https://epaviste-frontend.onrender.com/api/` |

---

## Troubleshooting

### Backend does not start on Koyeb
- Spring Boot needs up to 60 seconds to start on the free nano instance.
- Check the **Koyeb logs** tab for errors.
- Verify all environment variables (especially `SPRING_DATASOURCE_URL`) are set correctly.

### CORS errors in the browser
- Make sure `APP_CORS_ALLOWED_ORIGINS` on Koyeb is exactly `https://epaviste-frontend.onrender.com` (no trailing slash).

### Neon DB connection refused
- Ensure the JDBC URL starts with `jdbc:postgresql://` not `postgresql://`.
- The URL must include `?sslmode=require`.

### Render rewrite not working
- Double-check that `KOYEB_APP_URL` in `render.yaml` was replaced with the actual Koyeb hostname.
- Trigger a new Render deploy after updating `render.yaml`.
