package com.sq.sjc.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SjcInventoryMetricsJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(System.getProperty("kafka.bootstrap", "localhost:9092"))
                .setTopics("sjc.inventory.flow")
                .setGroupId("sjc-flink-metrics")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "sjc-inventory-flow");
        stream.map(SjcRedisMetricSink::toMetric)
                .keyBy(v -> v.key)
                .map(SjcRedisMetricSink::writeRedis)
                .name("redis-metrics-sink");

        env.execute("SJC Inventory Metrics Job");
    }
}
