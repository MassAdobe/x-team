FROM openjdk:8-jdk-alpine
MAINTAINER AIOT
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} xxl-job-admin.jar
ENTRYPOINT ["java","-jar","-DAPPLICATION_PROFILES=dev","/xxl-job-admin.jar"]