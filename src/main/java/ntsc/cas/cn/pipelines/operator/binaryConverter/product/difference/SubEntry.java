package ntsc.cas.cn.pipelines.operator.binaryConverter.product.difference;

import ntsc.cas.cn.pipelines.operator.binaryConverter.product.Time;

public class SubEntry {
    public int childId;
    public Time childEventTime = new Time();
    public int childDuration;
    public int childValueFirst;
    public int childValueSecond;
}
