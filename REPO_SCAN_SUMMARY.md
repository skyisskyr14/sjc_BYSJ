# 仓库扫描摘要（第一阶段）

> 扫描时间：2026-03-05
> 范围：`/workspace/sjc_BYSJ`

## 1. 顶层结构结论

仓库当前仅包含三大业务相关目录：

- `spring/`：后端多模块 Maven 工程（可运行核心）
- `web/`：Vue2 前端工程（当前为初始化模板）
- `db/`：**当前不存在**（后续需要新建并按版本化 SQL 落地）

另有 `.git/`、`spring/out/`、`web/node_modules/` 等构建/依赖产物目录。

---

## 2. db/ 目录现状与 SQL 结论

- 当前仓库内不存在 `db/` 目录。
- 全仓 `*.sql` 文件扫描结果为空。
- 因此目前没有可复用的仓库内 SQL 版本脚本，后续需按约定新增：
  - `db/sjc/V001__init.sql`
  - `db/sjc/V002__seed.sql`
  - 后续增量版本脚本

> 说明：`sq_system` 库并非通过本仓库 SQL 提供，现阶段只能从运行配置与实体/仓储推断其已在外部存在。

---

## 3. web/ 当前结构与页面/路由状态

### 3.1 工程形态

- Vue 2.6 + Vue Router 3 + Vuex 3。
- 依赖仍是 Vue CLI 初始化状态，尚未接入 ElementUI、ECharts、高德地图、axios 封装、WebSocket 封装。

### 3.2 目录与文件

`web/src` 当前仅有：

- `main.js`
- `App.vue`
- `router/index.js`
- `store/index.js`
- `views/HomeView.vue`
- `views/AboutView.vue`
- `components/HelloWorld.vue`

### 3.3 路由与页面

- 仅存在两个路由：
  - `/` -> `HomeView`
  - `/about` -> `AboutView`
- 页面内容为 Vue 脚手架示例，未见业务页面。

---

## 4. spring/ 多模块结构与启动关系

## 4.1 聚合结构

- 顶层聚合：`spring/pom.xml`
- 已纳入构建模块：
  - `system/admin-core`
  - `system/auth-app`
  - `system/captcha-core`
  - `system/common`
  - `system/framework`
  - `system/generator`
  - `system/platform-registry`
  - `system/security`
  - `system/user-core`
  - `system/ws`
  - `dev-admin`
- `style-template` 未纳入 Maven modules（符合“模板不参与构建”定位）。
- `project/pom.xml` 存在，但 `<modules>` 为空，尚未承载业务模块。

### 4.2 启动模块

- 当前可运行启动模块为 `spring/dev-admin`，启动类：`com.sq.SQApplication`。
- `@SpringBootApplication(scanBasePackages = "com.sq")`，可扫描 system 与后续 project 下模块（前提是模块被聚合并引入依赖）。

### 4.3 配置与多数据源

- `application.yml` 使用 dynamic-datasource：
  - 主库：`system` -> `sq_system`
  - 业务库：`zxq` -> 指向数据库名 `sjc`
- 说明：当前命名仍是 `zxq`，但 URL 已连接 `sjc`，后续业务模块应遵循现有 `@DS("zxq")` 约定，避免破坏现状。

---

## 5. style-template 精读结论（后端规范基线）

> 说明：当前 style-template 已有 README + 完整示例代码（controller/model/repository/entity/dto/vo），可直接复制模式。

### 5.1 包命名约定

- 根包模式：`com.sq.{模块}`。
- 示例模板包：`com.sq.style.*`。
- 推荐业务模块结构：
  - `controller`
  - `model`
  - `repository`
  - `entity`
  - `dto`
  - `vo`

### 5.2 类命名约定

- Controller：`XxxController`
- Model：`XxxModel`
- Repository：`XxxRepository`
- Entity：`XxxEntity`
- DTO：`XxxSaveDto` / `XxxQueryDto`
- VO：`XxxVo`

### 5.3 分层职责

- Controller：只做参数接收、轻校验、调用 Model、包装返回。
- Model：业务规则、校验、流程编排、Entity/VO 转换。
- Repository：MyBatis-Plus `BaseMapper` + 注解 SQL。
- Entity：与表一一映射。
- DTO/VO：输入输出对象分离。

### 5.4 统一返回体

- 使用 `ResponseResult<T>`，结构：`code/message/data`。
- 常见调用：`ResponseResult.success(...)`、`ResponseResult.fail(...)`。

### 5.5 异常体系

