# Simple Boot - 生产级后端基础架构模板

这是一个基于 Spring Boot 3 和 Java 17 构建的、高度模块化的、生产就绪的后端基础架构模板。它集成了业界主流的技术栈和最佳实践，旨在为新项目的快速启动提供一个坚实、可靠、可扩展的基础。

## ✨ 核心特性

*   **现代化技术栈**: Spring Boot 3, Spring Security 6, Mybatis-Plus, MySQL, Redis。
*   **健壮的安全体系**:
    *   基于 JWT 的无状态认证流程。
    *   完整的 RBAC (基于角色的访问控制) 权限模型。
    *   支持到 API 级别的细粒度权限控制 (`@PreAuthorize`)。
*   **完备的管理后台API**:
    *   提供了对**用户**、**角色**、**权限**的完整 CRUD 和分配接口。
    *   提供了**系统配置**的动态管理接口。
    *   提供了**字典数据**的类型和条目管理接口。
    *   提供了**操作日志**的查询和审计功能。
*   **生产化准备**:
    *   **Redis 缓存**: 集成 Spring Cache 和 Redis，对高频访问数据（如用户权限、字典）进行缓存，并支持配置文件开关。
    *   **API 幂等性保障**: 通过 Token + Redis + AOP 实现，防止重复提交关键操作。
    *   **应用监控**: 集成 Spring Boot Actuator，提供标准化的健康检查和信息端点。
    *   **异步处理**: 通过 `@Async` 为耗时任务（如日志记录）提供异步执行能力。
    *   **启动时健康检查**: 强制检查数据库连接，确保服务在核心依赖不可用时启动失败 (Fail-Fast)。
*   **高质量代码**:
    *   **清晰的架构**: 严格遵循模块化、分层设计，通过 `modules`, `framework`, `config` 包分离业务、框架与配置。
    *   **类型安全的配置**: 使用 `@ConfigurationProperties` 将配置项封装为强类型Bean，提高可维护性和开发效率。
    *   **自定义校验注解**: 将业务校验逻辑从Service层解耦 (e.g., `@UniqueUsername`)。
    *   **统一枚举处理**: 通过通用接口和类型处理器，实现枚举在前后端及数据库中的优雅转换。
    *   **API文档**: 通过 Javadoc 和 SpringDoc 实现了完善、自动化的 API 文档。
*   **专业的开发实践**:
    *   **自动化测试**: 集成了 JUnit 5, Mockito (单元测试) 和 **Testcontainers** (集成测试)，确保在真实的 Docker 容器环境中进行可靠的测试。
    *   **容器化支持**: 提供了优化的多阶段 `Dockerfile` 和用于本地开发的 `docker-compose.yml`。
    *   **多环境配置**: 清晰地分离了 `local`, `test`, `prod`, `integration-test` 等环境的配置，遵循“一次构建，随处运行”原则。

## 🛠️ 技术栈

*   **核心框架**: Spring Boot 3, Spring Security 6, Spring Web MVC
*   **数据持久化**: Mybatis-Plus, MySQL Connector
*   **缓存**: Redis, Spring Cache
*   **安全**: JWT
*   **API文档**: SpringDoc (OpenAPI 3)
*   **测试**: JUnit 5, Mockito, Testcontainers
*   **容器化**: Docker, Docker Compose
*   **工具**: Lombok, MapStruct, Guava

## 🚀 快速开始

### 环境要求

*   JDK 17 或更高版本
*   Maven 3.6+
*   Docker
*   Docker Compose

### 本地开发环境运行

1.  **克隆项目**
    ```bash
    git clone <your-repository-url>
    cd simple-boot
    ```

2.  **启动依赖服务**
    项目根目录下的 `docker-compose.yml` 文件已经为您配置好了 **MySQL 8.0** 和 **Redis 7.0** 服务。只需在项目根目录下运行：
    ```bash
    docker-compose up -d
    ```
    此命令会在后台为您启动一个 MySQL 容器和一个 Redis 容器。数据库的初始表结构和数据将通过挂载的 `./script/db` 目录自动导入。

