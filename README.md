# english-data-manager

英语学习系统后端服务，同时为后台管理系统和 App 客户端提供接口数据。

## 技术栈

- Java 17
- Spring Boot 3.2.5
- MyBatis-Plus 3.5.7
- MySQL
- Redis
- Kafka
- Maven

## 功能说明

- 后台管理接口：管理员登录、数据看板、分类管理、推荐管理、视频管理、字幕管理、视频内容管理、用户管理、订单管理、角色菜单权限、权限分组、系统配置、会员套餐、文件上传等。
- App 客户端接口：账号密码登录、短信登录、首页数据、视频列表和详情、收藏数据、播放记录、用户资料、权限分组信息、会员套餐列表、订单创建、订单查询等。
- 开放接口：支付回调。

## 启动方式

1. 创建 MySQL 数据库，并执行 `src/main/resources/db/schema.sql`。
2. 将 `src/main/resources/application.example.yml` 复制为 `src/main/resources/application.yml`。
3. 根据本地环境修改数据库、Redis、Kafka、文件存储和认证相关配置。
4. 启动服务：

```bash
mvn spring-boot:run
```

## 默认账号

- 后台管理员：`admin / admin123`
- App 用户：`13800000000 / user123456`

## Swagger

- UI: `http://127.0.0.1:8080/swagger-ui/index.html`
- OpenAPI: `http://127.0.0.1:8080/v3/api-docs`

## 注意事项

- Token 存储在 Redis 中。
- 视频创建和更新事件会发送到 Kafka 主题 `video-changed-topic`。
- 支付回调会根据用户购买的会员套餐时长升级用户 VIP 状态。
- 文件上传使用存储服务抽象，当前实现为本地文件存储。
