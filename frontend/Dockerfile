# 1. Construir la app
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json .npmrc ./
RUN npm install --ignore-scripts
COPY . .
RUN npm install -g @quasar/cli && npx quasar prepare && quasar build

# 2. Servir con servidor pequeño
FROM nginx:alpine
COPY --from=build /app/dist/spa /usr/share/nginx/html