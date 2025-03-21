package ntsc.cas.cn.pipelines.operator.mqtt;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.mqtt.product.difference.DifferenceOperatorMQTT;
import ntsc.cas.cn.pipelines.operator.mqtt.product.other.OtherOperatorMQTT;

public class OperatorMQTTFactory {
    public static OperatorMQTT getOperatorMQTT(final DataType type) {
        switch (type) {
            case Difference:
                return DifferenceOperatorMQTT.getInstance();
            case Other:
                return OtherOperatorMQTT.getInstance();
            default:
                break;
        }
        return null;
    }
}
