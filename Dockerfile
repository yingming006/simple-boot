# ---- 第一阶段：构建 ----
# 使用一个包含 Maven 和 JDK 的镜像作为构建环境
FROM maven:3.8-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖项，利用Docker的层缓存机制
# 只有当 pom.xml 变化时，才会重新下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制所有源码
COPY src ./src

# 执行 Maven 打包命令
# -DskipTests 表示在构建镜像时不运行测试
RUN mvn package -DskipTests

# ---- 第二阶段：运行 ----
# 使用一个更轻量的、只包含 JRE 的官方镜像作为运行环境
FROM openjdk:17-jre-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段（别名为 build）复制打包好的 JAR 文件到当前阶段
COPY --from=build /app/target/simple-0.0.1.jar app.jar

# 暴露应用程序的端口
EXPOSE 8080

# 设置容器启动时执行的命令
# ENTRYPOINT ["java", "-jar", "app.jar"]
# 使用下面的命令形式，允许在 docker run 时传递 JVM 参数，更灵活
CMD ["java", "-jar", "/app/app.jar"]