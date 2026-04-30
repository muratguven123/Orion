# ----------------------------
# 1. AŞAMA: DERLEME (Build)
# ----------------------------
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Proje dosyalarini kopyala
COPY pom.xml .
COPY src ./src

# Projeyi derle (Testleri atlayarak)
RUN mvn clean package -DskipTests

# ----------------------------
# 2. AŞAMA: ÇALIŞTIRMA (Run)
# ----------------------------
FROM openjdk:17-jdk-slim
WORKDIR /app

# İlk aşamada oluşan JAR dosyasını buraya al
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]