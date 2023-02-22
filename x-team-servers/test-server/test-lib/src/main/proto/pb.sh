#!/bin/bash

## 常量
APPLICATION_NAME="test"

## 变量
target_path=$(pwd)

echo "----- INFO [CURRENT-PATH]: [$target_path] -----"

# open-api
# shellcheck disable=SC2225
mkdir -p ./grpc
cp -r ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/grpc/gateway ./grpc
echo "----- INFO [OPENAPI HAS BEEN COPIED] -----"

## 到达mvn的目录
# shellcheck disable=SC2164
cd ../../../
export JAVA_HOME=$JAVA_11_HOME
mvn clean install

## 回到原始目录
# shellcheck disable=SC2164
cd "$target_path"

## 删除open-api文件
rm -rf ./grpc
echo "----- INFO [OPENAPI (ANNOTATIONS/OPEN-API-V2) HAS BEEN DELETED] -----"

## 先删除原来的PB和API文件
rm -rf ../../../../../../pb/$APPLICATION_NAME-server/*.pb
rm -rf ../../../../../../pb/$APPLICATION_NAME-server/*.json
echo "----- INFO [DELETED ORIGIN $APPLICATION_NAME-server.pb AND $APPLICATION_NAME-api.json] -----"

## 复制envoy的proto到目标文件夹下
# LDS
# shellcheck disable=SC2225
cp ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/envoy/service/listener/v3/lds.proto \
./$APPLICATION_NAME/server/service/lds.proto
echo "----- INFO [ENVOY-LDS HAS BEEN COPIED] -----"

# RDS
# shellcheck disable=SC2225
cp ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/envoy/service/route/v3/rds.proto \
./$APPLICATION_NAME/server/service/rds.proto
echo "----- INFO [ENVOY-RDS HAS BEEN COPIED] -----"

# CDS
# shellcheck disable=SC2225
cp ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/envoy/service/cluster/v3/cds.proto \
./$APPLICATION_NAME/server/service/cds.proto
echo "----- INFO [ENVOY-CDS HAS BEEN COPIED] -----"

# EDS
# shellcheck disable=SC2225
cp ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/envoy/service/endpoint/v3/eds.proto \
./$APPLICATION_NAME/server/service/eds.proto
echo "----- INFO [ENVOY-EDS HAS BEEN COPIED] -----"

# gRPC-Health
# shellcheck disable=SC2225
cp ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/grpc/health/v1/health.proto \
./$APPLICATION_NAME/server/service/annotations.proto
echo "----- INFO [GRPC-HEALTH HAS BEEN COPIED] -----"

# gRPC-reflection
# shellcheck disable=SC2225
cp ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/grpc/reflection/v1alpha/reflection.proto \
./$APPLICATION_NAME/server/service/reflection.proto
echo "----- INFO [GRPC-REFLECTION HAS BEEN COPIED] -----"

# gRPC-Health-Server
# shellcheck disable=SC2225
cp ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto/grpc/health/server/health.proto \
./$APPLICATION_NAME/server/service/healthServer.proto
echo "----- INFO [GRPC-HEALTH HAS BEEN COPIED] -----"

## 生成相关的"pb"文件，给envoy使用
echo ""
echo "----- INFO [DOING PROTOC PB] -----"
protoc -I ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto \
  -I ./ \
  --include_imports --include_source_info \
  --descriptor_set_out=../../../../../../pb/$APPLICATION_NAME-server/$APPLICATION_NAME-server.pb \
  ./$APPLICATION_NAME/server/service/*.proto
echo "----- INFO [NEW $APPLICATION_NAME-server.pb HAS BEEN GENERATED] -----"
echo ""

## 复制到envoy目录下
cp ../../../../../../pb/$APPLICATION_NAME-server/$APPLICATION_NAME-server.pb ~/docker/envoy-grpc-xds/pb/$APPLICATION_NAME-server.pb
echo "----- INFO [NEW $APPLICATION_NAME-server.pb HAS BEEN COPIED] -----"

## 删除envoy的proto文件
# shellcheck disable=SC1101
rm -rf ./$APPLICATION_NAME/server/service/lds.proto \
./$APPLICATION_NAME/server/service/rds.proto \
./$APPLICATION_NAME/server/service/cds.proto \
./$APPLICATION_NAME/server/service/eds.proto \
./$APPLICATION_NAME/server/service/health.proto \
./$APPLICATION_NAME/server/service/reflection.proto \
./$APPLICATION_NAME/server/service/annotations.proto \
./$APPLICATION_NAME/server/service/healthServer.proto
echo "----- INFO [ENVOY (LDS/RDS/CDS/EDS/HEALTH/REFLECTION) HAS BEEN DELETED] -----"

## 生成相关的"swagger-json"文件，给YAPI使用
echo ""
echo "----- INFO [DOING PROTOC SWAGGER] -----"
protoc -I ../../../../../../x-team-starters/x-team-grpc-springboot-starter/src/main/proto \
  -I ./ \
  --include_imports --include_source_info \
  --openapiv2_out ../../../../../../pb/$APPLICATION_NAME-server \
  --openapiv2_opt logtostderr=true \
  --openapiv2_opt allow_delete_body=true \
  --openapiv2_opt allow_merge=true \
  --openapiv2_opt generate_unbound_methods=true \
  --openapiv2_opt openapi_naming_strategy=fqn \
  ./$APPLICATION_NAME/server/service/*.proto
echo "----- INFO [NEW $APPLICATION_NAME-api.json HAS BEEN GENERATED] -----"
echo ""

## 转译api的文件json
# shellcheck disable=SC2224
mv ../../../../../../pb/$APPLICATION_NAME-server/apidocs.swagger.json ../../../../../../pb/$APPLICATION_NAME-server/$APPLICATION_NAME-api.json
echo "----- INFO [RENAME apidocs.swagger.json HAS BEEN RENAMED AS $APPLICATION_NAME-api.json] -----"
