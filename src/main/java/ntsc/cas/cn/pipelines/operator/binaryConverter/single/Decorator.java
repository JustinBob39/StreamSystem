package ntsc.cas.cn.pipelines.operator.binaryConverter.single;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.CRC24;
import ntsc.cas.cn.pipelines.operator.binaryConverter.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

public class Decorator implements BinaryConverter {
    private static final int PREA_BIT_COUNT = 8;
    private static final int RESV_BIT_COUNT = 6;
    private static final int LEN_BIT_COUNT = 10;
    private static final int CRC_BIT_COUNT = 24;

    private byte[] body;
    private byte[] head;
    private byte[] tail;

    private final BinaryConverter converter;

    public Decorator(final BinaryConverter converter) {
        this.converter = converter;
    }

    private void extractHead() {
        assert (body.length != 0);

        final int headBitCount = PREA_BIT_COUNT + RESV_BIT_COUNT + LEN_BIT_COUNT;
        final BitSet headBit = new BitSet(headBitCount);
        int idx = 0;
        final byte preamble = (byte) 0b11010011;
        Util.extractInt(preamble, idx, PREA_BIT_COUNT, headBit);
        idx += PREA_BIT_COUNT;
        Util.extractInt(0, idx, RESV_BIT_COUNT, headBit);
        idx += RESV_BIT_COUNT;
        Util.extractInt(body.length, idx, LEN_BIT_COUNT, headBit);
        Util.flipBitSet(headBit);
        head = Arrays.copyOf(headBit.toByteArray(), headBitCount / 8);
    }

    private void extractTail() {
        final BitSet tailBit = new BitSet(CRC_BIT_COUNT);
        final int crc = new CRC24(concatenateByteArrays(head, body)).computeCRC();
        Util.extractInt(crc, 0, CRC_BIT_COUNT, tailBit);
        Util.flipBitSet(tailBit);
        tail = Arrays.copyOf(tailBit.toByteArray(), CRC_BIT_COUNT / 8);
    }

    private byte[] concatenateByteArrays(final byte[]... arrays) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (final byte[] array : arrays) {
                outputStream.write(array);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] convertToBinary(final Object avro, final DataType type) {
        body = converter.convertToBinary(avro, type); // must call first, head need body length
        extractHead();
        extractTail();
        return concatenateByteArrays(head, body, tail);
    }
}
