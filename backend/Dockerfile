# ============================================================
# Spring Boot — multi-stage build.
#
# Stage 1 (build): Maven компілює + пакує JAR
# Stage 2 (run):   мінімальний JRE образ запускає JAR
#
# Припущення: артефакт називається vdm-0.0.1-SNAPSHOT.jar
# Змінити якщо інший artifactId/version в pom.xml
# ============================================================

# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Копіюємо pom.xml окремо → кешування шару mvn dependency:go-offline
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -Dcheckstyle.skip -q

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копіюємо тільки JAR (не весь target/)
COPY --from=builder /app/target/vdm-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
