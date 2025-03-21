package ntsc.cas.cn.test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class NormalProducer {
    final static Logger logger = LoggerFactory.getLogger(NormalProducer.class);

    public static void main(String[] args) {
        // create Producer properties
        final Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.108:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        final KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // create a producer record
        final ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test", "hello world");

        // send data - asynchronous
        producer.send(producerRecord);
        logger.info("Record send");

        // flush data - synchronous
        producer.flush();

        // flush and close producer
        producer.close();
    }
}
