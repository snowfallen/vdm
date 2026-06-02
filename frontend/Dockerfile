# ============================================================
# Angular — multi-stage build.
#
# Stage 1 (build): Node компілює Angular → dist/
# Stage 2 (serve): Nginx роздає статику + SPA fallback
#
# Nginx краще ніж http-server для прода:
# - менший образ (~20MB vs ~200MB)
# - нормальний SPA fallback (try_files → index.html)
# - gzip compression з коробки
# ============================================================

# --- Stage 1: Build ---
FROM node:20-alpine AS build

WORKDIR /app

# Копіюємо package files окремо → кешування шару npm install
COPY package*.json ./
RUN npm ci --prefer-offline

COPY . .

# --configuration production включає optimization, AOT, tree-shaking
RUN npm run build -- --configuration production

# --- Stage 2: Serve ---
FROM nginx:1.25-alpine

# Наш nginx конфіг (SPA fallback + gzip)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Копіюємо зібрану статику
# Шлях dist/vdm/browser — стандарт Angular 17+, змінити якщо інше
COPY --from=build /app/dist/vdm/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
