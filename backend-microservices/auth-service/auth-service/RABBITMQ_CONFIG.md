# RabbitMQ Konfigürasyon Rehberi

## Genel Bakış
Bu dokümantasyon, Auth Service'de RabbitMQ event-driven mimarisinin uygulanmasını açıklamaktadır.

## Oluşturulan Dosyalar

### 1. RabbitMqConfig.java
**Konum:** `src/main/java/com/murat/orion/auth_service/AuthDomain/Config/RabbitMqConfig.java`

Ana RabbitMQ konfigürasyonu. Aşağıdaki öğeleri tanımlar:

#### Exchanges
- `auth.exchange` - Ana topic exchange

#### Queues
- `auth.user.registered.queue` - Kullanıcı kayıt olayları
- `auth.user.login.queue` - Kullanıcı giriş olayları
- `auth.user.logout.queue` - Kullanıcı çıkış olayları
- `auth.password.changed.queue` - Şifre değişimi olayları
- `auth.otp.sent.queue` - OTP gönderimi olayları
- `auth.otp.verified.queue` - OTP doğrulama olayları
- `auth.email.login.queue` - Email giriş olayları
- `auth.sms.login.queue` - SMS giriş olayları
- `auth.login.failed.queue` - Başarısız giriş olayları
- `auth.dead.letter.queue` - İşlenemeyen mesajlar

#### Routing Keys
Her queue'nin karşılık gelen routing key'i vardır:
- `auth.user.registered`
- `auth.user.login`
- `auth.user.logout`
- `auth.password.changed`
- `auth.otp.sent`
- `auth.otp.verified`
- `auth.email.login`
- `auth.sms.login`
- `auth.login.failed`

### 2. RabbitMqPublisher.java
**Konum:** `src/main/java/com/murat/orion/auth_service/AuthDomain/Config/RabbitMqPublisher.java`

Event yayıncı (publisher) bileşeni. Farklı türde eventleri yayınlamak için metodlar içerir:

```java
// Kullanım örneği
@Autowired
private RabbitMqPublisher publisher;

// Doğrudan metodlar
publisher.publishUserRegisteredEvent(event);
publisher.publishUserLoginEvent(event);
publisher.publishOtpSentEvent(event);

// Veya genel method
publisher.publishEvent(exchange, routingKey, event);
```

### 3. RabbitMqMessageConverterConfig.java
**Konum:** `src/main/java/com/murat/orion/auth_service/AuthDomain/Config/RabbitMqMessageConverterConfig.java`

RabbitMQ mesaj dönüştürücü konfigürasyonu. Jackson JSON serialization/deserialization sağlar.

### 4. RabbitMqErrorHandler.java
**Konum:** `src/main/java/com/murat/orion/auth_service/AuthDomain/Config/RabbitMqErrorHandler.java`

RabbitMQ mesaj işleme hatalarını yönetir. Hata durumlarında logla ve gerekli aksiyonları al.

### 5. RabbitMqListener.java
**Konum:** `src/main/java/com/murat/orion/auth_service/AuthDomain/Config/RabbitMqListener.java`

Event consumer (dinleyici) bileşeni. Queue'lardan gelen mesajları dinler ve işler.

```java
// @RabbitListener anotasyonu ile queue'dan mesaj alınır
@RabbitListener(queues = RabbitMqConfig.USER_REGISTERED_QUEUE)
public void handleUserRegisteredEvent(Object event) {
    // Event işlemesi
}
```

## application.yaml Konfigürasyonu

RabbitMQ bağlantı parametreleri environment variables veya varsayılan değerlerle yapılandırılır:

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}
    virtual-host: ${RABBITMQ_VHOST:/}
    connection-timeout: 10000
    listener:
      simple:
        acknowledge-mode: auto
        prefetch: 1
        concurrency: 1
        max-concurrency: 10
        retry:
          enabled: true
          initial-interval: 1000
          max-interval: 10000
          max-attempts: 3
          multiplier: 2.0
```

## Maven Bağımlılığı

pom.xml'e aşağıdaki dependency eklendi:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

## Kullanım Örnekleri

### Event Yayınlama
```java
@Autowired
private RabbitMqPublisher publisher;

// Kullanıcı kaydı olayı
public void registerUser(User user) {
    // ... kullanıcı kaydı işlemi ...
    publisher.publishUserRegisteredEvent(user);
}

// Email giriş olayı
public void emailLogin(String email) {
    // ... giriş işlemi ...
    publisher.publishEmailLoginEvent(loginEvent);
}
```

### Event Dinleme
```java
@RabbitListener(queues = RabbitMqConfig.USER_REGISTERED_QUEUE)
public void onUserRegistered(UserRegisteredEvent event) {
    log.info("Yeni kullanıcı kaydı: {}", event.getEmail());
    // E-posta gönderme, bildirim vb. işlemler yapılabilir
}
```

## Dead Letter Queue (DLQ)

İşlenemeyen veya hatalı mesajlar otomatik olarak Dead Letter Exchange'e gönderilir ve `auth.dead.letter.queue`'da depolanır.

## Environment Variables

Üretim ortamında aşağıdaki environment variables'ı ayarlayın:

```bash
RABBITMQ_HOST=rabbit-server.example.com
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your-username
RABBITMQ_PASSWORD=your-password
RABBITMQ_VHOST=/your-vhost
```

## RabbitMQ Management UI

RabbitMQ yönetim paneline erişmek için:
- URL: `http://localhost:15672`
- Varsayılan kullanıcı adı: `guest`
- Varsayılan şifre: `guest`

Buradan queues, exchanges, bindings'i kontrol edebilirsiniz.

## Notlar

1. **Acknowledgment Mode**: `AUTO` olarak ayarlanmıştır. Mesaj başarıyla işlenirse otomatik olarak acknowledge edilir.
2. **Prefetch**: 1 olarak ayarlanmıştır. Her consumer aynı anda sadece 1 mesaj alır.
3. **Retry**: Başarısız mesajlar yapılandırılan sayıda ve aralıklarda yeniden denenir.
4. **Concurrency**: Listener container'ında çalışan consumer sayısı 1-10 arasında değişir.

## Sorun Giderme

### RabbitMQ bağlantı hatası
```
Connection refused: connect
```
RabbitMQ sunucusunun çalışıp çalışmadığını kontrol edin.

### Message conversion error
JSON dönüştürme hatası varsa, event sınıflarının `Serializable` olduğundan veya Jackson anotasyonlarının doğru olduğundan emin olun.

### Dead Letter mesajları
Düzenli olarak dead letter queue'yu kontrol edin ve hatanın kaynağını belirleyin.

