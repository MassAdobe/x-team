# 非生产环境发布

## 创建基础镜像

`$ vim Dockerfile`

### 分开

```dockerfile
FROM openjdk:11 as builder
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 3B4FE6ACC0B21F32
RUN mv /etc/apt/sources.list /etc/apt/sources.list.bak && echo "deb http://mirrors.163.com/debian/ jessie main non-free contrib" >/etc/apt/sources.list && echo "deb http://mirrors.163.com/debian/ jessie-proposed-updates main non-free contrib" >>/etc/apt/sources.list && echo "deb-src http://mirrors.163.com/debian/ jessie main non-free contrib" >>/etc/apt/sources.list && echo "deb-src http://mirrors.163.com/debian/ jessie-proposed-updates main non-free contrib" >>/etc/apt/sources.list
RUN echo "">  /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-security main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-proposed main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-security main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-proposed main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse" >> /etc/apt/sources.list
RUN apt-get update
RUN apt upgrade
RUN apt-get install inetutils-ping -y
RUN apt-get install net-tools -y
RUN apt-get install telnetd-ssl -y
RUN apt-get install xinetd -y
RUN apt-get install telnetd -y
RUN apt-get install telnet -y
RUN apt-get install lsof -y
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo 'Asia/Shanghai' >/etc/timezone
```

### 创建基础镜像

`$ docker build -t jdk11:0408 .`

### 集中

```dockerfile
FROM envoyproxy/envoy:v1.21.6 as builder
USER root
ENV TZ=Asia/Shanghai \
    DEBIAN_FRONTEND=noninteractive
RUN echo "">  /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-security main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-proposed main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb http://mirrors.ustc.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-security main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-proposed main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb-src http://mirrors.ustc.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse" >> /etc/apt/sources.list
RUN apt-get update
RUN apt upgrade
RUN apt-get install inetutils-ping -y
RUN apt-get install net-tools -y
RUN apt-get install xinetd -y
RUN apt-get install telnet -y
RUN apt-get install vim -y
RUN apt-get install tar -y
RUN apt-get install lsof -y
COPY openjdk-11.tar.gz /root/openjdk-11.tar.gz
WORKDIR /root
RUN tar zxvf openjdk-11.tar.gz && mv jdk-11.0.2 jdk && rm -rf openjdk-11.tar.gz
ENV JAVA_HOME=/root/jdk
ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH=$JAVA_HOME/bin:$PATH
```

### 创建基础镜像

`$ docker build -t baseXTeam:0106 .`

### 清除镜像

`$ docker volume prune`

## 发布dev环境

需要根据自己的环境进行修改，部署前前需要修改：`~/.bash_profie`；

```shell
# 配置自己服务器的ssh登录用户名
export SERVER_USERNAME=xxx
# 配置自己的服务器的IP地址
export SERVER_IP_PORT="xxx.xxx.xxx.xxx"
# 配置自己的服务器ssh密码
export SERVER_PASSWORD=yyy
```

+ 可以执行`./deploy`下的`deploy.sh`即可；
+ 前提：先执行：`test-lib/src/main/proto`下的`pb.sh`；
+ 修改成`rsa`登录，懒得搞了。

## envoy.yaml说明

+ 用`sidecar`方式进行部署，`dev`环境使用`shell`脚本部署，使用`docker-compose`的方式进行编排；
+ 可以用`install docker`的方式，把代码和`envoy`部署在同一个基础镜像中，但是我懒得搞了。

```yaml
node:
  # 需要修改 ${application-name} + "-server-envoy"
  id: test-server-envoy
  # 需要修改 ${application-name} + "-server-envoy-cluster"
  cluster: test-server-envoy-cluster

admin:
  address:
    socket_address:
      address: 0.0.0.0
      port_value: 10417
  access_log:
  - name: envoy.access_loggers.file
    typed_config:
      "@type": type.googleapis.com/envoy.extensions.access_loggers.stream.v3.StdoutAccessLog

dynamic_resources:
  lds_config:
    resource_api_version: V3
    api_config_source:
      api_type: GRPC
      transport_api_version: V3
      grpc_services:
        envoy_grpc:
          # ① 需要修改 ${application-name} + "-server-xds"
          cluster_name: test-server-xds
  cds_config:
    resource_api_version: V3
    api_config_source:
      api_type: GRPC
      transport_api_version: V3
      grpc_services:
        envoy_grpc:
          # ② 需要修改 ${application-name} + "-server-xds"
          cluster_name: test-server-xds

static_resources:
  clusters:
  # ③ 需要修改 ${application-name} + "-server-xds"
  - name: test-server-xds
    connect_timeout: 10s
    type: LOGICAL_DNS
    http2_protocol_options: {}
    lb_policy: ROUND_ROBIN
    load_assignment:
      # ④ 需要修改 ${application-name} + "-server-xds"
      cluster_name: test-server-xds
      endpoints:
      - lb_endpoints:
        - endpoint:
            address:
              # 需要修改
              # 如果是集中部署 address: localhost
              # 如果是分开部署 address: ${application-name} + "-server"
              socket_address: { address: test-server, port_value: 9090 }
# ①、②、③、④ 保持命名一致
```


## docker-compose说明

```yaml
version: '3.3'

services:

  envoy:
    # 需要修改 ${application-name}+"-envoy"
    container_name: test-envoy
    restart: always
    image: envoyproxy/envoy-alpine:v1.21.6
    # 待修改
    ports:
      # 调用端口 需要修改
      - "18001:10408"
      # ADMIN端口 需要修改
      - "19001:10417"
    volumes:
      - ./envoy.yaml:/etc/envoy/envoy.yaml
      - ./pb/:/etc/envoy/pb/
    depends_on:
      - jar
    expose:
      # 调用端口
      - "10408"
      # ADMIN端口
      - "10417"
    networks:
      # 需要修改 ${application-name}+"_mesh"
      test_mash:
        # 需要修改 ${application-name}+"_envoy_network"
        aliases:
          - test_envoy_network

  server:
    # 需要修改 ${application-name}+"-server"
    container_name: test-server
    restart: always
    build:
      context: ../test-server/test-grpc/deploy
    ports:
      # XXL-JOB端口 需要修改
      - "17001:17001"
      # gRPC端口 需要修改
      - "16001:9090"
    volumes:
      - /etc/hosts:/etc/hosts:ro
      - ./log:/usr/local/logs/x-team
    expose:
      # XXL-JOB端口 需要修改
      - "17001"
      # gRPC端口
      - "9090"
    # 对齐nacos中的配置
    extra_hosts:
      - "dev-mysql:172.17.110.106"
      - "dev-redis:172.17.110.106"
    networks:
      # 需要修改 ${application-name}+"_mesh"
      test_mesh:
        aliases:
          # 需要修改 ${application-name}+"_server_network"
          - test_server_network

networks:
  # 需要修改
  test_mesh:
    driver: bridge
  host:
    external: true
```