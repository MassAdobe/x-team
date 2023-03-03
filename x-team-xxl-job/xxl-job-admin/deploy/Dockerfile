FROM jdk11:0408
MAINTAINER massadobe
COPY app.jar app.jar
ENTRYPOINT ["java","-jar","-DAPPLICATION_PROFILES=dev","/app.jar"]