# # 使用 Maven 构建 Spring Boot 项目
# FROM maven:3.9.4-openjdk-17 AS build

# # 设置工作目录
# WORKDIR /app

# # 复制 Maven 配置文件和源码
# COPY pom.xml ./
# COPY src ./src

# # 使用 Maven 打包项目（跳过测试）
# RUN mvn clean package -DskipTests

# 使用 JDK 运行 Spring Boot 项目
FROM openjdk:17-jdk-slim

# 使用 Chrome 浏览器
FROM selenium/standalone-chrome:latest

# 设置工作目录
WORKDIR /app

# 复制生成的 JAR 文件
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露 Spring Boot 默认端口
EXPOSE 8080

# 启动 Spring Boot 应用
CMD ["java", "-jar", "app.jar"]
