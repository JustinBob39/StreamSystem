package ntsc.cas.cn.pipelines.producer.product.difference;

import ntsc.cas.cn.avro.difference.oneRound.*;
import ntsc.cas.cn.pipelines.producer.Producer;
import org.apache.avro.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DifferenceProducer implements Producer {
    final static Logger logger = LoggerFactory.getLogger(DifferenceProducer.class);
    // config
    final static String topic = "Upstream-Forwarder_Difference";

    @Override
    public void startProduce() {
        Properties props = new Properties();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("producer.properties");
            props.load(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        final KafkaProducer<String, OneRound> producer = new KafkaProducer<>(props);
        int cnt = 0;
        while (true) {
            // fake data, construct record
            final Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
            final Schema allStationsSchema = OneRound.getClassSchema().getField("allStations").schema().getElementType();
            final Schema childrenSchema = allStationsSchema.getField("children").schema().getElementType();
            final LogicalType typeFirst = childrenSchema.getField("childValueFirst").schema().getLogicalType();
            final LogicalType typeSecond = childrenSchema.getField("childValueSecond").schema().getLogicalType();
            final int scaleFirst = ((LogicalTypes.Decimal) typeFirst).getScale();
            final int scaleSecond = ((LogicalTypes.Decimal) typeSecond).getScale();
            final BigDecimal decimalFirst = new BigDecimal("-1024.0").setScale(scaleFirst, RoundingMode.HALF_UP);
            final BigDecimal decimalSecond = new BigDecimal("-1.2345").setScale(scaleSecond, RoundingMode.HALF_UP);
            final ByteBuffer bytesFirst = conversion.toBytes(decimalFirst, null, typeFirst);
            final ByteBuffer bytesSecond = conversion.toBytes(decimalSecond, null, typeSecond);
            final Child childFirst = Child.newBuilder().setChildId(1).setChildEventTime(Instant.now()).setChildDuration(Duration._3MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
            final Child childSecond = Child.newBuilder().setChildId(2).setChildEventTime(Instant.now()).setChildDuration(Duration._5MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
            final Child childThird = Child.newBuilder().setChildId(3).setChildEventTime(Instant.now()).setChildDuration(Duration._10MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
            final List<Child> children = Arrays.asList(childFirst, childSecond, childThird);
            final List<SingleStation> stations = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                final SingleStation station = SingleStation.newBuilder().setParentId(i + 1).setParentEventTime(Instant.now()).setParentStatus(Status.NORMAL).setChildren(children).build();
                stations.add(station);
            }
            final OneRound message = OneRound.newBuilder().setAllStations(stations).build();

            // send message
            final ProducerRecord<String, OneRound> record = new ProducerRecord<>(topic, message);
            try {
                producer.send(record, (recordMetadata, e) -> {
                    if (e == null) {
                        logger.info(topic + " {}", recordMetadata);
                    } else {
                        logger.error(e.getMessage());
                    }
                });
                producer.flush();

                // timeout simulate
                ++cnt;
                if (cnt % 5 == 0) {
                    Thread.sleep(90 * 1000);
                    logger.info("Timeout trigger");
                } else {
                    Thread.sleep(60 * 1000);
                }
            } catch (SerializationException | InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private DifferenceProducer() {
    }

    private static final class InstanceHolder {
        static final DifferenceProducer instance = new DifferenceProducer();
    }

    public static DifferenceProducer getInstance() {
        return InstanceHolder.instance;
    }
}
