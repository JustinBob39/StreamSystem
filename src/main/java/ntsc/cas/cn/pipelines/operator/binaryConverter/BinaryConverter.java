package ntsc.cas.cn.pipelines.operator.binaryConverter;

import ntsc.cas.cn.pipelines.DataType;

public interface BinaryConverter {
    byte[] convertToBinary(final Object avro, final DataType type);
}
