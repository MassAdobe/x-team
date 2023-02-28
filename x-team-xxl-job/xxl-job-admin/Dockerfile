FROM jdk11:0408
MAINTAINER massadobe
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} xxl-job-admin.jar
ENTRYPOINT ["java","-jar","-DAPPLICATION_PROFILES=native","/xxl-job-admin.jar"]