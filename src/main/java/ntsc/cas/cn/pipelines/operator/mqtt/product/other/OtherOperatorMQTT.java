package ntsc.cas.cn.pipelines.operator.mqtt.product.other;

import ntsc.cas.cn.pipelines.operator.mqtt.OperatorMQTT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherOperatorMQTT implements OperatorMQTT {
    final static Logger logger = LoggerFactory.getLogger(OtherOperatorMQTT.class);

    @Override
    public void startOperatorMQTT() {
        logger.info("OtherOperatorMQTT start running");
    }

    private OtherOperatorMQTT() {
    }

    private static class SingletonHolder {
        private static final OtherOperatorMQTT instance = new OtherOperatorMQTT();
    }

    public static OtherOperatorMQTT getInstance() {
        return SingletonHolder.instance;
    }
}
