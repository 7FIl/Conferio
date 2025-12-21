# Conferio UI (React)

## Prerequisites
- Node.js 18+
- Backend running at http://localhost:8080

## Setup
```bash
# From conferio-ui folder
npm install
npm run dev
```

Open http://localhost:3000
- Login page uses sample user: `admin/password123`
- Sessions page requires login and calls backend APIs

## Configuration
- API base URL: `.env.development` `VITE_API_BASE_URL=http://localhost:8080`
- CORS is enabled in backend for `http://localhost:3000`

## Build
```bash
npm run build
npm run preview
```
