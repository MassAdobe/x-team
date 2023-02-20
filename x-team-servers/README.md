# x-team-servers

项目`DDD`服务，扁平化处理，遵循`istio`规范，所有服务自制，负载均衡和反向代理托管给`ingress`，服务注册发现托管给`coreDNS`。

## 注意：

+ `${server-name}-grpc`：`GRPC`服务实现；
+ `${server-name}-rest`：给`GRPC`服务实现提供`restful`接口能力；
+ `${server-name}-lib`：定义`GRPC`服务实现的`proto`代码，或给第三方使用；
+ `${server-name}-openapi`：提供给第三方的接口能力，以`HTTP1.1`为主；
+ `${server-name}-schedule`：使服务拥有定时任务的能力，转接`XXL-JOB`；