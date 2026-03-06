-- 注意：本文件是 ClickHouse DDL，不可在 MySQL 客户端执行。
-- 建议在 ClickHouse 中执行：clickhouse-client --multiquery < db/ch/V001__inventory_history.sql

CREATE TABLE IF NOT EXISTS sjc_inventory_flow_history (
  event_time DateTime,
  warehouse_id UInt64,
  material_id UInt64,
  flow_type String,
  qty Float64,
  before_qty Float64,
  after_qty Float64,
  biz_no String
)
ENGINE = MergeTree
PARTITION BY toDate(event_time)
ORDER BY (warehouse_id, material_id, event_time);
