FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the POM file
COPY pom.xml .

# Download all dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy

# Install FFmpeg and dependencies needed by JavaCV
RUN apt-get update && \
     apt-get install -y --no-install-recommends \
         ffmpeg \
         libavcodec-dev \
         libavformat-dev \
         libavutil-dev \
         libswscale-dev \
         libavfilter-dev \
         libpostproc-dev \
         libswresample-dev && \
     apt-get clean && \
     rm -rf /var/lib/apt/lists/*

# Create directory for uploads
RUN mkdir -p /app/uploads && \
    chmod 777 /app/uploads

# Create directory for uploads
RUN mkdir -p /app/uploads/temp && \
    chmod 777 /app/uploads/temp

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set Java system properties for JavaCV
ENV JAVA_OPTS="-Djavacpp.platform=linux-x86_64 -Djava.io.tmpdir=/tmp"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]