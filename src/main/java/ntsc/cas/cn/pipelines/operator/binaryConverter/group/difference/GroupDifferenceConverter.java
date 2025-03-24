package ntsc.cas.cn.pipelines.operator.binaryConverter.group.difference;

import ntsc.cas.cn.avro.difference.oneRound.SingleStation;
import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.Util;
import ntsc.cas.cn.pipelines.operator.binaryConverter.group.Decorator;
import ntsc.cas.cn.pipelines.operator.binaryConverter.group.GroupBinaryConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.BinaryConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.BinaryConverterFactory;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.product.difference.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GroupDifferenceConverter implements GroupBinaryConverter {
    private static final Logger log = LoggerFactory.getLogger(GroupDifferenceConverter.class);

    @Override
    public byte[] convertToBinary(final List<Object> avroList, final DataType type, final DataType subType) {
        // precondition
        // 1-30         first
        // 31-60        second
        // 61-90        third
        // 91-120       fourth
        // 121-150      fifth
        // 151-180      sixth
        // 180-200      seventh
        final List<SingleStation> stationList = new ArrayList<>();
        avroList.forEach(avro -> {
            stationList.add((SingleStation) avro);
        });
        stationList.sort(Comparator.comparingInt(SingleStation::getParentId));

        final int bitCount = Constant.TYPE_BIT_COUNT + Constant.BODY_BIT_COUNT * stationList.size();
        final int padBitCount = 8 - (bitCount % 8);
        final int realCount = bitCount + padBitCount;
        final BitSet bs = new BitSet(realCount);

        int idx = 0;
        Util.extractInt(subType.getCode(), idx, Constant.TYPE_BIT_COUNT, bs);
        idx += Constant.TYPE_BIT_COUNT;

        final int start = Decorator.PREA_BIT_COUNT + Decorator.RESV_BIT_COUNT + Decorator.LEN_BIT_COUNT + Constant.TYPE_BIT_COUNT;
        final int length = Constant.BODY_BIT_COUNT;
        for (SingleStation station : stationList) {
            final BinaryConverter singleConverter = BinaryConverterFactory.createConverter(type);
            assert singleConverter != null;
            final byte[] bytes = singleConverter.convertToBinary(station, type);
            final BitSet bitSet = BitSet.valueOf(bytes);
            Util.flipBitSet(bitSet);
            for (int i = 0; i < length; i++) {
                if (bitSet.get(start + i)) {
                    bs.set(idx, true);
                }
                ++idx;
            }
        }
        Util.flipBitSet(bs);
        return Arrays.copyOf(bs.toByteArray(), realCount / 8);
    }
}
