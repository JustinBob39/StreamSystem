package ntsc.cas.cn.pipelines.operator.influxdb.product.other;

import ntsc.cas.cn.pipelines.operator.influxdb.OperatorInfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherOperatorInfluxDB implements OperatorInfluxDB {
    final static Logger logger = LoggerFactory.getLogger(OtherOperatorInfluxDB.class);

    @Override
    public void startOperatorInfluxDB() {
        logger.info("OtherOperatorInfluxDB start running");
    }

    private OtherOperatorInfluxDB() {

    }

    private static class SingletonHolder {
        private static final OtherOperatorInfluxDB instance = new OtherOperatorInfluxDB();
    }

    public static OtherOperatorInfluxDB getInstance() {
        return SingletonHolder.instance;
    }
}
