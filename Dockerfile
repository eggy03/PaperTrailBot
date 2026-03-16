# Importing JDK and copying required files
FROM eclipse-temurin:25 AS build
WORKDIR /app

# Copy Maven wrapper
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Set execution permission for the Maven wrapper
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# Copy the source files after dependencies are cached
COPY src ./src

RUN ./mvnw -B -e -DskipTests clean package spring-boot:repackage

# Stage 2: Create the final Docker image using IBM Semeru Runtime
FROM ibm-semeru-runtimes:open-25-jre-noble AS runtime
WORKDIR /app
VOLUME /tmp

# Copy the JAR from the build stage
COPY --from=build /app/target/papertrailbot.jar papertrailbot.jar
ENTRYPOINT ["java","-jar","papertrailbot.jar"]