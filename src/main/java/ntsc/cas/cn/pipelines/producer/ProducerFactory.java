package ntsc.cas.cn.pipelines.producer;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.producer.product.difference.DifferenceProducer;
import ntsc.cas.cn.pipelines.producer.product.other.OtherProducer;

public class ProducerFactory {
    public static Producer getProducer(final DataType type) {
        switch (type) {
            case Difference:
                return DifferenceProducer.getInstance();
            case Other:
                return OtherProducer.getInstance();
            default:
                break;
        }
        return null;
    }
}
