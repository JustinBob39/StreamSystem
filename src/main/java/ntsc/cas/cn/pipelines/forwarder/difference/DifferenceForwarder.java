package ntsc.cas.cn.pipelines.forwarder.difference;

import ntsc.cas.cn.avro.difference.oneRound.*;
import ntsc.cas.cn.pipelines.forwarder.Forwarder;
import org.apache.avro.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.Duration;
import java.util.*;

public class DifferenceForwarder implements Forwarder {
    final static Logger logger = LoggerFactory.getLogger(DifferenceForwarder.class);
    // config
    static final String sourceTopic = "Upstream-Forwarder_Difference";
    static final String sinkTopic = "Forwarder-Operator_Difference";
    static final String groupId = "Forwarder-Difference";
    // timeout
    static final int gap = 60;
    // validate
    static final int innerCapacity = 16;
    static final int capacity = 4;
    static final BigDecimal firstUpper = new BigDecimal("+2047");
    static final BigDecimal firstLower = new BigDecimal("-2047");
    static final BigDecimal secondUpper = new BigDecimal("+2");
    static final BigDecimal secondLower = new BigDecimal("-2");
    // initial broadcast
    static final Map<Integer, SingleStation> cache = new TreeMap<>();

    static {
        final Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
        final Schema allStationsSchema = OneRound.getClassSchema().getField("allStations").schema().getElementType();
        final Schema childrenSchema = allStationsSchema.getField("children").schema().getElementType();
        final LogicalType typeFirst = childrenSchema.getField("childValueFirst").schema().getLogicalType();
        final LogicalType typeSecond = childrenSchema.getField("childValueSecond").schema().getLogicalType();
        final int scaleFirst = ((LogicalTypes.Decimal) typeFirst).getScale();
        final int scaleSecond = ((LogicalTypes.Decimal) typeSecond).getScale();
        final BigDecimal decimalFirst = new BigDecimal("0.0").setScale(scaleFirst, RoundingMode.HALF_UP);
        final BigDecimal decimalSecond = new BigDecimal("0.0").setScale(scaleSecond, RoundingMode.HALF_UP);
        final ByteBuffer bytesFirst = conversion.toBytes(decimalFirst, null, typeFirst);
        final ByteBuffer bytesSecond = conversion.toBytes(decimalSecond, null, typeSecond);
        final Child childFirst = Child.newBuilder().setChildId(0).setChildEventTime(Instant.EPOCH).setChildDuration(ntsc.cas.cn.avro.difference.oneRound.Duration._3MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final Child childSecond = Child.newBuilder().setChildId(0).setChildEventTime(Instant.EPOCH).setChildDuration(ntsc.cas.cn.avro.difference.oneRound.Duration._3MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final Child childThird = Child.newBuilder().setChildId(0).setChildEventTime(Instant.EPOCH).setChildDuration(ntsc.cas.cn.avro.difference.oneRound.Duration._3MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final List<Child> children = Arrays.asList(childFirst, childSecond, childThird);
        for (int i = 0; i < capacity; i++) {
            final SingleStation station = SingleStation.newBuilder().setParentId(i + 1).setFrameStatus(FrameStatus.INITIAL).setParentEventTime(Instant.EPOCH).setParentStatus(Status.NORMAL).setChildren(children).build();
            cache.put(i + 1, station);
        }
    }


    @Override
    public void startForward() {
        final Properties consumerProps = new Properties();
        final Properties producerProps = new Properties();
        try {
            final InputStream consumerStream = this.getClass().getClassLoader().getResourceAsStream("forwarder_consumer.properties");
            consumerProps.load(consumerStream);
            final InputStream producerStream = this.getClass().getClassLoader().getResourceAsStream("forwarder_producer.properties");
            producerProps.load(producerStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // consumer
        final KafkaConsumer<String, OneRound> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singleton(sourceTopic));

        // producer
        final KafkaProducer<String, OneRound> producer = new KafkaProducer<>(producerProps);

        while (true) {
            // first consume
            cache.forEach((integer, singleStation) -> {
                // INITIAL and TIMEOUT remain
                if (singleStation.getFrameStatus().equals(FrameStatus.NORMAL)) {
                    singleStation.setFrameStatus(FrameStatus.TIMEOUT);
                }
            });
            final ConsumerRecords<String, OneRound> records = consumer.poll(Duration.ofSeconds(gap));
            for (final ConsumerRecord<String, OneRound> record : records) {
                final OneRound message = record.value();
                final List<SingleStation> allStations = message.getAllStations();

                // filter and concatenate
                allStations.forEach(station -> {
                    if (validate(station)) {
                        cache.put(station.getParentId(), station);
                    } else {
                        logger.info("Data invalidate");
                    }
                });
                assert (cache.size() <= capacity); // defensive
                logger.info("Key: {}, Value: {}", record.key(), record.value());
                logger.info("Partition: {}, Offset:{}", record.partition(), record.offset());
            }
            if (records.isEmpty()) {
                logger.info("Timeout trigger");
            }
            consumer.commitSync();

            // second produce
            final List<SingleStation> sortedList = new ArrayList<>(cache.values());
            final OneRound sinkMessage = OneRound.newBuilder().setAllStations(sortedList).build();
            final ProducerRecord<String, OneRound> sinkRecord = new ProducerRecord<>(sinkTopic, sinkMessage);
            producer.send(sinkRecord, (recordMetadata, e) -> {
                if (e == null) {
                    logger.info(String.valueOf(recordMetadata));
                } else {
                    logger.error(e.getMessage());
                }
            });
            producer.flush();
        }
    }

    @Override
    public boolean validate(final Object avro) {
        final SingleStation station = (SingleStation) avro;
        if (station.getChildren().size() != 3) {
            return false;
        }
        if (station.getParentId() <= 0 || station.getParentId() > capacity) {
            return false;
        }
        final Instant parentEventTime = station.getParentEventTime();
        if (parentEventTime.isAfter(Instant.now())) {
            return false;
        }
        // parentStatus enum enough
        final Conversions.DecimalConversion conversion = new Conversions.DecimalConversion();
        final Schema allStationsSchema = OneRound.getClassSchema().getField("allStations").schema().getElementType();
        final Schema childrenSchema = allStationsSchema.getField("children").schema().getElementType();
        final LogicalType typeFirst = childrenSchema.getField("childValueFirst").schema().getLogicalType();
        final LogicalType typeSecond = childrenSchema.getField("childValueSecond").schema().getLogicalType();
        for (int i = 0; i < 3; i++) {
            final Child child = station.getChildren().get(i);
            final int childId = child.getChildId();
            if (childId < 0 || childId > innerCapacity) {
                return false;
            }
            final Instant childEventTime = child.getChildEventTime();
            if (childEventTime.isAfter(Instant.now())) {
                return false;
            }
            final ByteBuffer childValueFirst = child.getChildValueFirst();
            final ByteBuffer childValueSecond = child.getChildValueSecond();
            final BigDecimal decimalFirst = conversion.fromBytes(childValueFirst, null, typeFirst);
            final BigDecimal decimalSecond = conversion.fromBytes(childValueSecond, null, typeSecond);
            if (decimalFirst.compareTo(firstUpper) > 0) {
                return false;
            }
            if (decimalFirst.compareTo(firstLower) < 0) {
                return false;
            }
            if (decimalSecond.compareTo(secondUpper) > 0) {
                return false;
            }
            if (decimalSecond.compareTo(secondLower) < 0) {
                return false;
            }
            // childDuration enum enough
        }
        return true;
    }

    private DifferenceForwarder() {
    }

    private static class SingletonHolder {
        private static final DifferenceForwarder instance = new DifferenceForwarder();
    }

    public static DifferenceForwarder getInstance() {
        return SingletonHolder.instance;
    }
}
