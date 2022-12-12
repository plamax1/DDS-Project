FROM maven:3.6.3-jdk-11

# Set the working directory
WORKDIR /app

# Copy the pom.xml file
COPY . /app

# Build the project
RUN mvn clean install

# Run the sender class
CMD mvn exec:java
