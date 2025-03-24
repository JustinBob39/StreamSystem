import ntsc.cas.cn.avro.difference.oneRound.*;
import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.BinaryConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.BinaryConverterFactory;
import org.apache.avro.*;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DifferenceBinaryConverterTest {
    private static final Logger log = LoggerFactory.getLogger(DifferenceBinaryConverterTest.class);

    @Test
    public void convert() {
        final Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
        final Schema allStationsSchema = OneRound.getClassSchema().getField("allStations").schema().getElementType();
        final Schema childrenSchema = allStationsSchema.getField("children").schema().getElementType();
        final LogicalType typeFirst = childrenSchema.getField("childValueFirst").schema().getLogicalType();
        final LogicalType typeSecond = childrenSchema.getField("childValueSecond").schema().getLogicalType();
        final int scaleFirst = ((LogicalTypes.Decimal) typeFirst).getScale();
        final int scaleSecond = ((LogicalTypes.Decimal) typeSecond).getScale();
        final BigDecimal decimalFirst = new BigDecimal("+1234.5").setScale(scaleFirst, RoundingMode.HALF_UP);
        final BigDecimal decimalSecond = new BigDecimal("-1.2345").setScale(scaleSecond, RoundingMode.HALF_UP);
        final ByteBuffer bytesFirst = conversion.toBytes(decimalFirst, null, typeFirst);
        final ByteBuffer bytesSecond = conversion.toBytes(decimalSecond, null, typeSecond);

        final Instant instant = Instant.parse("2024-01-01T00:00:00Z");
        final Child childFirst = Child.newBuilder().setChildId(1).setChildEventTime(instant).setChildDuration(Duration._3MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final Child childSecond = Child.newBuilder().setChildId(2).setChildEventTime(instant).setChildDuration(Duration._5MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final Child childThird = Child.newBuilder().setChildId(3).setChildEventTime(instant).setChildDuration(Duration._10MIN).setChildValueFirst(bytesFirst).setChildValueSecond(bytesSecond).build();
        final List<Child> children = Arrays.asList(childFirst, childSecond, childThird);

        final SingleStation station = SingleStation.newBuilder().setParentId(1).setParentEventTime(instant).setParentStatus(Status.NORMAL).setChildren(children).build();

        final BinaryConverter converter = BinaryConverterFactory.createConverter(DataType.Difference);
        assert converter != null;
        byte[] bytes = converter.convertToBinary(station, DataType.Difference);
        log.info(Hex.encodeHexString(bytes));
        assertEquals("d3002300a01302100000130210000060739f8e08c0840000181ce7e3a330210000060739f8f0dda468", Hex.encodeHexString(bytes));


        // d3 00 23 00 a0 13 02 10 00 00 13 02 10 00 00 60 73 9f 8e 08 c0
        // 84 00 00 18 1c e7 e3 a3 30 21 00 00 06 07 39 f8 f0 dd a4 68


        // 1101 0011        d3      preamble
        // 0000 00 00       00      reserved + length
        // 0010 0011        23      length
        // =============================================================================
        // 0000 0000        00      type
        // 1010 0000        a0      type + parentId
        // 0001 0011        13      parentId + time.year
        // 000 0001 0       02      time.month + time.day
        // 0001 0000        10      time.day + time.hour
        // 0 000000 0       00      time.hour + time.minute + time.second
        // 00000 00 0       00      time.second + parentStatus + firstChildId
        // 0001 0011        13      firstChildId + time.year
        // 000 0001 0       02      time.month + time.day
        // 0001 0000        10      time.day + time.hour
        // 0 000000 0       00      time.hour + time.minute + time.second
        // 00000 000        00      time.second + firstChildValueFirst
        // 0110 0000        60      firstChildValueFirst
        // 0111001 1        73      firstChildValueFirst + firstChildValueSecond
        // 1001 1111        9f      firstChildValueSecond
        // 1000111 0        8e      firstChildValueSecond + firstChildDuration
        // 0 00010 00       08      firstChildDuration + secondChildId + time.year
        // 11000 000        c0      time.year + time.month
        // 1 00001 00       84      time.day + time.hour
        // 000 00000        00      time.hour + time.minute
        // 0 000000 0       00      time.minute + time.second + secondChildValueFirst
        // 0001 1000        18      secondChildValueFirst
        // 0001 1100        1c      secondChildValueFirst
        // 1 1100111        e7      secondChildValueFirst + secondChildValueSecond
        // 1110 0011        e3      secondChildValueSecond
        // 1 01 00011       a3      secondChildValueSecond + secondChildDuration+ thirdChildId
        // 0011000 0        30      time.year + time.month
        // 001 00001        21      time.month + time.day
        // 00000 000        00      time.hour + time.minute
        // 000 00000        00      time.minute + time.second
        // 0 0000110        06      time.second + thirdChildValueFirst
        // 0000 0111        07      thirdChildValueFirst
        // 001 11001        39      thirdChildValueFirst + thirdChildValueSecond
        // 1111 1000        f8      thirdChildValueSecond
        // 111 10 000       f0      thirdChildValueSecond + thirdChildDuration + padding
        // =============================================================================
        // CRC24
    }
}
