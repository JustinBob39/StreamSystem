package ntsc.cas.cn.pipelines;

public enum DataType {
    Difference(10), Other(100);

    private final int code;

    DataType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
