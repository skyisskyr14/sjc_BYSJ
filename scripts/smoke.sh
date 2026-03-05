#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:10438/api}"
COOKIE_JAR="$(mktemp)"

log(){ echo "[SMOKE] $*"; }
fail(){ echo "[SMOKE][FAIL] $*"; exit 1; }
json_get(){ python -c 'import json,sys;d=json.load(sys.stdin);print(d.get(sys.argv[1],""))' "$1"; }

request(){
  local method="$1" url="$2" data="${3:-}"
  if [[ -n "$data" ]]; then
    curl -sS -X "$method" "$BASE_URL$url" -H 'Content-Type: application/json' -d "$data" -c "$COOKIE_JAR" -b "$COOKIE_JAR"
  else
    curl -sS -X "$method" "$BASE_URL$url" -c "$COOKIE_JAR" -b "$COOKIE_JAR"
  fi
}

log "1) 健康检查"
HEALTH=$(curl -sS "$BASE_URL/actuator/health" || true)
[[ -n "$HEALTH" ]] || fail "health 接口不可用"

log "2) 仓库新增"
WH_RESP=$(request POST "/sjc/warehouse/create" '{"warehouseName":"冒烟仓库A","address":"测试地址","capacity":1000}')
[[ "$(echo "$WH_RESP" | json_get code)" == "200" ]] || fail "仓库新增失败: $WH_RESP"

log "3) 物资新增"
MAT_RESP=$(request POST "/sjc/material/create" '{"materialName":"冒烟物资A","materialType":"测试","unit":"箱","warnThreshold":5}')
[[ "$(echo "$MAT_RESP" | json_get code)" == "200" ]] || fail "物资新增失败: $MAT_RESP"

log "4) 查询库存列表（取首条做入/出库）"
INV_PAGE=$(request GET "/sjc/inventory/page?pageNum=1&pageSize=1")
[[ "$(echo "$INV_PAGE" | json_get code)" == "200" ]] || fail "库存查询失败: $INV_PAGE"
WAREHOUSE_ID=$(python - <<'PY' <<<"$INV_PAGE"
import json,sys
j=json.load(sys.stdin)
arr=((j.get('data') or {}).get('list') or [])
print(arr[0].get('warehouseId') if arr else '')
PY
)
MATERIAL_ID=$(python - <<'PY' <<<"$INV_PAGE"
import json,sys
j=json.load(sys.stdin)
arr=((j.get('data') or {}).get('list') or [])
print(arr[0].get('materialId') if arr else '')
PY
)
[[ -n "$WAREHOUSE_ID" && -n "$MATERIAL_ID" ]] || fail "缺少可用库存测试数据"

log "5) 入库"
IN_RESP=$(request POST "/sjc/inventory/inbound" "{\"warehouseId\":$WAREHOUSE_ID,\"materialId\":$MATERIAL_ID,\"qty\":2,\"remark\":\"smoke inbound\"}")
[[ "$(echo "$IN_RESP" | json_get code)" == "200" ]] || fail "入库失败: $IN_RESP"

log "6) 出库成功"
OUT_OK=$(request POST "/sjc/inventory/outbound" "{\"warehouseId\":$WAREHOUSE_ID,\"materialId\":$MATERIAL_ID,\"qty\":1,\"remark\":\"smoke outbound\"}")
[[ "$(echo "$OUT_OK" | json_get code)" == "200" ]] || fail "出库(成功场景)失败: $OUT_OK"

log "7) 出库失败断言（库存不足）"
OUT_FAIL=$(request POST "/sjc/inventory/outbound" "{\"warehouseId\":$WAREHOUSE_ID,\"materialId\":$MATERIAL_ID,\"qty\":999999,\"remark\":\"smoke outbound fail\"}")
[[ "$(echo "$OUT_FAIL" | json_get code)" != "200" ]] || fail "库存不足断言失败: $OUT_FAIL"

log "8) Dashboard 指标查询"
METRIC=$(request GET "/sjc/dashboard/metrics")
[[ "$(echo "$METRIC" | json_get code)" == "200" ]] || fail "dashboard指标失败: $METRIC"

log "9) 预警分页/确认流程（若存在UNCONFIRMED则确认）"
ALERTS=$(request GET "/sjc/alert/page?pageNum=1&pageSize=10&status=UNCONFIRMED")
[[ "$(echo "$ALERTS" | json_get code)" == "200" ]] || fail "预警分页失败: $ALERTS"
ALERT_ID=$(python - <<'PY' <<<"$ALERTS"
import json,sys
j=json.load(sys.stdin)
arr=((j.get('data') or {}).get('list') or [])
print(arr[0].get('id') if arr else '')
PY
)
if [[ -n "$ALERT_ID" ]]; then
  ACK=$(request POST "/sjc/alert/ack" "{\"id\":$ALERT_ID}")
  [[ "$(echo "$ACK" | json_get code)" == "200" ]] || fail "预警确认失败: $ACK"
fi

log "10) 调度任务状态流转 + 轨迹上报"
TASKS=$(request GET "/sjc/task/list")
[[ "$(echo "$TASKS" | json_get code)" == "200" ]] || fail "任务列表失败: $TASKS"
TASK_ID=$(python - <<'PY' <<<"$TASKS"
import json,sys
j=json.load(sys.stdin)
arr=(j.get('data') or [])
print(arr[0].get('id') if arr else '')
PY
)
if [[ -n "$TASK_ID" ]]; then
  for st in APPROVED RUNNING ARRIVED DONE; do
    ST=$(request POST "/sjc/dispatch/task/status" "{\"taskId\":$TASK_ID,\"toStatus\":\"$st\",\"remark\":\"smoke\"}")
    [[ "$(echo "$ST" | json_get code)" == "200" ]] || fail "任务状态流转失败($st): $ST"
  done
  TR=$(request POST "/sjc/dispatch/track/report" "{\"taskId\":$TASK_ID,\"longitude\":120.39,\"latitude\":36.07}")
  [[ "$(echo "$TR" | json_get code)" == "200" ]] || fail "轨迹上报失败: $TR"
fi

log "PASS: 冒烟链路通过"
rm -f "$COOKIE_JAR"
