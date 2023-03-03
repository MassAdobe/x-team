# 非生产环境发布

## 创建基础镜像

`$ vim Dockerfile`

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
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo 'Asia/Shanghai' >/etc/timezone
```

### 创建镜像

`$ docker build -t jdk11:0408 .`

### 清除镜像

`$ docker volume prune`

### docker-compose说明

```yaml
version: '3.3'

services:

  envoy:
    # 需要修改
    container_name: envoy-test
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
      # 需要修改
      envoytest:
        # 需要修改
        aliases:
          - envoy-test-network

  java:
    container_name: test-server
    restart: always
    build:
      context: .
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
    extra_hosts:
      - "dev-mysql:172.17.110.106"
      - "dev-redis:172.17.110.106"
    networks:
      # 需要修改
      envoytest:
        # 需要修改
        aliases:
          - server-test-network

networks:
  # 需要修改
  envoytest:
    driver: bridge
  host:
    external: true
```