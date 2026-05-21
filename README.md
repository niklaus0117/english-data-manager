# english-data-manager

Backend service for an English learning admin system and app clients.

## Stack

- Java 17
- Spring Boot 3.2.5
- MyBatis-Plus 3.5.7
- MySQL
- Redis
- Kafka
- Maven

## Features

- Admin APIs: login, dashboard, categories, recommendations, videos, subtitles, video content, users, orders, role/menu RBAC, permission groups, system configs, membership packages, file upload
- App APIs: password login, SMS login, home data, video list/detail, favorites, playback records, user profile, permission group info, membership package list, order creation, order query
- Open APIs: payment callback

## Startup

1. Create the MySQL database and run `src/main/resources/db/schema.sql`
2. Copy `src/main/resources/application.example.yml` to `src/main/resources/application.yml`
3. Update the local database, Redis, Kafka, storage, and auth settings
4. Start the app:

```bash
mvn spring-boot:run
```

## Default Accounts

- Admin: `admin / admin123`
- App: `13800000000 / user123456`

## Swagger

- UI: `http://127.0.0.1:8080/swagger-ui/index.html`
- OpenAPI: `http://127.0.0.1:8080/v3/api-docs`

## Notes

- Tokens are stored in Redis
- Video create/update events are sent to Kafka topic `video-changed-topic`
- Payment callback upgrades users to VIP based on the purchased membership package duration
- File upload uses a storage abstraction and currently saves files locally
