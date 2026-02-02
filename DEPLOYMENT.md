# Deployment Guide - Dungeon Board

This guide will help you deploy Dungeon Board to production.

## Architecture

- **Frontend**: Vercel (Vue.js + Vite)
- **Backend**: Render, Railway, or similar (Spring Boot + Java)

## Step 1: Deploy Backend to Render

### Option A: Using Render (Recommended - Free Tier Available)

1. **Create a Render account**
   - Go to https://render.com and sign up

2. **Create a new Web Service**
   - Click "New +"
   - Select "Web Service"
   - Connect your GitHub repository

3. **Configure the service**
   ```
   Name: dungeon-board-backend
   Environment: Docker
   Build Command: (leave empty for Docker)
   Start Command: (leave empty for Docker)

   Environment Variables:
   - PORT: 8080
   - JWT_SECRET: (generate a strong secret - use: openssl rand -base64 32)
   - CORS_ORIGINS: https://your-frontend-url.vercel.app
   - H2_CONSOLE_ENABLED: false
   - LOG_LEVEL: INFO
   ```

4. **Deploy**
   - Click "Create Web Service"
   - Render will build and deploy your backend
   - Get your backend URL (e.g., https://dungeon-board-backend.onrender.com)

### Option B: Using Railway

1. **Create a Railway account**
   - Go to https://railway.app and sign up

2. **Create a new project**
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your repository

3. **Configure**
   - Railway will auto-detect Spring Boot
   - Add environment variables:
     ```
     JWT_SECRET: your-secret-here
     CORS_ORIGINS: https://your-frontend-url.vercel.app
     H2_CONSOLE_ENABLED: false
     ```

4. **Get your backend URL**
   - Railway will provide a URL like https://your-project.up.railway.app

### Option C: Using Fly.io

1. **Install Fly CLI**
   ```bash
   curl -L https://fly.io/install.sh | sh
   ```

2. **Login and deploy**
   ```bash
   cd backend
   fly launch
   fly deploy
   ```

3. **Set environment variables**
   ```bash
   fly secrets set JWT_SECRET=your-secret-here
   fly secrets set CORS_ORIGINS=https://your-frontend-url.vercel.app
   ```

## Step 2: Deploy Frontend to Vercel

1. **Install Vercel CLI**
   ```bash
   npm install -g vercel
   ```

2. **Login to Vercel**
   ```bash
   vercel login
   ```

3. **Deploy from frontend directory**
   ```bash
   cd frontend
   vercel
   ```

4. **Follow the prompts**
   - Set up and deploy? **Y**
   - Which scope? (select your account)
   - Link to existing project? **N**
   - Project name: **dungeon-board**
   - In which directory is your code? **.**
   - Want to override settings? **N**

5. **Add environment variables in Vercel Dashboard**
   - Go to https://vercel.com/dashboard
   - Select your project
   - Go to Settings > Environment Variables
   - Add:
     ```
     VITE_API_URL=https://your-backend-url.onrender.com/api
     ```

6. **Redeploy if needed**
   ```bash
   vercel --prod
   ```

## Step 3: Update Backend CORS

After deploying your frontend to Vercel:

1. Go to your backend dashboard (Render/Railway/etc.)
2. Update the `CORS_ORIGINS` environment variable:
   ```
   https://your-project.vercel.app
   ```
3. Redeploy the backend

## Step 4: Test Your Deployment

1. Visit your Vercel frontend URL
2. Register a new account
3. Create a room
4. Test all game features

## Free Tier Options

| Service | Free Tier | Backend URL |
|---------|-----------|-------------|
| **Render** | Yes (750 hours/month) | `https://xxx.onrender.com` |
| **Railway** | $5 free credit/month | `https://xxx.up.railway.app` |
| **Fly.io** | Free allowance | `https://xxx.fly.dev` |
| **Vercel** | Yes (frontend only) | `https://xxx.vercel.app` |

## Environment Variables Reference

### Backend (Render/Railway)
```
PORT=8080
JWT_SECRET=your-strong-secret-key-here
CORS_ORIGINS=https://your-frontend.vercel.app
H2_CONSOLE_ENABLED=false
LOG_LEVEL=INFO
```

### Frontend (Vercel)
```
VITE_API_URL=https://your-backend-url.onrender.com/api
```

## Troubleshooting

### WebSocket Connection Issues

If WebSocket fails to connect:

1. **Check the URL format**:
   - Development: `ws://localhost:8080/ws/game`
   - Production: `wss://your-backend.onrender.com/ws/game`

2. **Verify CORS is properly configured**:
   - Your backend `CORS_ORIGINS` must include your Vercel URL

3. **Check browser console**:
   - Look for WebSocket connection errors
   - Verify the WSS URL is correct

### Backend Sleeping

Free tiers (Render, Railway) may spin down your backend when inactive:

1. **First request may be slow** (cold start)
2. **Solution**: Use a monitoring service like UptimeRobot to ping your backend every 5 minutes

### H2 Database Note

⚠️ **Important**: The current setup uses H2 in-memory database, which means:
- All data is lost when the backend restarts
- This is fine for testing/demo purposes
- For production, consider adding PostgreSQL:

```properties
# For PostgreSQL (recommended for production)
spring.datasource.url=jdbc:postgresql://host:port/database
spring.datasource.username=your-username
spring.datasource.password=your-password
```

## Custom Domain Setup (Optional)

### Vercel Frontend
1. Go to Vercel Dashboard > Your Project > Settings > Domains
2. Add your custom domain
3. Update DNS records as instructed

### Backend (Render example)
1. Go to Render Dashboard > Your Service > Settings > Custom Domains
2. Add your domain and update DNS

## Cost Estimate

**Free Tier Deployment:**
- Frontend (Vercel): $0/month
- Backend (Render): $0/month (750 hours)
- **Total: $0/month**

**Note**: Free tiers have limitations:
- Render spins down after 15 minutes of inactivity
- Railway has $5 free credit (lasts ~1 month for small apps)
- Consider upgrading if you need 24/7 uptime

## Monitoring

### Set up UptimeRobot (Free)
1. Go to https://uptimerobot.com
2. Add your backend URL
3. Set to check every 5 minutes
4. This prevents your backend from spinning down on free tiers

## Next Steps

1. ✅ Deploy backend to Render/Railway
2. ✅ Deploy frontend to Vercel
3. ✅ Update CORS settings
4. ✅ Test the complete application
5. ✅ Set up monitoring
6. ✅ Share your game URL with friends!
