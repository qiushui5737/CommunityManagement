# 智慧社区物业管理系统 — 后端

> 基于 Spring Boot 3 的 RESTful API 后端服务，为智慧社区物业管理系统提供完整的业务接口支撑。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | LTS 版本 |
| Spring Boot | 3.1.5 | RESTful 服务框架 |
| MyBatis-Plus | 3.5.8 | 增强 ORM，支持 CRUD 自动生成 |
| SQL Server | — | Microsoft 关系型数据库 |
| JWT (jjwt) | 0.12.3 | 无状态认证 |
| Spring Security Crypto | — | BCrypt 密码加密 |
| Lombok | 1.18.38 | 编译期代码生成 |
| HikariCP | 内置 | 高性能连接池 |

## 项目结构

```
src/main/java/com/community/
├── PropertyApplication.java       # 启动类
├── config/
│   ├── JwtUtil.java               # JWT Token 工具
│   ├── MybatisPlusConfig.java     # MyBatis-Plus 配置
│   ├── WebConfig.java             # CORS 跨域配置
│   ├── GlobalExceptionHandler.java # 全局异常处理
│   └── RateLimitFilter.java       # IP 限流过滤器
├── filter/
│   └── JwtAuthFilter.java         # JWT 认证过滤器
├── controller/                    # 17 个 Controller（~80+ API）
│   ├── AuthController.java        # 认证（登录/注册）
│   ├── UserController.java        # 用户管理
│   ├── PaymentBillController.java # 业主缴费
│   ├── AdminBillController.java   # 管理端账单
│   ├── FeeItemController.java     # 费用项目
│   ├── ParkingSpaceController.java# 车位管理
│   ├── FacilityController.java    # 设施管理
│   ├── FacilityBookingController.java # 设施借用
│   ├── RepairRequestController.java   # 报修管理
│   ├── FeedbackController.java    # 留言反馈
│   ├── AccessCardController.java  # 门禁卡管理
│   ├── AccessLogController.java   # 进出记录
│   ├── CommunityBuildingController.java # 楼栋管理
│   ├── CommunityUnitController.java     # 单元管理
│   ├── CommunityHouseController.java    # 房屋管理
│   ├── AnnouncementController.java      # 公告管理
│   └── FileUploadController.java        # 文件上传
├── entity/                        # 15 个实体类
├── mapper/                        # MyBatis Mapper 接口
└── service/                       # 业务逻辑层
```

## 环境要求

- **JDK 17+**
- **SQL Server** (2019+)
- **Maven** 3.6+（或使用内置 `mvnw`）

## 快速开始

### 1. 配置数据库

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=community_db;encrypt=true;trustServerCertificate=true;
    username: your_username
    password: your_password
```

### 2. 启动项目

```bash
# 使用 Maven Wrapper（无需安装 Maven）
./mvnw spring-boot:run        # Linux/Mac
.\mvnw.cmd spring-boot:run    # Windows

# 或使用 Maven
mvn spring-boot:run
```

启动成功后访问 `http://localhost:8081`。

### 3. 生产部署

```bash
# 打包
./mvnw clean package -DskipTests

# 运行（使用生产配置）
java -jar target/property-backend-1.0.0.jar \
  --spring.config.additional-location=./application-prod.yml \
  -Xms512m -Xmx1024m -XX:+UseG1GC
```

## 核心 API 概览

### 认证模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录 |
| POST | `/api/auth/register` | 用户注册 |
| GET | `/api/auth/info` | 获取当前用户信息 |

### 业主端
| 模块 | 接口前缀 | 核心功能 |
|------|---------|---------|
| 在线缴费 | `/api/owner/bills` | 账单查询、模拟支付 |
| 车位选购 | `/api/parking` | 楼栋列表、网格数据、购买、释放 |
| 设施借用 | `/api/facility-booking` | 提交申请、我的借用、归还 |
| 设施报修 | `/api/repair` | 提交报修、查询记录 |
| 留言反馈 | `/api/feedback` | 提交反馈、追加回复 |
| 个人中心 | `/api/users/owner` | 资料编辑、密码修改 |

### 管理端
| 模块 | 接口前缀 | 核心功能 |
|------|---------|---------|
| 数据概览 | `/api/admin/stats` | 统计指标 |
| 用户管理 | `/api/users` | CRUD、房屋绑定、状态切换 |
| 费用管理 | `/api/admin/fee-items` | 费用项目 CRUD |
| 账单管理 | `/api/admin/bills` | 批量生成、可收费对象查询 |
| 车位管理 | `/api/parking/admin` | CRUD、锁定/解锁、统计 |
| 楼栋管理 | `/api/buildings` | CRUD |
| 房屋管理 | `/api/houses` | CRUD、入住/退租 |
| 门禁卡 | `/api/access-card` | 发行、挂失、注销、统计 |
| 进出记录 | `/api/access-log` | 多维查询、统计概览 |

## 安全机制

- **JWT 无状态认证**：Token 有效期 24h，HMAC-SHA 签名
- **BCrypt 密码加密**：单向加密，防彩虹表攻击
- **IP 限流**：单 IP 每秒最多 50 次请求，超限返回 429
- **全局异常处理**：数据库异常返回 503，兜底 500 不泄漏堆栈
- **角色权限校验**：OWNER / ADMIN 角色隔离

## 数据库

系统使用 **15 张核心业务表**：

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户（业主/管理员） |
| `community_building` | 楼栋 |
| `community_unit` | 单元 |
| `community_house` | 房屋 |
| `parking_space` | 车位 |
| `property_fee_item` | 费用项目 |
| `payment_bill` | 缴费账单 |
| `facility` | 公共设施 |
| `facility_booking` | 借用申请 |
| `repair_request` | 报修工单 |
| `feedback` | 留言反馈 |
| `feedback_reply` | 反馈回复 |
| `access_card` | 门禁卡 |
| `access_log` | 进出记录 |
| `announcement` | 社区公告 |

## 配套前端

前端仓库：[community-frontend](https://github.com/qiushui5737/community-frontend)

## 许可证

本项目为课程设计作品，仅供学习使用。
