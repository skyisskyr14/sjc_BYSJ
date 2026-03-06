# project 模块拆分说明

按业务域将 `dev-admin` 中 `com.sq.sjc` 代码拆分复制到 `spring/project` 下：

- `sjc-auth-app`：注册认证（你之前要求）
- `sjc-inventory-app`：物资/仓库/库存/库存流水/数据范围
- `sjc-alert-app`：预警
- `sjc-dispatch-app`：调度任务/轨迹/状态
- `sjc-dashboard-app`：看板统计
- `sjc-outbox-app`：事件外盒投递

> 说明：本次遵循你的要求，不改 `dev-admin` 原有 `sjc` 目录代码，仅在 `project` 下完成按域拆分。
