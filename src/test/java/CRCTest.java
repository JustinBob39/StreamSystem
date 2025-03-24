import ntsc.cas.cn.pipelines.operator.binaryConverter.CRC24;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;

public class CRCTest {
    private static final Logger log = LoggerFactory.getLogger(CRCTest.class);

    @Test
    public void crc() {
        // https://emn178.github.io/online-tools/crc/
        // choose model CRC-24/LTE-A
        final CRC24 crc24 = new CRC24("Hello World".getBytes(StandardCharsets.UTF_8));
        final int value = crc24.computeCRC();
        byte[] bytes = intToByteArray(value);
        byte[] ans = new byte[]{(byte) 0x12, (byte) 0x93, (byte) 0x7c};
        log.info(Hex.encodeHexString(bytes));
        assertArrayEquals(ans, bytes);
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{(byte) (value >> 16), (byte) (value >> 8), (byte) value};
    }
}
