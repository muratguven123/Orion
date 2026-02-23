# RabbitMQ Konfigürasyon Rehberi

## Genel Bakış
Auth Service, mikroservis mimarisinde bir **Producer (Gönderici)** olarak yapılandırılmıştır.
Kullanıcı kaydı ve OTP gönderimi gibi olaylar, Spring ApplicationEvent yerine RabbitMQ üzerinden asenkron mesaj olarak yayınlanır.

Auth Service sadece mesaj üretir (publish), kuyruk oluşturmaz. Kuyruklar, mesajları tüketecek olan servisler (örn. notification-service) tarafından tanımlanır.

## Mimari

```
Auth Service (Producer)
    |
    v
[internal.exchange] (TopicExchange)
    |
    |-- notification.auth.registered --> (notification-service tarafından dinlenir)
    |-- notification.auth.otp         --> (notification-service tarafından dinlenir)
```

## Oluşturulan Dosyalar

### 1. RabbitMqConfig.java
**Konum:** `src/main/java/.../AuthDomain/Config/RabbitMqConfig.java`

Sadece `internal.exchange` TopicExchange tanımlar. Kuyruk veya binding oluşturmaz.

#### Exchange
- `internal.exchange` - Merkezi topic exchange (durable)

#### Routing Keys
- `notification.auth.registered` - Kullanıcı kayıt olayları
- `notification.auth.otp` - OTP gönderim olayları

### 2. RabbitMqMessageConverterConfig.java
**Konum:** `src/main/java/.../AuthDomain/Config/RabbitMqMessageConverterConfig.java`

Jackson2JsonMessageConverter ile RabbitTemplate yapılandırması. Mesajlar JSON formatında serialize/deserialize edilir.

### 3. Event Sınıfları (Java Record)
**Konum:** `src/main/java/.../AuthDomain/Events/`

- `UserRegisteredEvent` - Kullanıcı kayıt olayı (userId, email, phoneNumber, firstName, lastName, registeredAt)
- `OtpSentEvent` - OTP gönderim olayı (userId, phoneNumber, email, otpCode, otpType, sentAt, expiresAt)

## application.yaml Konfigürasyonu

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}
    virtual-host: ${RABBITMQ_VHOST:/}
    connection-timeout: 10000
```

## Kullanım

### Kayıt Olayı (AuthService.java)
Kullanıcı başarıyla kayıt olduğunda `rabbitTemplate.convertAndSend()` ile `internal.exchange`'e `notification.auth.registered` routing key'i ile mesaj gönderilir.

### OTP Olayı (SmsLoginStrategy.java)
OTP oluşturulup gönderildiğinde `rabbitTemplate.convertAndSend()` ile `internal.exchange`'e `notification.auth.otp` routing key'i ile mesaj gönderilir.

## Maven Bağımlılığı

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

## RabbitMQ Management UI

RabbitMQ yönetim panelinden mesajların exchange'e ulaştığını doğrulamak için:
- URL: `http://localhost:15672`
- Varsayılan kullanıcı adı: `guest`
- Varsayılan şifre: `guest`
- Exchanges sekmesinde `internal.exchange`'i kontrol edin

## Environment Variables

```bash
RABBITMQ_HOST=rabbit-server.example.com
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your-username
RABBITMQ_PASSWORD=your-password
RABBITMQ_VHOST=/your-vhost
```
