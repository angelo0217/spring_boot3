package com.example.demo;


import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;

public class TestJavaCdc {

    @Test
    public void testObj() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        MySqlSource<String> mysqlSource = MySqlSource.<String>builder()
                .hostname("127.0.0.1")
                .port(3307)
                .databaseList("mydb")
                .tableList("mydb.books")
                .username("root")
                .password("Java1234!")
                .serverTimeZone("UTC")
                .serverId("2")
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        env.enableCheckpointing(3000);

        DataStreamSource<String> mysqlDS = env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "CDC");

        mysqlDS.map(value -> {
            System.out.println("cccc: "+ value);
            return value;
        }).print();

        env.execute("MySQL Binlog Listener");
    }
}