# 第二步MVP实现记录

## 阶段(1) DB打底
- 复用样板：`spring/style-template` 的实体字段风格（`is_delete/create_time/update_time`）与业务库注释习惯。
- 产物：`db/sjc/V001__init.sql`、`db/sjc/V002__seed.sql`。
- 执行顺序：`V001 -> V002`。

## 阶段(2) 后端接口
- 复用样板：`ZxyDemoController` + `ZxyDemoModel` + `ZxyDemoRepository` 分层模板。
- 对齐点：
  - Controller 只接参/调用 Model/返回 `ResponseResult`
  - Repository 使用 `@Mapper + BaseMapper + @DS("zxq")`
  - 分页统一 `PageResult`
  - 出库不足通过 `BizException` 抛出并统一处理

## 阶段(2.5) 实时能力
- 复用样板：`system/ws` 的 `@ServerEndpoint` 写法。
- 产物：`/ws/sjc` 实时推送端点；消息结构 `{type,data,ts}`。
- 推送类型：`INVENTORY_METRICS_UPDATED`、`ALERT_CREATED`。

## 阶段(3) 前端页面
- 复用样板：仓库现有 Vue2 路由/Vuex 结构；接口统一通过 `src/api/* + src/utils/request.js`。
- 产物：dashboard、inventory/list、alert/list、task/list。
