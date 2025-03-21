package ntsc.cas.cn.pipelines.operator.influxdb;

public interface OperatorInfluxDB extends Runnable {
    void startOperatorInfluxDB();

    @Override
    default void run() {
        startOperatorInfluxDB();
    }
}
