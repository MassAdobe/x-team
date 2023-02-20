# x-team-dependencies

`maven`的依赖项

## 注意：

+ 项目中使用的所有第三方包，都需要在`x-team-dependencies`中定义，防止相关版本和包冲突；
+ 在项目中的应用不允许在坐标中`<dependency>`出现`<version>`；
    + ```xml
        <dependency>
            <groupId>com.x.team</groupId>
            <artifactId>x-team-common</artifactId>
        </dependency>

        <dependency>
            <groupId>com.x.team</groupId>
            <artifactId>x-team-log-springboot-starter</artifactId>
        </dependency>
+ 使用`${revision}`方便版本管理，防止版本冲突；
