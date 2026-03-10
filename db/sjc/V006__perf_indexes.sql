-- V006：性能优化索引
ALTER TABLE sjc_inventory_flow
  ADD INDEX idx_flow_time_type_wh_mat (create_time, flow_type, warehouse_id, material_id);

ALTER TABLE sjc_dispatch_track
  ADD INDEX idx_track_task_ctime (task_id, create_time);
