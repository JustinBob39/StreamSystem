package ntsc.cas.cn.pipelines.forwarder;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.forwarder.difference.DifferenceForwarder;
import ntsc.cas.cn.pipelines.forwarder.other.OtherForwarder;

public class ForwarderFactory {
    public static Forwarder getForwarder(final DataType type) {
        switch (type) {
            case Difference:
                return DifferenceForwarder.getInstance();
            case Other:
                return OtherForwarder.getInstance();
            default:
                break;
        }
        return null;
    }
}
