# syntax=docker/dockerfile:1
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build \
    && mkdir -p /static \
    && if [ -d dist/frontend/browser ]; then cp -r dist/frontend/browser/. /static/; else cp -r dist/frontend/. /static/; fi

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /static /usr/share/nginx/html
EXPOSE 80
