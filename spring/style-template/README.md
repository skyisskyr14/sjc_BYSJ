# style-template（最终版模板，不参与构建）

> 用途：作为本仓库后端模块的“代码风格样板间”，方便你后续新项目/新模块直接复制，并让 Codex 快速对齐你的开发习惯。

## 1. 模板定位
- 本目录默认不加入 `spring/pom.xml` 的 `<modules>`，不会参与打包与运行。
- 内容覆盖你当前项目的主流后端分层：
  - `controller`
  - `model`
  - `repository`
  - `entity`
  - `dto`
  - `vo`
- 风格目标：
  - 使用 `ResponseResult`（你习惯口头叫 RequestResult）统一响应
  - 使用 `@DS("zxq")` 标识业务库 这个根据项目名字来 后文出现zxq同理
  - 使用 `@RestController + /fd/*` 路由风格
  - Model 层集中承载校验/组装/业务流程
  - Controller 标准加审计注解：`@UserLog` / `@AdminLog`
  - 通过 `UserTokenContextHolder` 获取 `UserEntity`，再映射业务库用户（`zxq.user`）

---

## 2. 推荐目录结构（复制即用）

```text
src/main/java/com/sq/{module}/
  controller/
  dto/
  entity/
  model/
  repository/
  vo/
```

建议命名保持一致：
- Controller：`ZxyXxxController`
- Model：`ZxyXxxModel`
- Repository：`ZxyXxxRepository`
- Entity：`ZxyXxxEntity`
- DTO：`ZxyXxxSaveDto`
- VO：`ZxyXxxVo`

---

## 3. 业务代码风格约定（可作为生成提示词）

1) **Controller 只做 4 件事**
- 参数接收
- 轻量参数判空
- 调用 Model
- 返回 `ResponseResult`

并保持你项目常见写法：
- 用户端接口打 `@UserLog(action=..., module=...)`
- 管理端接口打 `@AdminLog(action=..., module=...)`
- 当前用户从 `UserTokenContextHolder.get()` 读取
- 业务用户 ID 通过 `zxyUserRepository.getUserBySysId(user.getId())` 映射

2) **Model 负责核心业务**
- 参数合法性校验
- 业务规则判断
- 数据库写入/更新
- Entity -> VO 转换
- 上下文用户映射（`sys_user.id -> zxq.user.sys_id -> zxq.user.id`）

3) **Repository 使用 MyBatis-Plus + 注解 SQL**
- 公共 CRUD：`BaseMapper`
- 常用查询：`@Select`
- 数据源：`@DS("zxq")`

4) **统一字段风格**
- 逻辑删除：`is_delete / isDelete`
- 创建时间：`create_time / createTime`
- 更新时间：`update_time / updateTime`

---

## 4. 建议接入项（按需打开）

你在真实模块中可按业务场景逐步接入：
- 登录态：`UserTokenContextHolder` / `TokenContextHolder`
- 审计注解：`@UserLog` / `@AdminLog`
- 分页：`PageRequest` + `PageResult`
- 权限：`@PermissionLimit`
- 文件上传：`MultipartFile` + 统一静态目录

### 必加风格（你项目当前高频）
1. Controller 方法打日志注解（用户端 `@UserLog`，管理端 `@AdminLog`）。
2. 一律返回 `ResponseResult.success/fail`。
3. 用上下文拿系统用户：
   - `UserEntity user = UserTokenContextHolder.get();`
4. 再映射业务用户：
   - `ZxyDemoBizUserEntity zxyUser = zxyDemoBizUserRepository.getUserBySysId(user.getId());`
   - 后续业务统一使用 `zxyUser.getId()`。

---

## 5. 从模板创建新模块的最小步骤

1. 复制本目录下 `controller/model/repository/dto/entity/vo`。
2. 按业务重命名为 `ZxyXxx*`。
3. 把 `@TableName("demo_table")` 改为真实表名。
4. 补充 Repository 查询 SQL。
5. 在 Model 中补齐业务校验逻辑。
6. 在 Controller 暴露 `list/detail/create/update/delete`。

---

## 6. 说明

本模板示例现在已包含完整 CRUD（列表、详情、新增、更新、删除）与常见校验写法，并补齐了你要求的：
- `@UserLog/@AdminLog`
- `UserTokenContextHolder -> 业务用户ID映射`
- `ResponseResult` 统一消息结构

可直接作为“项目最终风格模板”。
