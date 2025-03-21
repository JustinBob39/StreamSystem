package ntsc.cas.cn.test;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class NormalConsumer {
    final static Logger logger = LoggerFactory.getLogger(NormalConsumer.class);

    public static void main(String[] args) {
        // create Consumer properties
        final Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.108:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "normal-consumer");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // create the consumer
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton("test"));

        // receive data
        while (true) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
            for (final ConsumerRecord<String, String> record : records) {
                logger.info("Key: {}, Value: {}", record.key(), record.value());
                logger.info("Partition: {}, Offset:{}", record.partition(), record.offset());
            }
        }
    }
}
