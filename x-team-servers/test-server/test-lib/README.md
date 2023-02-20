# lib

用来定义`gRPC`的`proto`文件。

## 示例：

所有`gRPC`对应的结构体都在: `src/main/proto/${application.name}/server/message`中。

### 请求响应对象：

+ `package`是：`${application.name}.server.message;`；
+ 必须添加：`option java_multiple_files = true;`；
+ `java_package`是`${application.name}.server.message`；
+ `java_outer_classname`是`${application.name}MessageProto`，首字母大写。
+ 参数均使用`_`(下划线)的方式命名，例如：`string first_id = 1;`；
+ 请求的`Suffix`是`Request`例如：`TestRequest`；
    + `Struct`使用首字母大写的驼峰，例如：`TestRequest`；
+ 响应的`Suffix`是`Response`，例如：`TestResponse`；
    + `Struct`使用首字母大写的驼峰，例如：`TestResponse`；

```protobuf
syntax = "proto3";

package test.server.message;

option java_multiple_files = true;
option java_package = "test.server.message";
option java_outer_classname = "TestMessageProto";

// ----------------------测试接口------------------------
// 测试接口入参
message TestRequest {
  string first_id = 1;
  bool second = 2;
  repeated Third third = 3;
}

// 测试接口 Third
message Third {
  string third_first = 1;
  int32 third_second = 2;
}

// 测试接口出参
message TestResponse {
  string a = 1;
  int32 b = 2;
}

// ------------------------------------------------
```

### 接口定义：

所有`gRPC`对应的接口都在: `src/main/proto/${application.name}/server/service`中。

+ 使用`envoy`做`protocol`转换；
+ 符合`google.api.http`的方式；
+ 开发前请参阅：[google-api](https://cloud.google.com/apis/design/naming_convention?hl=zh-cn);
+ 引用：
    + `import "google/api/annotations.proto";`，原因略；
    + ` import "google/rpc/error_details.proto"`，原因略；
    + `import "google/api/http.proto"`，原因略；
    + `import "test/server/message/testMessage.proto";`，引用对象；
+ `package`是：`${application.name}.server.service;`；
+ 必须添加：`option java_multiple_files = true;`；
+ 必须添加：`option java_generic_services = true;`；
+ `java_package`是`${application.name}.server.service`；
+ `java_outer_classname`是`${service-zoom}ApiProto`，首字母大写；
+ `service`定义：`${service-zoom}Api`，首字母大写，例如：`service UseApi {}`；
    + **注意：**  `java_outer_classname`和`service name`对应关系，多了`Proto`的尾缀；
+ 可以定义空入参或空出参，需要引用：`import "google/protobuf/empty.proto";`； 
    + 使用方法自行`google`的`proto3`的定义；
+ 其他接口定义请参阅：[google-api](https://cloud.google.com/apis/design/naming_convention?hl=zh-cn);

```protobuf
syntax = "proto3";

package test.server.service;

import "google/api/annotations.proto";
import "google/rpc/error_details.proto";
import "google/api/http.proto";
import "test/server/message/testMessage.proto";

option java_multiple_files = true;
option java_generic_services = true;
option java_package = "test.server.service";
option java_outer_classname = "UseApiProto";

// use interface implements
service UseApi {
  // 测试接口
  rpc ApiTest (test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      get: "/test-server/v1/useApi/apiTest"
    };
  };
  // 测试Proxy-Get
  rpc ApiGet(test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      // test-server/v1/useApi/apiGet/firsts/useGet/param?second=false
      get: "/test-server/v1/useApi/apiGet/{first_id}"
    };
  };
  // 测试Proxy-Get-Other
  rpc ApiGetOther(test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      // test-server/v1/useApi/apiGet/firsts/useGet/param?second=false
      get: "/test-server/v1/useApi/apiGetOther/{first_id=firsts/*}"
    };
  };
  // 测试Proxy-Post
  rpc ApiPost(test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      post: "/test-server/v1/useApi/apiPost/{first_id=firsts/*}"
      body: "*"
    };
  };
  // 测试Proxy-Put
  rpc ApiPut(test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      put: "/test-server/v1/useApi/apiPut/{first_id}"
      body: "*"
    };
  };
  // 测试Proxy-Delete
  rpc ApiDelete(test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      delete: "/test-server/v1/useApi/apiDelete/{first_id}"
      body: "*"
    };
  };
  // 测试自定义Post
  rpc ApiSelfPost(test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      post: "/test-server/v1/useApi/apiSelfPost/{first_id=firsts/*}:self"
      body: "*"
    };
  };
  // 测试自定义Put
  rpc ApiSelfPut(test.server.message.TestRequest) returns (test.server.message.TestResponse) {
    option (google.api.http) = {
      put: "/test-server/v1/useApi/apiSelfPut/{first_id=firsts/*}:self"
      body: "*"
    };
  };
}
```

### 结合 ENVOY 的 gRPC使用

#### ENVOY的使用：

[ENVOY-EXAMPLE](../../../x-team-starters/x-team-grpc-springboot-starter/README.md)