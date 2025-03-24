import ntsc.cas.cn.avro.difference.oneRound.*;
import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.group.GroupBinaryConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.group.GroupBinaryConverterFactory;
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

public class DifferenceGroupBinaryConverterTest {
    private static final Logger log = LoggerFactory.getLogger(DifferenceGroupBinaryConverterTest.class);

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
        final SingleStation station2 = SingleStation.newBuilder().setParentId(2).setParentEventTime(instant).setParentStatus(Status.NORMAL).setChildren(children).build();
        final SingleStation station3 = SingleStation.newBuilder().setParentId(3).setParentEventTime(instant).setParentStatus(Status.NORMAL).setChildren(children).build();
        List<Object> avroList = Arrays.asList(station3, station2, station);
        GroupBinaryConverter converter = GroupBinaryConverterFactory.createConverter(DataType.Difference);
        assert converter != null;
        byte[] bytes = converter.convertToBinary(avroList, DataType.Difference, DataType.DifferenceFirst);
        log.info(Hex.encodeHexString(bytes));
        assertEquals("d3006500b01302100000130210000060739f8e08c0840000181ce7e3a330210000060739f8f0118108000009810800003039cfc704604200000c0e73f1d19810800003039cfc780cc084000004c0840000181ce7e38230210000060739f8e8cc0840000181ce7e3c959e11", Hex.encodeHexString(bytes));


        // d3 00 65 00 b0 13 02 10 00 00 13 02 10 00 00 60 73 9f
        // 8e 08 c0 84 00 00 18 1c e7 e3 a3 30 21 00 00 06 07 39
        // f8 f0 11 81 08 00 00 09 81 08 00 00 30 39 cf c7 04 60
        // 42 00 00 0c 0e 73 f1 d1 98 10 80 00 03 03 9c fc 78 0c
        // c0 84 00 00 04 c0 84 00 00 18 1c e7 e3 82 30 21 00 00
        // 06 07 39 f8 e8 cc 08 40 00 01 81 ce 7e 3c 95 9e 11


        // 1101 0011        d3      preamble
        // 0000 00 00       00      reserved + length(101)
        // 0110 0101        23      length
        // =============================================================================
        // 0000 0000        00      type
        // 1011 0000        b0      type + parentId
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
        // 111 10 000       f0      thirdChildValueSecond + thirdChildDuration + parentId

        // 00010 001        11      parentId + time.year
        // 1000 0001        81      time.year + time.month
        // 00001 000        08      time.day + time.hour
        // 00 000000        00      time.hour + time.minute
        // 000000 00        00      time.second + parentStatus
        // 00001 001        09      firstChildId + time.year
        // 1000 0001        81      time.year + time.day
        // 00001 000        08      time.day + time.hour
        // 00 000000        00      time.hour + time.minute
        // 000000 00        00      time.second + firstChildValueFirst
        // 0011 0000        30      firstChildValueFirst
        // 0011 1001        39      firstChildValueFirst
        // 1100 1111        cf      firstChildValueSecond
        // 1100 0111        c7      firstChildValueSecond
        // 00 00010 0       04      firstChildDuration + secondChildId + time.year
        // 011000 00        60      time.year + time.month
        // 01 00001 0       42      time.month + time.day + time.hour
        // 0000 0000        00      time.hour + time.minute
        // 00 000000        00      time.minute + time.second
        // 0000 1100        0c      secondChildValueFirst
        // 0000 1110        0e      secondChildValueFirst
        // 01 110011        73      secondChildValueFirst + secondChildValueSecond
        // 1111 0001        f1      secondChildValueSecond
        // 11 01 0001       d1      secondChildValueSecond + secondChildDuration + thirdChildId
        // 1 0011000        98      thirdChildId + time.year
        // 0001 0000        10      time.month + time.day
        // 1 00000 00       80      time.day + time.hour + time.minute
        // 0000 0000        00      time.minute + time.second
        // 00 000011        03      time.second + thirdChildValueFirst
        // 0000 0011        03      thirdChildValueFirst
        // 1001 1100        9c      thirdChildValueFirst + thirdChildValueSecond
        // 1111 1100        fc      thirdChildValueSecond
        // 0111 11 00       78      thirdChildValueSecond + thirdChildDuration + parentId

        // 0000 1100        0c
        // 1100 0000        c0
        // 1000 0100        84
        // 0000 0000        00
        // 0000 0000        00
        // 0000 0100        04
        // 1100 0000        c0
        // 1000 0100        84
        // 0000 0000        00
        // 0000 0000        00
        // 0001 1000        18
        // 0001 1100        1c
        // 1110 0111        e7
        // 1110 0011        e3
        // 1000 0010        82
        // 0011 0000        30
        // 0010 0001        21
        // 0000 0000        00
        // 0000 0000        00
        // 0000 0110        06
        // 0000 0111        07
        // 0011 1001        39
        // 1111 1000        f8
        // 1110 1000        e8
        // 1100 1100        cc
        // 0000 1000        08
        // 0100 0000        40
        // 0000 0000        00
        // 0000 0001        01
        // 1000 0001        81
        // 1100 1110        ce
        // 0111 1110        7e
        // 00111 100        3c      pad one bit
        // =============================================================================
        // CRC24
    }
}
