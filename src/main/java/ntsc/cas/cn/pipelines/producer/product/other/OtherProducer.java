package ntsc.cas.cn.pipelines.producer.product.other;

import ntsc.cas.cn.pipelines.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherProducer implements Producer {
    final static Logger logger = LoggerFactory.getLogger(OtherProducer.class);

    @Override
    public void startProduce() {
        logger.info("OtherProducer start produce");
    }

    private OtherProducer() {
    }

    private static final class InstanceHolder {
        static final OtherProducer instance = new OtherProducer();
    }

    public static OtherProducer getInstance() {
        return InstanceHolder.instance;
    }
}
