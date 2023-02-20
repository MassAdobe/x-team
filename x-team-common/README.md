# x-team-common

工具包

创建工具类时，实现方法尽量使用`static`修饰。

## 作用：

+ ### 系统级别常量池
    + 云服务的常量池：`CloudServerConstants`；
    + 普通常量池：`CommonConstants`；
    + GRPC的常量池：`GrpcConstants`；
    + HTTP的常量池：`HttpConstants`；
    + kafka配置常量池：`KafkaConstants`；
    + 国际化的常量池：`LanguageConstants`；
    + 日志的常量池：`LogConstants`；
    + Redis的常量池：`RedisConstants`；
    + 正则的常量池：`RegexConstants`；
    + SQL的常量池：`SqlConstants`；
+ ### 系统界别错误枚举
    + 报错枚举：`ErrorCodeMsg`，用来标志全系统的报错；
+ ### 系统级别工具包
    + AES加密包：`AESUtils`；
    + Base64加密包：`Base64Utils`；
    + 位运算包：`BcryptUtils`；
    + 位图运算包：`BitMapUtils`；
    + 常用工具包：`CommonUtils`；
    + 秘钥加密包：`EncryptUtils`；
    + Hmac加密包：`HmacUtil`；
    + JSON工具类：`JsonUtils`；
    + Md5加密包：`Md5Utils`；
    + RSA加密包：`RSAUtils`；
    + Sha256加密包：`Sha256Util`；