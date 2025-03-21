package ntsc.cas.cn.pipelines.operator.binaryConverter;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.product.Decorator;
import ntsc.cas.cn.pipelines.operator.binaryConverter.product.difference.DifferenceBinaryConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.product.other.OtherBinaryConverter;

// use then drop, one shot
public class BinaryConverterFactory {
    public static BinaryConverter createConverter(final DataType type) {
        switch (type) {
            case Difference:
                return new Decorator(new DifferenceBinaryConverter());
            case Other:
                return new Decorator(new OtherBinaryConverter());
            default:
                break;
        }
        return null;
    }
}
