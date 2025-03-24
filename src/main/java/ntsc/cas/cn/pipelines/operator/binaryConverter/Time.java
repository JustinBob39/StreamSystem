package ntsc.cas.cn.pipelines.operator.binaryConverter;

import java.util.EnumMap;
import java.util.Map;

public class Time {
    public byte year;
    public byte month;
    public byte day;
    public byte hour;
    public byte minute;
    public byte second;

    public enum TimeIndex {
        Year, Month, Day, Hour, Minute, Second
    }

    public static final Map<TimeIndex, Integer> timeIndexLength = new EnumMap<>(TimeIndex.class);

    static {
        timeIndexLength.put(TimeIndex.Year, 7);
        timeIndexLength.put(TimeIndex.Month, 4);
        timeIndexLength.put(TimeIndex.Day, 5);
        timeIndexLength.put(TimeIndex.Hour, 5);
        timeIndexLength.put(TimeIndex.Minute, 6);
        timeIndexLength.put(TimeIndex.Second, 6);
    }
}
