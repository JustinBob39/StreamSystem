package ntsc.cas.cn.pipelines.operator.mqtt;

public interface OperatorMQTT extends Runnable {
    void startOperatorMQTT();

    @Override
    default void run() {
        startOperatorMQTT();
    }
}
