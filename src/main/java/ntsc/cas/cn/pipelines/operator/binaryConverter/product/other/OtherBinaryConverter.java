package ntsc.cas.cn.pipelines.operator.binaryConverter.product.other;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.BinaryConverter;

public class OtherBinaryConverter implements BinaryConverter {
    @Override
    public byte[] convertToBinary(final Object avro, final DataType type) {
        return new byte[0];
    }
}
