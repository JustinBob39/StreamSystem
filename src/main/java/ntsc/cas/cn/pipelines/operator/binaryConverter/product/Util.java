package ntsc.cas.cn.pipelines.operator.binaryConverter.product;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.BitSet;

public class Util {
    public static void flipBitSet(final BitSet bs) {
        assert (bs != null);
        for (int i = 0; i < bs.length(); i += 8) {
            for (int j = 0; j < 4; j++) {
                final boolean temp = bs.get(i + j);
                bs.set(i + j, bs.get(i + 7 - j));
                bs.set(i + 7 - j, temp);
            }
        }
    }

    public static void fillTime(final Time time, final Instant instant) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss").withZone(ZoneId.of("UTC"));
        final String formattedTime = formatter.format(instant);

        time.year = Byte.parseByte(formattedTime.substring(0, 2));
        time.month = Byte.parseByte(formattedTime.substring(2, 4));
        time.day = Byte.parseByte(formattedTime.substring(4, 6));
        time.hour = Byte.parseByte(formattedTime.substring(6, 8));
        time.minute = Byte.parseByte(formattedTime.substring(8, 10));
        time.second = Byte.parseByte(formattedTime.substring(10, 12));
    }

    public static void extractTime(final Time time, final int bitOffset, final BitSet bs) {
        int offset = bitOffset;
        int bitCount = Time.timeIndexLength.get(Time.TimeIndex.Year);
        extractInt(time.year, offset, bitCount, bs);
        offset += bitCount;

        bitCount = Time.timeIndexLength.get(Time.TimeIndex.Month);
        extractInt(time.month, offset, bitCount, bs);
        offset += bitCount;

        bitCount = Time.timeIndexLength.get(Time.TimeIndex.Day);
        extractInt(time.day, offset, bitCount, bs);
        offset += bitCount;

        bitCount = Time.timeIndexLength.get(Time.TimeIndex.Hour);
        extractInt(time.hour, offset, bitCount, bs);
        offset += bitCount;

        bitCount = Time.timeIndexLength.get(Time.TimeIndex.Minute);
        extractInt(time.minute, offset, bitCount, bs);
        offset += bitCount;

        bitCount = Time.timeIndexLength.get(Time.TimeIndex.Second);
        extractInt(time.second, offset, bitCount, bs);
    }

    public static void extractInt(final int value, final int bitOffset, final int bitCount, final BitSet bs) {
        int bitMask = (1 << (bitCount - 1));
        int offset = bitOffset;
        for (int i = 0; i < bitCount; i++) {
            if ((value & bitMask) != 0) {
                bs.set(offset);
            }
            offset++;
            bitMask >>= 1;
        }
    }
}
