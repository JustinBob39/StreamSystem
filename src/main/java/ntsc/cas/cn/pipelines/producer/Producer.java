package ntsc.cas.cn.pipelines.producer;

public interface Producer extends Runnable {
    void startProduce();

    @Override
    default void run() {
        startProduce();
    }
}
