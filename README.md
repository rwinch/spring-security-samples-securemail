Running SecureMail with Gradle
======================

1. Ensure you have java installed
2. cd spring-security-samples-securemail
3. Start the application with ./gradlew jettyRun or .\gradlew.bat jettyRun

Browse to http://localhost:8080/mail/

Login with rob@example.org / penguin or luke@example.com / lion

Running SecureMail in Spring Tool Suite
======================

1. Download STS http://www.springsource.com/downloads/sts I use 2.8.1.RELEASE
2. Navigate to the Dashboard -> Extensions tab and install the following plugins:
   * Gradle Support
   * Groovy Eclipse
   * Scala IDE for Eclipse
3. Import the Project with Gradle Eclipse plugin.
   * File-> Import... -> Gradle Project
   * Next >
   * Build Model
   * Ensure the project is selected and click Finish.
4. Right click the project and click Run As -> Run on Server
