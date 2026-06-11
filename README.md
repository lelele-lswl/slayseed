# 杀戮尖塔2种子分享网站

一个基于 Vue3 + Spring Boot 的杀戮尖塔2种子分享平台

## 技术栈

- **后端**: Spring Boot 4.0.6 + Java 17 + JPA + H2 Database
- **前端**: Vue 3 + Vite + Axios

## 项目结构

`
SlaySeed/
├── src/
│   └── main/
│       ├── java/org/lelele/slayseed/
│       │   ├── controller/      # REST控制器
│       │   ├── entity/          # 实体类
│       │   ├── repository/      # 数据访问层
│       │   └── service/         # 业务逻辑层
│       └── resources/           # 配置文件
└── frontend/                    # Vue3前端项目
`

## 功能特性

- ✅ 分享新种子（种子码、角色、描述、标签）
- ✅ 浏览所有种子
- ✅ 按角色筛选种子
- ✅ 查看热门种子（按点赞排序）
- ✅ 点赞功能
- ✅ 浏览次数统计
- ✅ 美观的深色主题UI

## 运行项目

### 1. 启动后端

`ash
# 在项目根目录运行
mvnw spring-boot:run
`

后端将在 http://localhost:8080 启动

### 2. 启动前端

`ash
cd frontend
npm install
npm run dev
`

前端将在 http://localhost:3000 启动

## API 接口

- GET /api/seeds - 获取所有种子
- GET /api/seeds/character/{character} - 按角色获取种子
- GET /api/seeds/top - 获取热门种子
- GET /api/seeds/{id} - 获取单个种子详情
- POST /api/seeds - 创建新种子
- POST /api/seeds/{id}/like - 点赞种子

## H2 数据库控制台

访问 http://localhost:8080/h2-console

- JDBC URL: jdbc:h2:file:./data/slayseed
- 用户名: sa
- 密码: (留空)
