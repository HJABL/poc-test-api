####  Two solutions

##### WireMock : 

WireMock is an HTTP mock server. At its core it is web server that can be primed to serve canned responses to particular requests (stubbing) and that captures incoming requests so that they can be checked later (verification).
http://wiremock.org/

Example : 
https://github.com/HJABL/poc-test-api/blob/master/src/test/java/com/europcar/BookControllerIntTest.java

##### Jetty : 
![jetty](https://github.com/HJABL/poc-test-api/blob/master/jetty.JPG)
https://www.eclipse.org/jetty/documentation/current/architecture.html

Example : 
https://github.com/HJABL/poc-test-api/blob/master/src/test/java/com/europcar/BookControllerWireMockIntTest.java

## 1. How to start
```
$ mvn spring-boot:run

$ curl -v localhost:8080/books/10
```
