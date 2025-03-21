package ntsc.cas.cn.pipelines.forwarder;

public interface Forwarder extends Runnable {
    void startForward();

    boolean validate(final Object avro);

    @Override
    default void run() {
        startForward();
    }
}
