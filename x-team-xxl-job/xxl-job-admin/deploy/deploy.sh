#!/bin/bash

## 常量
APPLICATION_NAME="xxl-job-admin"

## 变量
target_path=$(pwd)

## 编译当前代码
# shellcheck disable=SC2164
cd ../
export JAVA_HOME=$JAVA_11_HOME
mvn clean install

## 上传相关数据
# shellcheck disable=SC2164
cd "$target_path"
# 复制
cp docker-compose-template.yaml docker-compose.yaml
# 更换数据
TIMESTAMP=$(date "+%Y%m%d%H%M%S")
TAG="$APPLICATION_NAME-v$TIMESTAMP"
sed -i "" "s/{{SERVER_TAG}}/$TAG/g" docker-compose.yaml
# 移动
mv ../target/*.jar ./app.jar
# 压缩
tar zcvf ./app.tar.gz ./app.jar
# 删除
rm -rf ./app.jar

## 执行逻辑
expect -c"
	set timeout 10
	spawn ssh $SERVER_USERNAME@$SERVER_IP_PORT
	expect {
		*yes/no* {send \"yes\r\";exp_continue}
		*password:* {send \"$SERVER_PASSWORD\r\"}
	}
	expect ]# { send \"cd /data/x-team/$APPLICATION_NAME\n\" }
	expect ]# { send \"docker-compose down\n\" }
	expect ]# { send \"docker rm \`docker ps -aq\`\"\n }
	expect ]# { send \"docker rmi \`docker images -q $APPLICATION_NAME\`\n\" }
	expect ]# { send \"exit\n\" }
	interact
"
expect -c"
	set timeout 10
	spawn scp -r ./ $SERVER_USERNAME@$SERVER_IP_PORT:/data/x-team/$APPLICATION_NAME
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
	expect ]# { send \"cd /data/x-team/$APPLICATION_NAME\n\" }
	expect ]# { send \"tar zxvf app.tar.gz\n\" }
	expect ]# { send \"rm -rf ./deploy.sh ./app.tar.gz\n\" }
	expect ]# { send \"docker-compose up -d\n\" }
	expect ]# {send \"exit\n\"}
	interact
"

# 删除
rm -rf ./docker-compose.yaml
rm -rf ./app.tar.gz

