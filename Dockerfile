FROM container-registry.oracle.com/graalvm/native-image:21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw -Pnative native:compile

FROM ubuntu:22.04
RUN apt-get update && apt-get install -y \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/event-management-system app

RUN useradd -r -u 1001 spring && chown -R spring:spring /app
USER spring:spring

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["./app"]
