FROM jdk11:0408
MAINTAINER massadobe
COPY app.jar app.jar
# 需要修改
ENTRYPOINT ["java","-jar","-DAPPLICATION_PROFILES=dev", \
"-DXXL_EXECUTOR_PORT=17001", \
"-DHTTP_PORT=8080", \
"-DGRPC_PORT=9090", \
"/app.jar"]