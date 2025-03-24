package ntsc.cas.cn.pipelines.operator.binaryConverter.group.other;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.group.GroupBinaryConverter;

import java.util.List;

public class GroupOtherConverter implements GroupBinaryConverter {
    @Override
    public byte[] convertToBinary(List<Object> avroList, DataType type, final DataType subType) {
        return new byte[0];
    }
}
