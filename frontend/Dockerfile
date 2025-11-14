# 1. Construir con Node 20
FROM node:20-alpine AS build
WORKDIR /app

# Instala TODO (incluye devDependencies)
COPY package*.json .npmrc ./
RUN npm ci --ignore-scripts

COPY . .

# Usa npx → no necesitas CLI global
RUN npx quasar build

# 2. Servir con Nginx slim (25 MB)
FROM nginx:alpine-slim
COPY --from=build /app/dist/spa /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf