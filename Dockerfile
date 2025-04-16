# Stage 1: Build stage
FROM hseeberger/scala-sbt:11.0.19_1.9.9_2.13.12 AS build

WORKDIR /app

# Copy build files and dependencies first (for caching)
COPY build.sbt .
COPY project ./project

# Fetch dependencies
RUN sbt update

# Copy the rest of the source code
COPY . .

# Build the application (fat JAR)
RUN sbt clean compile stage

# Stage 2: Run stage
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the built app from the build stage
COPY --from=build /app/target/universal/stage /app

# Expose the default Play port
EXPOSE 9000

# Run the app
CMD ["bin/marlowbank-1.0-SNAPSHOT"]
