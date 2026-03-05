package com.sq.sjc.flink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

public class SjcRedisMetricSink {
    static final ObjectMapper M = new ObjectMapper();

    static Metric toMetric(String json) throws Exception {
        JsonNode n = M.readTree(json);
        long warehouseId = n.path("warehouseId").asLong();
        long materialId = n.path("materialId").asLong();
        String type = n.path("flowType").asText();
        double qty = n.path("qty").asDouble();
        return new Metric("sjc:inventory:material:" + warehouseId + ":" + materialId, type, qty);
    }

    static Metric writeRedis(Metric m) {
        try (Jedis jedis = new Jedis(System.getProperty("redis.host", "localhost"), Integer.getInteger("redis.port", 6379))) {
            if ("INBOUND".equals(m.type)) jedis.hincrByFloat(m.key, "qty_in", m.qty);
            if ("OUTBOUND".equals(m.type)) jedis.hincrByFloat(m.key, "qty_out", m.qty);
            jedis.hincrByFloat(m.key, "qty_net", "INBOUND".equals(m.type) ? m.qty : -m.qty);
            jedis.hset(m.key, "ts", String.valueOf(System.currentTimeMillis()));
        }
        return m;
    }

    static class Metric {
        String key; String type; double qty;
        Metric(String key, String type, double qty) { this.key = key; this.type = type; this.qty = qty; }
    }
}
