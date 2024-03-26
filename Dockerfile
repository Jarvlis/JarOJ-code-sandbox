# Docker 镜像构建
FROM maven:3.8.1-jdk-8-slim AS builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY jaroj-code-sandbox-0.0.1-SNAPSHOT.jar ./target/

# Build a release artifact.
#RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/app/target/jaroj-code-sandbox-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]