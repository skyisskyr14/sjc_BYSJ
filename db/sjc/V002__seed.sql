-- 执行顺序：先执行 V001__init.sql，再执行 V002__seed.sql
INSERT INTO sjc_role(role_code, role_name, role_desc) VALUES
('SYS_ADMIN','系统管理员','系统全量管理权限'),
('DISPATCHER','应急调度员','调度任务管理'),
('WAREHOUSE_ADMIN','仓库管理员','仓储与库存管理'),
('VIEWER','查看者','只读访问')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), role_desc=VALUES(role_desc);

INSERT INTO sjc_warehouse(id, warehouse_name, address, longitude, latitude, contact_name, contact_phone, capacity)
VALUES
(1,'市南主仓','青岛市市南区应急路1号',120.382600,36.067100,'王仓管','13800000001',50000),
(2,'市北分仓','青岛市市北区民安路8号',120.377400,36.111200,'李仓管','13800000002',30000)
ON DUPLICATE KEY UPDATE warehouse_name=VALUES(warehouse_name), address=VALUES(address), capacity=VALUES(capacity);

INSERT INTO sjc_material(id, material_name, material_type, spec, unit, shelf_life_days, warn_threshold)
VALUES
(1,'矿泉水','生活保障','550ml*24瓶','箱',365,80),
(2,'医用口罩','医疗防护','50只/盒','盒',1095,120),
(3,'应急帐篷','安置救援','4-6人','顶',3650,20),
(4,'方便面','生活保障','24桶/箱','箱',180,60)
ON DUPLICATE KEY UPDATE material_name=VALUES(material_name), warn_threshold=VALUES(warn_threshold);

INSERT INTO sjc_inventory(id, warehouse_id, material_id, qty_total, qty_available, qty_locked, last_change_time)
VALUES
(1,1,1,220,220,0,NOW()),
(2,1,2,90,90,0,NOW()),
(3,1,3,36,36,0,NOW()),
(4,2,1,150,150,0,NOW()),
(5,2,4,40,40,0,NOW())
ON DUPLICATE KEY UPDATE qty_total=VALUES(qty_total), qty_available=VALUES(qty_available), last_change_time=NOW();

INSERT INTO sjc_inventory_flow(biz_no, warehouse_id, material_id, flow_type, status, qty, before_qty, after_qty, remark, create_time)
VALUES
(CONCAT('SEED-IN-',UNIX_TIMESTAMP(),'-1'),1,1,'INBOUND','DONE',220,0,220,'初始化入库',NOW()-INTERVAL 2 DAY),
(CONCAT('SEED-IN-',UNIX_TIMESTAMP(),'-2'),1,2,'INBOUND','DONE',90,0,90,'初始化入库',NOW()-INTERVAL 2 DAY),
(CONCAT('SEED-OUT-',UNIX_TIMESTAMP(),'-1'),2,4,'OUTBOUND','DONE',10,50,40,'历史出库',NOW()-INTERVAL 1 DAY);

INSERT INTO sjc_alert(alert_type, alert_level, status, warehouse_id, material_id, trigger_value, threshold_value, alert_message, create_time)
VALUES
('LOW_STOCK','HIGH','UNCONFIRMED',1,2,90,120,'市南主仓-医用口罩库存低于阈值',NOW()-INTERVAL 1 HOUR);

INSERT INTO sjc_dispatch_task(task_no, from_warehouse_id, demand_point_name, demand_address, demand_longitude, demand_latitude, status, progress_percent, planned_depart_time, planned_arrive_time, remark)
VALUES
('TASK-DEMO-001',1,'临时安置点A','青岛市崂山区安置点A',120.510100,36.129900,'PENDING',0,NOW()+INTERVAL 1 HOUR,NOW()+INTERVAL 2 HOUR,'演示任务');

INSERT INTO sjc_dispatch_task_item(task_id, material_id, qty, unit)
SELECT id, 1, 20, '箱' FROM sjc_dispatch_task WHERE task_no='TASK-DEMO-001' LIMIT 1;