- 基础业务异常：`BizException`（含 `code` 字段，默认 400）。
- 模板示例中更多采用 Model 返回错误字符串 + Controller `fail` 的轻量方式；后续可在关键失败场景引入 `BizException` 并统一拦截（需与现有 system 机制保持一致）。

### 5.6 分页封装

- 分页请求：`PageRequest{pageNum,pageSize}`。
- 分页返回：`PageResult{list,total,pageNum,pageSize}`，支持从 MP 的 `IPage` 快速转换。

### 5.7 DTO/VO 使用习惯

- DTO 仅承载入参。
- VO 仅承载出参。
- Model 内进行 Entity -> VO 映射（当前风格偏手写转换）。

### 5.8 Mapper/Repository 写法

- `@Mapper` + `BaseMapper<Entity>`。
- 数据源注解：`@DS("zxq")`（业务库）。
- 自定义查询使用 `@Select`，SQL 直接注解在 Repository 接口。

### 5.9 枚举/常量组织方式

- system 中已有按模块的 enum 目录（如 ws 的 `MsgType`/`MsgSide`）。
- style-template 未提供常量专包示例，建议跟随现有模块习惯按业务模块建立 `enums`（必要时）并保持局部聚合。

### 5.10 Controller 风格

- `@RestController` + `@RequestMapping("/fd/*")`。
- OpenAPI 注解：`@Tag` + `@Operation`。
- 用户上下文：`UserTokenContextHolder.get()` 获取系统用户。
- 日志审计注解：
  - 用户侧：`@UserLog`
  - 管理侧：`@AdminLog`

### 5.11 日志与鉴权/拦截器风格

- 日志：AOP 通过 `UserLogAspect`/`AdminLogAspect` 做操作审计。
- 鉴权与安全拦截器位于 `system/security`：
  - `TokenInterceptor`
  - `PermissionInterceptor`
  - `FloodInterceptor`
  - `IpAccessInterceptor`
  - `ProjectContextInterceptor`
  - `MaintenanceTimeInterceptor`
- 配套上下文：`TokenContextHolder`、`UserTokenContextHolder`、`ProjectContextHolder`。

---

## 6. 差距清单（对照目标系统）

1. 缺少 `db/` 及全部 SQL 版本脚本。
2. 前端仍是脚手架默认页，目标业务路由/页面均未落地。
3. `spring/project` 尚为空，未承载 sjc 业务模块。
4. 尚未见 Kafka/Flink/Redis 实时链路的业务实现（system 中仅有通用 redis 能力与现有 ws 业务）。
5. 尚未见针对“物资/库存/调度/预警/报表”的 API 与实体。

---

## 7. 里程碑计划拆分（下一阶段执行）

### M1：基线搭建（后端骨架 + SQL v1）

- 在 `db/sjc` 新建 `V001__init.sql` + `V002__seed.sql`。
- 在 `spring/project` 新增 sjc 聚合与首批子模块（按 style-template 复制结构）。
- 落地基础实体：物资字典、仓库、库存、流水、调度任务、预警、角色扩展。
- 打通基础 CRUD + 分页 + 统一返回。

### M2：系统能力接入（鉴权/角色/审计）

- 复用 `sq_system` 登录态与 token。
- 新增 sjc 用户角色扩展表（关联 `sq_system.user.id`）。
- 接口加 `@UserLog/@AdminLog` 与必要 `@PermissionLimit`。

### M3：实时闭环最小可用（模拟链路）

- 后端实现模拟事件发生器（入库/出库/任务进度）。
- Redis 缓存实时指标；WebSocket 推送库存/预警/任务进度三类事件。
- 前端接入 `ws.js` + Vuex 实时状态。

### M4：前端业务化重构

- 接入 ElementUI + axios 封装 + 统一布局。
- 新增 `src/api/{auth,material,warehouse,inventory,task,alert,dashboard}.js`。
- 实现六大页面：dashboard/inventory/task/report/alert/map。
- 完成筛选、分页、弹窗、抽屉、状态流转、导出。

### M5：流处理扩展点（Kafka/Flink/ClickHouse）

- 增加 Kafka Producer 与 topic 规范。
- 增加 Flink 本地 job（分钟库存、小时趋势、阈值预警）。
- 增加 ClickHouse 持久化接口与配置开关（无环境时 MySQL 兜底）。

### M6：联调、验收与文档

- 完成后端编译与关键接口自测。
- 前端构建与主要流程联调。
- 输出运行 README（含 sq_system 导入说明、sjc SQL 执行顺序、启动命令）。
