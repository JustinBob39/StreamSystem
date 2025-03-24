package ntsc.cas.cn.pipelines;

public enum DataType {
    Difference(10),
    DifferenceFirst(11),
    DifferenceSecond(12),
    DifferenceThird(13),
    DifferenceFourth(14),
    DifferenceFifth(15),
    DifferenceSixth(16),
    DifferenceSeventh(17),

    Other(100);

    private final int code;

    DataType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
