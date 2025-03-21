import ntsc.cas.cn.avro.difference.oneRound.*;
import org.apache.avro.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OneRoundTest {
    private static final Logger log = LoggerFactory.getLogger(OneRoundTest.class);

    @Test
    public void construct() {
        final Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
        final Schema allStationsSchema = OneRound.getClassSchema().getField("allStations").schema().getElementType();
        final Schema childrenSchema = allStationsSchema.getField("children").schema().getElementType();
        final LogicalType typeFirst = childrenSchema.getField("childValueFirst").schema().getLogicalType();
        final LogicalType typeSecond = childrenSchema.getField("childValueSecond").schema().getLogicalType();
        final int scaleFirst = ((LogicalTypes.Decimal) typeFirst).getScale();
        final int scaleSecond = ((LogicalTypes.Decimal) typeSecond).getScale();
        final BigDecimal decimalFirst = new BigDecimal("-9.99").setScale(scaleFirst, RoundingMode.HALF_UP);
        final BigDecimal decimalSecond = new BigDecimal("-9.99").setScale(scaleSecond, RoundingMode.HALF_UP);
        final ByteBuffer bytesFirst = conversion.toBytes(decimalFirst, null, typeFirst);
        final ByteBuffer bytesSecond = conversion.toBytes(decimalSecond, null, typeSecond);
        final Child childFirst = Child.newBuilder().setChildId(1).setChildEventTime(Instant.now()).setChildDuration(Duration._3MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final Child childSecond = Child.newBuilder().setChildId(2).setChildEventTime(Instant.now()).setChildDuration(Duration._5MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final Child childThird = Child.newBuilder().setChildId(3).setChildEventTime(Instant.now()).setChildDuration(Duration._10MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final List<Child> children = Arrays.asList(childFirst, childSecond, childThird);

        final SingleStation station = SingleStation.newBuilder().setParentId(1).setParentEventTime(Instant.now()).setParentStatus(Status.NORMAL).setChildren(children).build();
        final List<SingleStation> stations = Collections.singletonList(station);

        final OneRound message = OneRound.newBuilder().setAllStations(stations).build();
        log.info("{}", message.getAllStations().get(0).getFrameStatus());
    }
}
