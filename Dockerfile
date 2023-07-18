FROM eclipse-temurin:17-jdk AS builder
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY gradlew .
COPY gradle gradle
COPY server server
COPY content content
COPY server-dist server-dist
RUN chmod +x ./gradlew && ./gradlew clean installDist --no-daemon

FROM eclipse-temurin:17-jdk

EXPOSE 8147
EXPOSE 8080
ENV SERVER__TERMINAL=false

WORKDIR /app

COPY --from=builder ./server-dist/build/install/server-dist/ ./

CMD ["./bin/server-dist"]