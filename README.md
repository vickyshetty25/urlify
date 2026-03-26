# URLify — URL Shortener Service

A high-performance URL shortener built with Java 21 and Spring Boot.
Handles redirects in under 1ms using Redis caching with async click analytics.

---

## Features

- Base62 short code generation — 7 chars = 3.5 trillion unique codes, zero collisions
- Custom alias support — create memorable short URLs like `/my-github`
- Redis-cached redirects — under 1ms response on cache hit
- 302 Temporary Redirect — ensures every click is recorded for analytics
- Sliding window rate limiting — max 10 requests per IP per 60 seconds
- Async click analytics — redirect never slowed down by DB writes (@Async)
- URL deactivation — disable any short URL instantly

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.4 |
| PostgreSQL | 16 |
| Redis | 7 |
| Docker + docker-compose | latest |
| Maven | - |

---

## How to Run
```bash
git clone https://github.com/vickyshetty25/urlify.git
cd urlify
docker compose up --build
```

API available at: `http://localhost:8080`

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/urls/shorten | Shorten a URL (with optional custom alias) |
| GET | /{code} | Redirect to original URL |
| GET | /api/urls/{code} | Get URL info and click count |
| DELETE | /api/urls/{code} | Deactivate a short URL |

---

## Sample Request & Response

**Shorten a URL:**
```json
POST /api/urls/shorten
{
    "longUrl": "https://github.com/vickyshetty25",
    "customAlias": "my-github"
}
```

**Response:**
```json
{
    "shortUrl": "http://localhost:8080/my-github",
    "shortCode": "1",
    "longUrl": "https://github.com/vickyshetty25",
    "clickCount": 0,
    "createdAt": "2026-03-23T18:27:21"
}
```

---

## How Redis Caching Works

- First redirect: queries PostgreSQL (~30-50ms), caches result in Redis (24hr TTL)
- Second redirect: returns from Redis in under 1ms — PostgreSQL not touched
- Cache is NOT evicted on redirect — URLs rarely change once created

---

## How Rate Limiting Works

Uses Redis Sorted Sets (sliding window algorithm):
- Key: `ratelimit:{ip_address}`
- Each request adds a timestamp to the sorted set
- Timestamps older than 60 seconds are removed
- If count >= 10 → HTTP 429 Too Many Requests
- More accurate than fixed-window — no boundary exploitation

---

## Why 302 and not 301?

- **301 Permanent**: Browser caches the destination URL forever
  → User never calls my server again → all analytics lost
- **302 Temporary**: Browser always calls my server
  → Every click is recorded → accurate analytics