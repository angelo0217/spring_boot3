package com.example.demo;


import com.example.demo.utils.JsonUtil;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.RotateEventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventMetadata;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.google.gson.Gson;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;

public class TestJavaCdc {

    private String convertRowsToJson(Serializable[] row, long tableId) {
        // 假設這裡使用 Gson 將 rows 轉換為 JSON 字符串
        var columns = tableMap.get(tableId);
        Map<String, Serializable> rowData = new LinkedHashMap<>();
        for (int i = 0; i < row.length; i++) {
            rowData.put(columns.get(i), row[i]);
        }
        return convertToJson(rowData);
    }

    Map<Long, List<String>> tableMap = new HashMap<>();
    private String convertToJson(Map<String, Serializable> data) {
//        Gson gson = new Gson();
//        return gson.toJson(data);
        return JsonUtil.objectToJson(data);
    }

    public void printJson(List<Serializable[]> rows, long tableId){
        for (Serializable[] row : rows) {
            var json = convertRowsToJson(row, tableId);
            System.out.println(json);
        }
    }

    @Test
    public void testBinlog() throws IOException {
        var client = new BinaryLogClient("127.0.0.1", 3308, "root", "Java1234!");
        client.setBinlogFilename("mysql-bin.000003");
        client.setBinlogPosition(4);
        client.setServerId(1);

        client.registerEventListener(event -> {
            var data = event.getData();
            if (data instanceof TableMapEventData) {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                var metadata = (TableMapEventMetadata) tableMapEventData.getEventMetadata();
                tableMap.put(tableMapEventData.getTableId(), metadata.getColumnNames());
                return;
            }
            if (event.getData() instanceof RotateEventData) {
                var rotateEventData = (RotateEventData) event.getData();
                System.out.println("**** file: " + rotateEventData.getBinlogFilename());
            }
            EventHeaderV4 header = event.getHeader();


            if (data instanceof UpdateRowsEventData) {
                var eventData = (UpdateRowsEventData) data;
                var updateRowsEventData = (UpdateRowsEventData) data;
                List<Map.Entry<Serializable[], Serializable[]>> rows = updateRowsEventData.getRows();

                for (Map.Entry<Serializable[], Serializable[]> row : rows) {
                    Serializable[] key = row.getKey();
                    Serializable[] value = row.getValue();
                    String beforeJson = convertRowsToJson(key, eventData.getTableId());
                    String afterJson = convertRowsToJson(value, eventData.getTableId());
                    System.out.println("Before update: " + beforeJson);
                    System.out.println("After update: " + afterJson);
                }
                System.out.println("===update:" + header.getPosition());
                System.out.println(data);
            } else if (data instanceof WriteRowsEventData) {
                var eventData = (WriteRowsEventData) data;
                printJson(eventData.getRows(), eventData.getTableId());
                System.out.println("===insert" + header.getPosition());
                System.out.println(data);
            } else if (data instanceof DeleteRowsEventData) {
                var eventData = (DeleteRowsEventData) data;
                printJson(eventData.getRows(), eventData.getTableId());
                System.out.println("===delete" + header.getPosition());
                System.out.println(data);
            }
        });

        client.connect(); // 移至此行
    }

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