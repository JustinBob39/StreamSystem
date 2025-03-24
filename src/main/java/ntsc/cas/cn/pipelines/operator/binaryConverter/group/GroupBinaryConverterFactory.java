package ntsc.cas.cn.pipelines.operator.binaryConverter.group;


import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.group.difference.GroupDifferenceConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.group.other.GroupOtherConverter;

public class GroupBinaryConverterFactory {
    public static GroupBinaryConverter createConverter(final DataType type) {
        switch (type) {
            case Difference:
                return new Decorator(new GroupDifferenceConverter());
            case Other:
                return new Decorator(new GroupOtherConverter());
            default:
                break;
        }
        return null;
    }
}
