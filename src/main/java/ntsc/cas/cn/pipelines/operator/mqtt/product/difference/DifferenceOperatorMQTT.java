package ntsc.cas.cn.pipelines.operator.mqtt.product.difference;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import ntsc.cas.cn.avro.difference.oneRound.OneRound;
import ntsc.cas.cn.avro.difference.oneRound.SingleStation;
import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.BinaryConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.BinaryConverterFactory;
import ntsc.cas.cn.pipelines.operator.mqtt.OperatorMQTT;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class DifferenceOperatorMQTT implements OperatorMQTT {
    final static Logger logger = LoggerFactory.getLogger(DifferenceOperatorMQTT.class);
    // config
    static final String sourceTopic = "Forwarder-Operator_Difference";
    static final String sinkTopic = "Operator-MQTT_Difference";
    static final DataType type = DataType.Difference; // binary need
    static final String applicationId = "MQTT-Difference";
    static final String mqttPrefix = "difference/";

    @Override
    public void startOperatorMQTT() {
        Properties props = new Properties();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("operator.properties");
            props.load(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        final StreamsBuilder builder = new StreamsBuilder();

        // source
        final Serde<OneRound> sourceValueSpecificAvroSerde = new SpecificAvroSerde<>();
        final String url = (String) props.get(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG);
        final Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, url);
        sourceValueSpecificAvroSerde.configure(serdeConfig, false);
        final KStream<String, OneRound> source = builder.stream(sourceTopic, Consumed.with(Serdes.String(), sourceValueSpecificAvroSerde));

        // sink
        source.flatMap((oldKey, message) -> message.getAllStations().stream().map(station -> new KeyValue<>(mqttPrefix + station.getParentId(), DifferenceOperatorMQTT.convertSingleStationToBinary(station))).collect(Collectors.toList())).to((key, value, recordContext) -> sinkTopic + "_" + key.substring(key.indexOf('/') + 1), Produced.with(Serdes.String(), Serdes.ByteArray()));

        // start
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static byte[] convertSingleStationToBinary(final SingleStation station) {
        final BinaryConverter converter = BinaryConverterFactory.createConverter(type);
        assert (converter != null);
        logger.info("Station {} convert to binary", station.getParentId());
        return converter.convertToBinary(station, type);
    }

    private DifferenceOperatorMQTT() {

    }

    private static class SingletonHolder {
        private static final DifferenceOperatorMQTT instance = new DifferenceOperatorMQTT();
    }

    public static DifferenceOperatorMQTT getInstance() {
        return SingletonHolder.instance;
    }
}
