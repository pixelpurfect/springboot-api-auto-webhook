# springboot-api-auto-webhook
Bajaj Finserv Health – Programming Challenge SRM (25th-30th April’25)
# Spring Boot Auto Webhook Submission 🚀

This Spring Boot app automatically:
- Hits the `/generateWebhook` API on startup
- Processes assigned user-follow data
- Sends the correct response to the provided webhook
- Retries failed webhook POST up to 4 times
- Includes JWT token in Authorization header

### ✅ Technologies
- Java 17
- Spring Boot 3.x
- WebClient
- Retry logic with backoff
- Docker

### 🔧 How to Build

```bash
./mvnw clean package
