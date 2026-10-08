# VoC Backend (Spring Boot 2.7 / Java 8 / MySQL 8)

## Run
```
mysql: create user/db or let createDatabaseIfNotExist do it
export DB_USER=root DB_PASS=root VOC_PUBLIC_URL=http://localhost:8081
mvn spring-boot:run
```
Flyway creates the schema. Dev admin: `admin / admin123` (HTTP Basic) - remove the seeder for production.

## Quick test
```
# 1 channel + frequency rule + api key (as admin)
curl -u admin:admin123 -H 'Content-Type: application/json' -X POST localhost:8080/api/admin/channels -d '{"code":"SHOP_SMS","name":"Shops","deliveryMethod":"SMS"}'
curl -u admin:admin123 -H 'Content-Type: application/json' -X POST localhost:8080/api/admin/frequency-rules -d '{"name":"30-day cap","days":30,"scope":"ALL_CHANNELS","enabled":true}'
curl -u admin:admin123 -X POST 'localhost:8080/api/admin/api-keys?name=mobile-app'
# 2 survey -> submit -> approve -> activate (see /api/admin/surveys)
# 3 digital channel
curl -H 'X-API-Key: voc_...' -H 'Content-Type: application/json' -X POST localhost:8080/api/v1/digital/survey-requests -d '{"msisdn":"962790000000","channelCode":"SHOP_SMS"}'
```

## Endpoints
| Area | Path |
|---|---|
| Admin (Basic auth, roles ADMIN/DESIGNER/APPROVER) | `/api/admin/surveys`, `/channels`, `/frequency-rules`, `/sampling-rules`, `/api-keys`, `/jobs`, `/audit-logs`, `/reports/survey/{id}/summary` |
| Digital/external (X-API-Key) | `POST /api/v1/digital/survey-requests`, `/api/v1/triggers`, `/api/v1/digital/responses` |
| Customer link page | `GET/POST /api/public/surveys/{token}[/responses]` |
