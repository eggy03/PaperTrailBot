# Importing JDK and copying required files
FROM openjdk:21-jdk AS build
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

RUN ./mvnw clean package spring-boot:repackage

# Stage 2: Create the final Docker image using IBM Semeru Runtime
FROM ibm-semeru-runtimes:open-21-jre-focal AS runtime
WORKDIR /app
VOLUME /tmp

# Copy the JAR from the build stage
COPY --from=build /app/target/paper-trail-bot.jar paper-trail-bot.jar
ENTRYPOINT ["java","-jar","paper-trail-bot.jar"]
EXPOSE 8080