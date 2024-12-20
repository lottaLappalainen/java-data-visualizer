# Software Design - Group assignment

## Building and executing the application

The application is built using Maven and the executable jar is created with the Maven Assembly -plugin. The application uses Java 17.

Requirements:
- JDK 17
- Maven

Build command:

`mvn clean package assembly:single`

To run the application, ensure the current working directory is in the /target directory of the project.

Run command:

`java -jar app-1.0-SNAPSHOT-jar-with-dependencies.jar`
