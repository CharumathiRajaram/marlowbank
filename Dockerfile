FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy pre-built Play application ZIP
COPY target/universal/*.zip ./app.zip

# Install unzip and extract app

RUN apt-get update && apt-get install -y unzip && \
    unzip app.zip && \
    rm app.zip && \
    mv marlowbank-* marlowbank
# Set working directory to extracted app folder
WORKDIR /app/marlowbank

# Run the application
CMD ["bin/marlowbank", "-Dplay.http.secret.key=/+NoPAH46CBiKjGP1CSD9fy05sqOAIAbT26zLkdNEMQ="]