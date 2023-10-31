# Use a base image with Java
FROM openjdk:17-oracle

# Set the working directory in the container
WORKDIR /

# Copy the JAR file into the container at the working directory
COPY target/jira_rush.jar /
COPY resources/ /resources

# Expose the port that your Spring Boot application listens on
EXPOSE 8080
# Define the command to run your application
CMD ["java", "-jar", "jira_rush.jar"]
