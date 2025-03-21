package ntsc.cas.cn.pipelines.forwarder.other;

import ntsc.cas.cn.pipelines.forwarder.Forwarder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherForwarder implements Forwarder {
    final static Logger logger = LoggerFactory.getLogger(OtherForwarder.class);

    @Override
    public void startForward() {
        logger.info("OtherForwarder start forward");
    }

    @Override
    public boolean validate(final Object avro) {
        return false;
    }

    private OtherForwarder() {
    }

    private static class SingletonHolder {
        private static final OtherForwarder instance = new OtherForwarder();
    }

    public static OtherForwarder getInstance() {
        return SingletonHolder.instance;
    }
}
