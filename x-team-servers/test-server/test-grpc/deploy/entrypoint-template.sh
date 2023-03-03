#!/usr/bin/env sh

# 启动程序
nohup java -jar -DAPPLICATION_PROFILES=dev -DXXL_EXECUTOR_PORT={{XXL_PORT}} -DHTTP_PORT=8080 -DGRPC_PORT=9090 /app.jar >> /dev/null 2>&1 &
echo "springboot {{APPLICATION_NAME}}-server Started..........."
sleep 10s

# 重启envoy
envoy -c /etc/envoy/envoy.yaml
echo "envoy {{APPLICATION_NAME}}-envoy Started..........."

