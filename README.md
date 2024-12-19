# Market-Checkout

### Install Dependencies
Ensure that the following are installed on your system:

* Java 21 (To verify: java -version)
* Docker (To verify: docker -v)

### Run database container:
* Run following commands
* cd database
* docker build -t db:latest .
* docker run -d -p 40000:5432 --name db-container db:latest

### Installation:
* mvn clean install

### Run tests:
* mvn test

### Run application:
* start an application and go to http://localhost:8080/swagger-ui/index.html