3.  **配置并运行应用**
    *   在 IntelliJ IDEA 中，打开 `src/main/resources/application-local.yml` 文件，确保其中的数据库密码与 `docker-compose.yml` 中设置的 `MYSQL_ROOT_PASSWORD` 一致。
    *   为 `SimpleApiApplication` 的运行配置添加 VM 选项 `-Dspring.profiles.active=local`，以激活本地开发环境配置。
    *   直接运行 `SimpleApiApplication.java` 的 `main` 方法。

### 运行自动化测试

本项目集成了完善的单元测试和集成测试。集成测试会自动使用 Testcontainers 启动临时的 Docker 容器来运行，确保测试环境的纯净。

在项目根目录下运行 Maven 命令即可：
```bash
mvn clean test
```

### 打包为生产环境 JAR

```bash
mvn clean package
```
打包成功后，可执行的 JAR 文件会生成在 `target/simple-0.0.1.jar`。

## 部署

本项目已通过 `Dockerfile` 完全容器化。

1.  **构建 Docker 镜像**
    在项目根目录下运行：
    ```bash
    docker build -t simple-api .
    ```

2.  **运行 Docker 容器**
    使用 `docker run` 命令启动应用。您可以通过 `-e` 参数传递环境变量来激活不同的配置文件。

    *   **以 `test` 环境运行**:
        ```bash
        docker run -d -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=test" --name simple-app-test simple-api
        ```

    *   **以 `prod` 环境运行**:
        ```bash
        docker run -d -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=prod" --name simple-app-prod simple-api
        ```
    **注意**: 在生产环境部署前，请确保 `application-prod.yml` 中的数据库和文件存储等配置已正确填写。

## 📖 API 文档

项目启动后，可以通过以下地址访问由 SpringDoc 自动生成的 Swagger UI 界面和 Actuator 监控端点：

*   **Swagger UI**: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
*   **Actuator Health**: [http://localhost:8080/api/actuator/health](http://localhost:8080/api/actuator/health)

## ⚙️ 配置

项目采用多环境配置文件的方式进行管理：
*   `application.yml`: 存放所有环境共享的通用配置。
*   `application-local.yml`: 本地开发环境配置。
*   `application-test.yml`: 测试服务器环境配置。
*   `application-prod.yml`: 生产环境配置。
*   `src/test/resources/application-integration-test.yml`: 自动化集成测试专用配置。

通过 `-Dspring.profiles.active=<profile>` 或 `SPRING_PROFILES_ACTIVE=<profile>` 环境变量来激活不同的配置。

## 🏗️ 项目结构

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/example/simple
│   │   │       ├── common          // 通用工具类、DTO、VO、BaseEnum
│   │   │       ├── config          // Spring 配置类 (按功能划分)
│   │   │       │   ├── cache, doc, mybatis, redis, security, web
│   │   │       │   └── properties  // 类型安全的配置属性类
│   │   │       ├── exception       // 全局异常处理
│   │   │       ├── framework       // 框架级横切关注点
│   │   │       │   ├── aop, async, idempotent, runner, security, validation, xss
│   │   │       └── modules         // **核心业务模块** (按领域划分)
│   │   │           ├── auth, config, dict, file, log, permission, role, user
│   │   └── resources
│   │       ├── mapper              // Mybatis-Plus Mapper XML
│   │       └── application.yml     // 配置文件
│   └── test                        // 自动化测试
│       ├── java
│       │   └── com/example/simple
│       │       ├── BaseIntegrationTest.java // Testcontainers 集成测试基类
│       │       ├── generator
│       │       └── modules
│       └── resources
│           └── application-integration-test.yml
├── script/db
│   └── simple.sql                  // 项目完整SQL脚本
├── .dockerignore
├── docker-compose.yml              // 本地开发环境编排
├── Dockerfile                      // 应用容器化构建文件
└── pom.xml                         // Maven 项目配置
```