FROM openjdk:11
MAINTAINER massadobe
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo 'Asia/Shanghai' >/etc/timezone
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} test-server.jar
ENTRYPOINT ["java","-jar","-DAPPLICATION_PROFILES=native","/test-server.jar"]