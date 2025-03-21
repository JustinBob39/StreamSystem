package ntsc.cas.cn.pipelines.operator.influxdb.product.difference;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import ntsc.cas.cn.avro.difference.oneRound.OneRound;
import ntsc.cas.cn.avro.difference.oneRound.SingleStation;
import ntsc.cas.cn.avro.difference.singleStationLine.SingleStationLine;
import ntsc.cas.cn.pipelines.operator.influxdb.OperatorInfluxDB;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class DifferenceOperatorInfluxDB implements OperatorInfluxDB {
    final static Logger logger = LoggerFactory.getLogger(DifferenceOperatorInfluxDB.class);
    // config
    static final String sourceTopic = "Forwarder-Operator_Difference";
    static final String sinkTopic = "Operator-InfluxDB_Difference";
    static final String applicationId = "InfluxDB-Difference";

    @Override
    public void startOperatorInfluxDB() {
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
        final Serde<SingleStationLine> sinkValueSpecificAvroSerde = new SpecificAvroSerde<>();
        sinkValueSpecificAvroSerde.configure(serdeConfig, false);
        source.flatMapValues(message -> message.getAllStations().stream().map(DifferenceOperatorInfluxDB::convertSingleStationToLine).collect(Collectors.toList())).to(sinkTopic, Produced.with(Serdes.String(), sinkValueSpecificAvroSerde));

        // start
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static String convertUTCToString(final Instant instant) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss").withZone(ZoneId.of("UTC"));
        return formatter.format(instant);
    }

    private static SingleStationLine convertSingleStationToLine(final SingleStation station) {
        final SingleStationLine.Builder builder = SingleStationLine.newBuilder();

        builder.setTags(Collections.singletonMap("parentId", String.valueOf(station.getParentId())));

        builder.setFrameStatus(station.getFrameStatus().name());
        builder.setParentEventTime(convertUTCToString(station.getParentEventTime()));
        builder.setParentStatus(station.getParentStatus().name());

        builder.setFirstChildId(station.getChildren().get(0).getChildId());
        builder.setFirstChildEventTime(convertUTCToString(station.getChildren().get(0).getChildEventTime()));
        String dur = station.getChildren().get(0).getChildDuration().name();
        builder.setFirstChildDuration(dur.substring(1));

        final Conversions.DecimalConversion conversion = new Conversions.DecimalConversion();
        final Schema allStationsSchema = OneRound.getClassSchema().getField("allStations").schema().getElementType();
        final Schema childrenSchema = allStationsSchema.getField("children").schema().getElementType();
        final LogicalType typeFirst = childrenSchema.getField("childValueFirst").schema().getLogicalType();
        final LogicalType typeSecond = childrenSchema.getField("childValueSecond").schema().getLogicalType();
        ByteBuffer childValueFirst = station.getChildren().get(0).getChildValueFirst();
        ByteBuffer childValueSecond = station.getChildren().get(0).getChildValueSecond();
        BigDecimal decimalFirst = conversion.fromBytes(childValueFirst, null, typeFirst);
        BigDecimal decimalSecond = conversion.fromBytes(childValueSecond, null, typeSecond);
        builder.setFirstChildValueFirstFloat(decimalFirst.floatValue());
        builder.setFirstChildValueFirstString(decimalFirst.toPlainString());
        builder.setFirstChildValueSecondFloat(decimalSecond.floatValue());
        builder.setFirstChildValueSecondString(decimalSecond.toPlainString());

        builder.setSecondChildId(station.getChildren().get(1).getChildId());
        builder.setSecondChildEventTime(convertUTCToString(station.getChildren().get(1).getChildEventTime()));
        dur = station.getChildren().get(1).getChildDuration().name();
        builder.setSecondChildDuration(dur.substring(1));
        childValueFirst = station.getChildren().get(1).getChildValueFirst();
        childValueSecond = station.getChildren().get(1).getChildValueSecond();
        decimalFirst = conversion.fromBytes(childValueFirst, null, typeFirst);
        decimalSecond = conversion.fromBytes(childValueSecond, null, typeSecond);
        builder.setSecondChildValueFirstFloat(decimalFirst.floatValue());
        builder.setSecondChildValueFirstString(decimalFirst.toPlainString());
        builder.setSecondChildValueSecondFloat(decimalSecond.floatValue());
        builder.setSecondChildValueSecondString(decimalSecond.toPlainString());

        builder.setThirdChildId(station.getChildren().get(2).getChildId());
        builder.setThirdChildEventTime(convertUTCToString(station.getChildren().get(2).getChildEventTime()));
        dur = station.getChildren().get(2).getChildDuration().name();
        builder.setThirdChildDuration(dur.substring(1));
        childValueFirst = station.getChildren().get(2).getChildValueFirst();
        childValueSecond = station.getChildren().get(2).getChildValueSecond();
        decimalFirst = conversion.fromBytes(childValueFirst, null, typeFirst);
        decimalSecond = conversion.fromBytes(childValueSecond, null, typeSecond);
        builder.setThirdChildValueFirstFloat(decimalFirst.floatValue());
        builder.setThirdChildValueFirstString(decimalFirst.toPlainString());
        builder.setThirdChildValueSecondFloat(decimalSecond.floatValue());
        builder.setThirdChildValueSecondString(decimalSecond.toPlainString());

        logger.info("Station {} convert to line", station.getParentId());
        return builder.build();
    }

    private DifferenceOperatorInfluxDB() {

    }

    private static final class InstanceHolder {
        static final DifferenceOperatorInfluxDB instance = new DifferenceOperatorInfluxDB();
    }

    public static DifferenceOperatorInfluxDB getInstance() {
        return InstanceHolder.instance;
    }
}
