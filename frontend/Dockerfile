# ---------- Builder ----------
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
ARG VITE_BASE_URL
ENV VITE_BASE_URL=${VITE_BASE_URL}

# Build the Next.js app
RUN npm run build


# ---------- Production ----------
FROM node:20-alpine

WORKDIR /app

COPY --from=builder /app ./

EXPOSE 3000

CMD ["npm","run" ,"preview --host 0.0.0.0 --port 3000"]




