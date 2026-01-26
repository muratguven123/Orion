# 🌟 Orion - Modern Bankacılık Uygulaması

Orion, Spring Boot 3.4.12 ve Java 17 ile geliştirilmiş, güvenli ve ölçeklenebilir bir bankacılık uygulamasıdır. JWT tabanlı kimlik doğrulama, çoklu oturum açma yöntemleri ve gerçek zamanlı bildirim özellikleri sunar.

## 📋 İçindekiler

- [Özellikler](#-özellikler)
- [Teknoloji Stack](#-teknoloji-stack)
- [Proje Yapısı](#-proje-yapısı)
- [Kurulum](#-kurulum)
- [Yapılandırma](#-yapılandırma)
- [API Dokümantasyonu](#-api-dokümantasyonu)
- [Domain Yapısı](#-domain-yapısı)
- [Güvenlik](#-güvenlik)
- [Test](#-test)
- [Katkıda Bulunma](#-katkıda-bulunma)

## ✨ Özellikler

### 🔐 Kimlik Doğrulama ve Yetkilendirme
- **Çoklu Oturum Açma Desteği**
  - Email ve şifre ile giriş
  - SMS/OTP tabanlı giriş
- **JWT Token Yönetimi**
  - Access Token ve Refresh Token desteği
  - Güvenli token yenileme mekanizması
- **Role-Based Access Control (RBAC)**
  - USER, ADMIN, MODERATOR rolleri
  - Method seviyesinde yetkilendirme

### 💰 Hesap Yönetimi
- **Çoklu Hesap Türü Desteği**
  - Vadesiz Hesap (CHECKING)
  - Vadeli Hesap (DEPOSIT)
  - Yatırım Hesabı (INVESTMENT)
  - Tasarruf Hesabı (SAVINGS)
- **Hesap İşlemleri**
  - Hesap oluşturma ve güncelleme
  - Hesap durumu yönetimi (ACTIVE, SUSPENDED, CLOSED)
  - Para transferi ve işlem geçmişi

### 💳 Ödeme Sistemi
- Güvenli ödeme işlemleri
- Transaction yönetimi
- Asenkron ödeme işlemleri

### 📢 Bildirim Sistemi
- **Çoklu Kanal Desteği**
  - Email bildirimleri (SMTP)
  - SMS bildirimleri (Telegram Bot entegrasyonu)
- **Event-Driven Architecture**
  - Kullanıcı kayıt bildirimleri
  - OTP gönderimi
  - Hesap işlem bildirimleri
  - Ödeme bildirimleri

### 📊 API Dokümantasyonu
- Swagger/OpenAPI 3.0 entegrasyonu
- Interaktif API test arayüzü
- Detaylı endpoint dokümantasyonu

## 🛠 Teknoloji Stack

### Backend
- **Java 17** - Programlama dili
- **Spring Boot 3.4.12** - Application framework
- **Spring Security** - Güvenlik ve yetkilendirme
- **Spring Data JPA** - ORM ve veritabanı işlemleri
- **Spring Cloud** - Microservice altyapısı

### Veritabanı
- **PostgreSQL** - İlişkisel veritabanı
- **Hibernate** - ORM provider

### Güvenlik
- **JJWT 0.12.5** - JWT token yönetimi
- **BCrypt** - Şifre hashleme
- **Spring Security** - Kimlik doğrulama ve yetkilendirme

### Bildirim ve İletişim
- **Telegram Bots API 6.9.7.1** - SMS bildirimleri için
- **Spring Mail** - Email gönderimi
- **JavaMailSender** - SMTP desteği

### Geliştirme Araçları
- **Lombok** - Boilerplate kod azaltma
- **Spring DevTools** - Hot reload
- **Spring Boot Actuator** - Uygulama metrikleri ve health check
- **Maven** - Dependency yönetimi

### Test
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework
- **Spring Test** - Integration testing
- **Spring RestDocs** - API dokümantasyonu

### API Dokümantasyonu
- **SpringDoc OpenAPI 2.8.8** - Swagger UI
- **AsciiDoctor** - REST docs

## 📁 Proje Yapısı

```
orion/
├── src/
│   ├── main/
│   │   ├── java/org/murat/orion/
│   │   │   ├── AuthDomain/              # Kimlik doğrulama ve yetkilendirme
│   │   │   │   ├── Config/              # JWT, Security yapılandırması
│   │   │   │   ├── Controller/          # Auth API endpoints
│   │   │   │   ├── Service/             # Auth business logic
│   │   │   │   ├── Entity/              # User, OtpCode entities
│   │   │   │   ├── Repository/          # Data access layer
│   │   │   │   ├── Dto/                 # Request/Response DTOs
│   │   │   │   └── Mapper/              # Entity-DTO mapping
│   │   │   │
│   │   │   ├── AccountDomain/           # Hesap yönetimi
│   │   │   │   ├── Controller/          # Account API endpoints
│   │   │   │   ├── Service/             # Account business logic
│   │   │   │   ├── Entity/              # Account entities
│   │   │   │   ├── Repository/          # Data access layer
│   │   │   │   └── Dto/                 # Account DTOs
│   │   │   │
│   │   │   ├── Payment/                 # Ödeme sistemi
│   │   │   │   ├── Controller/          # Payment endpoints
│   │   │   │   ├── Service/             # Payment processing
│   │   │   │   ├── Entity/              # Payment entities
│   │   │   │   └── Repository/          # Payment data access
│   │   │   │
│   │   │   ├── Notification/            # Bildirim sistemi
│   │   │   │   ├── Service/             # Email, SMS servisleri
│   │   │   │   ├── Entity/              # Notification entities
│   │   │   │   ├── Events/              # Event publishers
│   │   │   │   └── Repository/          # Notification data
│   │   │   │
│   │   │   └── OrionApplication.java   # Ana uygulama sınıfı
│   │   │
│   │   └── resources/
│   │       └── application.properties   # Uygulama yapılandırması
│   │
│   └── test/                            # Test dosyaları
│       └── java/org/murat/orion/
│
├── .env.example                         # Ortam değişkenleri şablonu
├── pom.xml                              # Maven yapılandırması
└── README.md                            # Bu dosya
```

## 🚀 Kurulum

### Gereksinimler

- Java 17 veya üzeri
- Maven 3.8+
- PostgreSQL 14+
- (Opsiyonel) Telegram Bot Token (SMS bildirimleri için)
- (Opsiyonel) SMTP sunucu erişimi (Email bildirimleri için)

### Adım Adım Kurulum

#### 1. Projeyi Klonlayın

```bash
git clone https://github.com/muratguven123/Orion.git
cd Orion
```

#### 2. Veritabanını Oluşturun

PostgreSQL'de yeni bir veritabanı oluşturun:

```sql
CREATE DATABASE orion_db;

-- Schema'ları oluşturun
CREATE SCHEMA identity;
CREATE SCHEMA account;
CREATE SCHEMA payment;
CREATE SCHEMA notification;
```

#### 3. Ortam Değişkenlerini Yapılandırın

`.env.example` dosyasını `.env` olarak kopyalayın ve değerleri doldurun:

```bash
cp .env.example .env
```

`.env` dosyasını düzenleyin:

```properties
# Veritabanı Yapılandırması
DB_URL=jdbc:postgresql://localhost:5432/orion_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT Yapılandırması
JWT_SECRET=your-secret-key-min-256-bit
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Telegram Bot Yapılandırması (Opsiyonel)
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_BOT_USERNAME=your_bot_username
TELEGRAM_CHAT_ID=your_chat_id

# Email Yapılandırması (Opsiyonel)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
MAIL_FROM=noreply@orion.com
```

#### 4. Projeyi Derleyin

```bash
mvn clean install
```

#### 5. Uygulamayı Çalıştırın

```bash
mvn spring-boot:run
```

Veya JAR dosyası ile:

```bash
java -jar target/Orion-0.0.1-SNAPSHOT.jar
```

Uygulama `http://localhost:8080` adresinde çalışmaya başlayacaktır.

## ⚙️ Yapılandırma

### application.properties

```properties
# Server yapılandırması
server.port=8080

# Veritabanı yapılandırması
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.default_schema=public

# JWT yapılandırması
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
jwt.refresh-expiration=${JWT_REFRESH_EXPIRATION}

# Telegram yapılandırması
telegram.bot.token=${TELEGRAM_BOT_TOKEN}
telegram.bot.username=${TELEGRAM_BOT_USERNAME}
telegram.chat.id=${TELEGRAM_CHAT_ID}

# Mail yapılandırması
spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Güvenlik Yapılandırması

Uygulama aşağıdaki endpoint'leri public olarak sunar:
- `/api/auth/**` - Kimlik doğrulama işlemleri
- `/swagger-ui/**` - API dokümantasyonu
- `/v3/api-docs/**` - OpenAPI spesifikasyonu

Diğer tüm endpoint'ler JWT token ile koruma altındadır.

## 📖 API Dokümantasyonu

### Swagger UI

Uygulama çalıştıktan sonra Swagger UI'a aşağıdaki adresten erişebilirsiniz:

```
http://localhost:8080/swagger-ui.html
```

### Ana Endpoint Grupları

#### 1. Authentication (`/api/auth`)

**Kayıt Ol**
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "phoneNumber": "+905551234567",
  "firstName": "Ahmet",
  "lastName": "Yılmaz"
}
```

**Email ile Giriş**
```http
POST /api/auth/login/email
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**SMS/OTP Gönder**
```http
POST /api/auth/send-otp
Content-Type: application/json

{
  "phoneNumber": "+905551234567"
}
```

**OTP Doğrula ve Giriş Yap**
```http
POST /api/auth/verify-otp
Content-Type: application/json

{
  "phoneNumber": "+905551234567",
  "otpCode": "123456"
}
```

**Token Yenile**
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

#### 2. Account Management (`/api/accounts`)

**Hesap Oluştur**
```http
POST /api/accounts
Authorization: Bearer {token}
Content-Type: application/json

{
  "accountType": "CHECKING",
  "currency": "TRY",
  "initialBalance": 1000.00
}
```

**Kullanıcının Hesaplarını Listele**
```http
GET /api/accounts
Authorization: Bearer {token}
```

**Hesap Detayı**
```http
GET /api/accounts/{accountId}
Authorization: Bearer {token}
```

**Hesap Güncelle**
```http
PUT /api/accounts/{accountId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "accountName": "Ana Hesabım",
  "status": "ACTIVE"
}
```

**Hesap Kapat**
```http
DELETE /api/accounts/{accountId}
Authorization: Bearer {token}
```

#### 3. Payment (`/api/payments`)

**Ödeme Yap**
```http
POST /api/payments
Authorization: Bearer {token}
Content-Type: application/json

{
  "fromAccountId": "uuid-here",
  "toAccountId": "uuid-here",
  "amount": 500.00,
  "description": "Ödeme açıklaması"
}
```

**Ödeme Geçmişi**
```http
GET /api/payments/history
Authorization: Bearer {token}
```

## 🏗 Domain Yapısı

### AuthDomain (Kimlik Doğrulama)

**Temel Sınıflar:**
- `User` - Kullanıcı entity'si, Spring Security UserDetails implementasyonu
- `OtpCode` - OTP kodları için entity
- `JwtService` - JWT token oluşturma ve doğrulama
- `SecurityConfig` - Spring Security yapılandırması
- `LoginStrategy` - Strategy pattern ile çoklu login desteği
  - `EmailLoginStrategy` - Email/password ile giriş
  - `SmsLoginStrategy` - SMS/OTP ile giriş

**Özellikler:**
- BCrypt ile şifre hashleme
- JWT tabanlı stateless kimlik doğrulama
- OTP ile iki faktörlü doğrulama
- Refresh token mekanizması

### AccountDomain (Hesap Yönetimi)

**Temel Sınıflar:**
- `Account` - Banka hesabı entity'si
- `AccountType` - Hesap türleri enum (CHECKING, DEPOSIT, INVESTMENT, SAVINGS)
- `AccountStatus` - Hesap durumları (ACTIVE, SUSPENDED, CLOSED)
- `AccountService` - Hesap işlemleri business logic

**Özellikler:**
- Çoklu hesap türü desteği
- Hesap durumu yönetimi
- UUID tabanlı hesap numaraları
- Event-driven bildirimler

### Payment (Ödeme Sistemi)

**Temel Sınıflar:**
- `Payment` - Ödeme entity'si
- `PaymentService` - Ödeme işlemleri
- `PaymentController` - Payment API endpoints

**Özellikler:**
- Transaction yönetimi
- Asenkron ödeme işlemleri
- Ödeme geçmişi
- Güvenli para transferi

### Notification (Bildirim Sistemi)

**Temel Sınıflar:**
- `NotificationService` - Email gönderim servisi
- `TelegramBotService` - SMS/Telegram bildirim servisi
- `Notification` - Bildirim entity'si
- `NotificationType` - Bildirim türleri

**Event Yapısı:**
- `UserRegisteredEvent` - Kullanıcı kayıt bildirimi
- `OtpSentEvent` - OTP gönderim bildirimi
- `AccountCreatedEvent` - Hesap oluşturma bildirimi
- `PaymentProcessedEvent` - Ödeme işlem bildirimi

**Özellikler:**
- Asenkron bildirim gönderimi
- Email template desteği
- Telegram bot entegrasyonu
- Event-driven architecture

## 🔒 Güvenlik

### JWT Token Yapısı

```json
{
  "sub": "user@example.com",
  "userId": "123",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Token Kullanımı

Her istekte `Authorization` header'ı ile token gönderilmelidir:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Güvenlik Özellikleri

- ✅ JWT tabanlı stateless authentication
- ✅ BCrypt ile şifre hashleme
- ✅ CSRF koruması
- ✅ CORS yapılandırması
- ✅ Rate limiting (Opsiyonel)
- ✅ SQL Injection koruması (JPA/Hibernate)
- ✅ XSS koruması
- ✅ Secure headers

### OTP Güvenliği

- OTP kodları 5 dakika geçerlidir
- Her telefon numarası için aynı anda tek OTP geçerlidir
- OTP kodları SecureRandom ile üretilir
- Kullanılan OTP kodları otomatik olarak invalid edilir

## 🧪 Test

### Unit Test Çalıştırma

```bash
mvn test
```

### Integration Test

```bash
mvn verify
```

### Test Coverage

```bash
mvn clean test jacoco:report
```

Coverage raporu `target/site/jacoco/index.html` adresinde oluşturulur.

### Test Yapısı

- **Unit Tests**: Business logic testleri
- **Integration Tests**: API endpoint testleri
- **Repository Tests**: Database işlem testleri
- **Security Tests**: Auth ve authorization testleri

## 📊 Monitoring ve Health Check

### Actuator Endpoints

Uygulama Spring Boot Actuator ile health check ve monitoring desteği sunar:

```
GET /actuator/health        # Uygulama sağlık durumu
GET /actuator/info          # Uygulama bilgileri
GET /actuator/metrics       # Metrikler
```

## 🚀 Production Deployment

### Docker ile Deployment

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/Orion-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t orion-app .
docker run -p 8080:8080 --env-file .env orion-app
```

### Production Checklist

- [ ] `.env` dosyasını production değerleri ile güncelleyin
- [ ] JWT secret key'i güçlü bir değer ile değiştirin
- [ ] Veritabanı connection pool ayarlarını optimize edin
- [ ] HTTPS/SSL sertifikası yapılandırın
- [ ] Rate limiting ekleyin
- [ ] Logging seviyesini production için ayarlayın
- [ ] Actuator endpoint'lerini güvence altına alın
- [ ] Database backup stratejisi oluşturun

## 🤝 Katkıda Bulunma

1. Bu repository'yi fork edin
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Branch'inizi push edin (`git push origin feature/AmazingFeature`)
5. Pull Request oluşturun

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 👤 İletişim

Murat Güven - [@muratguven123](https://github.com/muratguven123)

Proje Linki: [https://github.com/muratguven123/Orion](https://github.com/muratguven123/Orion)

## 🙏 Teşekkürler

- [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL](https://www.postgresql.org/)
- [JWT](https://jwt.io/)
- [Telegram Bots API](https://github.com/rubenlagus/TelegramBots)
- [Lombok](https://projectlombok.org/)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

⭐️ Bu projeyi beğendiyseniz yıldız vermeyi unutmayın!
