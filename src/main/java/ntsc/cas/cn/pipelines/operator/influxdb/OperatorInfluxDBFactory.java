package ntsc.cas.cn.pipelines.operator.influxdb;

import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.influxdb.product.difference.DifferenceOperatorInfluxDB;
import ntsc.cas.cn.pipelines.operator.influxdb.product.other.OtherOperatorInfluxDB;

public class OperatorInfluxDBFactory {
    public static OperatorInfluxDB getOperatorInfluxDB(DataType type) {
        switch (type) {
            case Difference:
                return DifferenceOperatorInfluxDB.getInstance();
            case Other:
                return OtherOperatorInfluxDB.getInstance();
            default:
                break;
        }
        return null;
    }
}
