# Orion Microservices - Postman Test Rehberi

Bu rehber, Orion mikroservis mimarisindeki tum endpoint'lerin Postman ile nasil test edilecegini adim adim anlatir.

## Mimari Genel Bakis

```
Postman Istekleri
       |
       v
+------------------+
|   API Gateway    |  Port: 8222  (Tek giris noktasi)
+--------+---------+
         |  JWT dogrulama + X-User-Id header ekleme
         |
         +---> Auth Service     (Port: 9000)  /api/auth/**      -> JWT gerektirmez
         +---> Account Service  (Port: 8081)  /api/accounts/**   -> JWT gerektirir
         +---> Payment Service  (Port: 8082)  /api/payments/**   -> JWT gerektirir
         +---> Invest Service   (Port: 8083)  /api/invest/**     -> JWT gerektirir
```

### Servis Portlari

| Servis | Port | Aciklama |
|---|---|---|
| API Gateway | 8222 | Tum istekler buradan gecer |
| Auth Service | 9000 | Kayit ve giris islemleri |
| Account Service | 8081 | Hesap yonetimi |
| Payment Service | 8082 | Odeme islemleri |
| Invest Service | 8083 | Yatirim islemleri |
| Discovery Server (Eureka) | 8761 | Servis kesfetme |
| Config Server | 8888 | Merkezi konfigürasyon |
| PostgreSQL | 5432 | Veritabani |
| RabbitMQ | 5672 / 15672 | Mesaj kuyrugu / Yonetim paneli |

## Postman Ortam Ayarlari

Postman'de bir **Environment** olusturun:

| Degisken | Deger |
|---|---|
| `base_url` | `http://localhost:8222` |
| `token` | _(login'den sonra doldurulacak)_ |

---

## 1. AUTH SERVICE (JWT Gerektirmez)

Bu endpoint'ler herkese aciktir, token gerekmez.

### 1.1 Kullanici Kaydi (Register)

```
POST {{base_url}}/api/auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
    "firstName": "Murat",
    "lastName": "Guven",
    "email": "murat@example.com",
    "password": "12345678",
    "phoneNumber": "+905551234567"
}
```

**Validasyon Kurallari:**
- `firstName`: Zorunlu, 2-50 karakter
- `lastName`: Zorunlu, 2-50 karakter
- `email`: Zorunlu, gecerli email formatinda
- `password`: Zorunlu, en az 8 karakter
- `phoneNumber`: Opsiyonel, +XXXXXXXXXXXX formatinda (10-15 rakam)

**Basarili Response (201 Created):**
```json
{
    "userId": 1,
    "email": "murat@example.com",
    "phoneNumber": "+905551234567",
    "firstName": "Murat",
    "lastName": "Guven",
    "role": "USER",
    "status": "ACTIVE",
    "createdAt": "2025-01-01T12:00:00",
    "message": "Kayit basarili",
    "requiresVerification": false,
    "verificationCodeSent": false
}
```

---

### 1.2 Email ile Giris (Login)

```
POST {{base_url}}/api/auth/login/email
Content-Type: application/json
```

**Request Body:**
```json
{
    "email": "murat@example.com",
    "password": "12345678"
}
```

**Basarili Response (200 OK):**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userId": 1,
    "email": "murat@example.com",
    "firstName": "Murat",
    "lastName": "Guven",
    "role": "USER",
    "phoneNumber": "+905551234567",
    "loginTime": "2025-01-01T12:00:00",
    "status": "SUCCESS"
}
```

> **ONEMLI:** Response'daki `accessToken` degerini kopyalayip Postman environment degiskeni `token` olarak kaydedin. Diger tum protected endpoint'lerde bu token kullanilacak.

---

### 1.3 SMS OTP Gonderme

```
POST {{base_url}}/api/auth/login/sms/send-otp
Content-Type: application/json
```

**Request Body:**
```json
{
    "phoneNumber": "+905551234567"
}
```

---

### 1.4 SMS OTP Dogrulama ile Giris

```
POST {{base_url}}/api/auth/login/sms/verify
Content-Type: application/json
```

**Request Body:**
```json
{
    "phoneNumber": "+905551234567",
    "verificationCode": "123456"
}
```

**Validasyon:** `verificationCode` tam 6 haneli olmali.

---

### 1.5 Token Yenileme (Refresh Token)

```
POST {{base_url}}/api/auth/refresh-token
Content-Type: application/json
```

**Request Body:**
```json
{
    "refreshToken": "<login'den gelen refreshToken degeri>"
}
```

---

## 2. ACCOUNT SERVICE (JWT Gerektirir)

Tum Account Service endpoint'leri JWT token gerektirir. Postman'de:
- **Authorization** sekmesi -> Type: **Bearer Token** -> Token: `{{token}}`

API Gateway, JWT'den `X-User-Id` header'ini otomatik olarak cikarip servise ekler.

### 2.1 Hesap Olusturma

```
POST {{base_url}}/api/accounts/create
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body:**
```json
{
    "accountName": "Ana Hesabim",
    "accountType": "CHECKING",
    "currency": "TRY",
    "initialDeposit": 1000.00
}
```

**`accountType` Degerleri:**
- `DEPOSIT` - Vadeli mevduat hesabi
- `INVESTMENT` - Yatirim hesabi
- `CHECKING` - Vadesiz hesap
- `SAVINGS` - Birikim hesabi

**Basarili Response (201 Created):**
```json
{
    "id": 1,
    "accountNumber": "ACC-XXXXXXXXXX",
    "accountName": "Ana Hesabim",
    "accountType": "CHECKING",
    "status": "ACTIVE",
    "balance": 1000.00,
    "currency": "TRY",
    "isActive": true,
    "createdAt": "2025-01-01T12:00:00",
    "updatedAt": "2025-01-01T12:00:00"
}
```

