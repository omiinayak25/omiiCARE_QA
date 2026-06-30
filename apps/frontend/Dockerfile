# =============================================================================
# omiiCARE_QA frontend — multi-stage production image.
# Stage 1 builds the Vite bundle; stage 2 serves it with nginx. Built from the
# frontend directory:
#   docker build -f apps/frontend/Dockerfile -t omiicare-qa-frontend apps/frontend
# =============================================================================

# --- Build stage ---
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY . .
RUN npm run build

# --- Runtime stage ---
FROM nginx:1.27-alpine AS runtime
# SPA routing: fall back to index.html for client-side routes.
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:80/ || exit 1
