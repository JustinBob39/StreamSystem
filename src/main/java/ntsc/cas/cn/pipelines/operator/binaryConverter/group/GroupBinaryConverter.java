package ntsc.cas.cn.pipelines.operator.binaryConverter.group;

import ntsc.cas.cn.pipelines.DataType;

import java.util.List;

public interface GroupBinaryConverter {
    byte[] convertToBinary(final List<Object> avroList, final DataType type, final DataType subType);
}