---

### 2.2 Tum Hesaplarimi Listele

```
GET {{base_url}}/api/accounts/my-accounts
Authorization: Bearer {{token}}
```

---

### 2.3 Aktif Hesaplarimi Listele

```
GET {{base_url}}/api/accounts/my-accounts/active
Authorization: Bearer {{token}}
```

---

### 2.4 Hesap ID ile Sorgulama

```
GET {{base_url}}/api/accounts/{accountId}
Authorization: Bearer {{token}}
```

Ornek: `GET {{base_url}}/api/accounts/1`

---

### 2.5 Hesap Numarasi ile Sorgulama

```
GET {{base_url}}/api/accounts/number/{accountNumber}
Authorization: Bearer {{token}}
```

Ornek: `GET {{base_url}}/api/accounts/number/ACC-1234567890`

---

### 2.6 Hesap Guncelleme

```
PUT {{base_url}}/api/accounts/{accountId}
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body:**
```json
{
    "accountName": "Guncellenmis Hesap Adi",
    "accountType": "SAVINGS",
    "currency": "USD"
}
```

---

### 2.7 Hesap Deaktive Etme

```
PATCH {{base_url}}/api/accounts/{accountId}/deactivate
Authorization: Bearer {{token}}
```

---

### 2.8 Hesap Aktive Etme

```
PATCH {{base_url}}/api/accounts/{accountId}/activate
Authorization: Bearer {{token}}
```

---

### 2.9 Hesap Silme

```
DELETE {{base_url}}/api/accounts/{accountId}
Authorization: Bearer {{token}}
```

**Response:** 204 No Content

---

### 2.10 Kullanicinin Hesaplari (User ID ile)

```
GET {{base_url}}/api/accounts/user/{userId}
Authorization: Bearer {{token}}
```

---

### 2.11 Internal Debit (Bakiye Dusurme)

```
POST {{base_url}}/api/accounts/internal/debit
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body:**
```json
{
    "userId": 1,
    "amount": 100.00
}
```

---

### 2.12 Internal Credit (Bakiye Ekleme)

```
POST {{base_url}}/api/accounts/internal/credit
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body:**
```json
{
    "userId": 1,
    "amount": 500.00
}
```

---

## 3. INVEST SERVICE (JWT Gerektirir)

### 3.1 Yatirim Alimi (Buy)

```
POST {{base_url}}/api/invest/buy
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body:**
```json
{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 10,
    "type": "stock"
}
```

**`type` Degerleri:**
- `stock` - Hisse senedi
- `gold` - Altin
- `crypto` - Kripto para

**Basarili Response (200 OK):**
```
"Alim islemi basariyla gerceklesti."
```

---

### 3.2 Yatirim Satimi (Sell)

```
POST {{base_url}}/api/invest/sell
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body:**
```json
{
    "userId": 1,
    "symbol": "BTC",
    "quantity": 0.5,
    "type": "crypto"
}
```

**Basarili Response (200 OK):**
```
"Satis islemi basariyla gerceklesti."
```

---

### 3.3 Portfoy Goruntuleme

```
GET {{base_url}}/api/invest/portfolio/{userId}
Authorization: Bearer {{token}}
```

Ornek: `GET {{base_url}}/api/invest/portfolio/1`

---

## 4. PAYMENT SERVICE

Payment Service icin API Gateway'de `/api/payments/**` route'u tanimli, ancak henuz Controller sinifi yazilmamis. Bu servis su an kullanima hazir degil.

---

## 5. NOTIFICATION SERVICE

Notification Service REST endpoint sunmaz. RabbitMQ uzerinden event-driven calisir:
- Kullanici kaydi yapildiginda `UserRegisteredEvent` dinler
- OTP gonderildiginde `OtpSentEvent` dinler
- Hesap borclama yapildiginda `AccountDebitedEvent` dinler

Bu servis Postman ile dogrudan test edilemez.

---

## Test Sirasi (Onerilen Akis)

1. **Register** -> Yeni kullanici olustur
2. **Login** -> accessToken al
3. **Create Account** -> Hesap olustur
4. **Get My Accounts** -> Hesaplari kontrol et
5. **Buy Asset** -> Yatirim yap
6. **Get Portfolio** -> Portfoyu kontrol et
7. **Deactivate Account** -> Hesap kapat

---

## Bilinen Sorunlar

### API Gateway Invest Route Uyumsuzlugu

API Gateway konfigurasyonunda invest service route'u `/api/invests/**` olarak tanimli, ancak InvestController'da `@RequestMapping` `/api/invest` (sonsuz "s" yok). Bu durum Gateway uzerinden isteklerin 404 donmesine yol acabilir.

**Cozum:** API Gateway konfigurasyonundaki route'u `/api/invest/**` olarak guncelle veya InvestController'daki mapping'i `/api/invests` olarak degistir.

### Payment Service Controller Eksik

Payment Service icin veritabani ve konfigürasyon hazir, ancak Controller sinifi henuz yazilmamis.

---

## Ortam Baslatma

### Docker ile
```bash
cd backend-microservices
docker compose up -d
```

### Manuel
```bash
# 1. Config Server (ilk baslamali)
cd config-server/config-server && mvn spring-boot:run

# 2. Discovery Server
cd discovery-server/discovery-server && mvn spring-boot:run

# 3. API Gateway
cd api-gateway/api-gateway && mvn spring-boot:run

# 4. Auth Service
cd auth-service/auth-service && mvn spring-boot:run

# 5. Account Service
cd account-service && mvn spring-boot:run

# 6. Invest Service
cd invest-service/invest-service && mvn spring-boot:run
```

Eureka Dashboard: http://localhost:8761 adresinden tum servislerin ayakta oldugunun dogrulayin.
