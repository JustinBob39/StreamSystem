package ntsc.cas.cn.pipelines.operator.binaryConverter.product;

public class CRC24 {
    private static final int[] CRC_LOOKUP = new int[256];

    private static final int GENERATOR = 0x1864CFB;

    private static final int HIGH = 0x1000000;

    private final byte[] buffer;

    static {
        CRC_LOOKUP[0] = 0;
        CRC_LOOKUP[1] = GENERATOR;
        int h = GENERATOR;
        for (int i = 2; i < 256; i <<= 1) {
            h <<= 1;
            if ((h & HIGH) != 0) {
                h ^= GENERATOR;
            }
            for (int j = 0; j < i; ++j) {
                CRC_LOOKUP[i + j] = CRC_LOOKUP[j] ^ h;
            }
        }
    }

    public CRC24(byte[] data) {
        buffer = data;
    }

    private int peekByte(int offset) {
        return buffer[offset] & 0xFF;
    }

    public int computeCRC() {
        int crc = 0;
        for (int i = 0; i < buffer.length; ++i) {
            crc = ((crc << 8) ^ CRC_LOOKUP[peekByte(i) ^ (crc >>> 16)]) & (HIGH - 1);
        }
        return crc;
    }
}
