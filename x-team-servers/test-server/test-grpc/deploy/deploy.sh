#!/bin/bash

## 常量
APPLICATION_NAME="test"
## 对齐application.yaml
SERVER_PORT="16001"
XXL_PORT="17001"
ENVOY_PORT="18001"
ADMIN_PORT="19001"
## 集中还是分开 默认“集中”
DEPLOY_TYPE="concentrated"
#DEPLOY_TYPE="split"
## 地址
XDS_ADDRESS="127.0.0.1"
## 变量
target_path=$(pwd)

## 编译当前代码
# shellcheck disable=SC2164
cd ../../$APPLICATION_NAME-grpc
export JAVA_HOME=$JAVA_11_HOME
mvn clean install
# shellcheck disable=SC2164
cd "$target_path"
# 复制
cp ../../../template/envoy-template.yaml envoy.yaml
if [ "split" == "$DEPLOY_TYPE" ]; then
  cp ../../../template/docker-compose-split.yaml docker-compose.yaml
  cp ../../../template/Dockerfile-split Dockerfile
  XDS_ADDRESS="$APPLICATION_NAME-server"
else
  cp ../../../template/docker-compose-concentrated.yaml docker-compose.yaml
  cp ../../../template/Dockerfile-concentrated Dockerfile
  cp ../../../template/entrypoint-concentrated.sh entrypoint.sh
fi
# 更换数据
TIMESTAMP=$(date "+%Y%m%d%H%M%S")
TAG="$APPLICATION_NAME-v$TIMESTAMP"
# docker-compose
sed -i "" "s/{{SERVER_TAG}}/$TAG/g" docker-compose.yaml
sed -i "" "s/{{APPLICATION_NAME}}/$APPLICATION_NAME/g" docker-compose.yaml
sed -i "" "s/{{SERVER_PORT}}/$SERVER_PORT/g" docker-compose.yaml
sed -i "" "s/{{XXL_PORT}}/$XXL_PORT/g" docker-compose.yaml
sed -i "" "s/{{ENVOY_PORT}}/$ENVOY_PORT/g" docker-compose.yaml
sed -i "" "s/{{ADMIN_PORT}}/$ADMIN_PORT/g" docker-compose.yaml
# envoy
sed -i "" "s/{{APPLICATION_NAME}}/$APPLICATION_NAME/g" envoy.yaml
sed -i "" "s/{{XDS_ADDRESS}}/$XDS_ADDRESS/g" envoy.yaml
# dockerfile
sed -i "" "s/{{XXL_PORT}}/$XXL_PORT/g" Dockerfile
sed -i "" "s/{{DEPLOY_TYPE}}/$DEPLOY_TYPE/g" Dockerfile
# entrypoint
sed -i "" "s/{{XXL_PORT}}/$XXL_PORT/g" entrypoint.sh
sed -i "" "s/{{DEPLOY_TYPE}}/$DEPLOY_TYPE/g" entrypoint.sh
sed -i "" "s/{{APPLICATION_NAME}}/$APPLICATION_NAME/g" entrypoint.sh
# 移动
cp ../target/*.jar ./app.jar

## 执行逻辑
expect -c"
	set timeout 10
	spawn ssh $SERVER_USERNAME@$SERVER_IP_PORT
	expect {
		*yes/no* {send \"yes\r\";exp_continue}
		*password:* {send \"$SERVER_PASSWORD\r\"}
	}
	expect ]# { send \"cd /data/x-team/$APPLICATION_NAME-server\n\" }
	expect ]# { send \"docker-compose down\n\" }
	expect ]# { send \"docker rm \`docker ps -aq\`\"\n }
	expect ]# { send \"docker rmi \`docker images -q $APPLICATION_NAME-server\`\n\" }
	expect ]# { send \"exit\n\" }
	interact
	set timeout 10
	spawn scp -r ./ $SERVER_USERNAME@$SERVER_IP_PORT:/data/x-team/$APPLICATION_NAME-server
	expect {
		*yes/no* {send \"yes\r\";exp_continue}
		*password:* {send \"$SERVER_PASSWORD\r\"}
	}
	interact
	set timeout 10
	spawn ssh $SERVER_USERNAME@$SERVER_IP_PORT
	expect {
		*yes/no* {send \"yes\r\";exp_continue}
		*password:* {send \"$SERVER_PASSWORD\r\"}
	}
	expect ]# { send \"cd /data/x-team/$APPLICATION_NAME-server\n\" }
	expect ]# { send \"rm -rf ./deploy.sh ./README.md ./docker-compose-* ./Dockerfile-* ./entrypoint-* ./envoy-* ./api\n\" }
	expect ]# { send \"docker-compose up -d\n\" }
	expect ]# { send \"exit\n\" }
	interact
"

# 删除
rm -rf ./docker-compose.yaml ./app.jar ./Dockerfile ./entrypoint.sh ./envoy.yaml




