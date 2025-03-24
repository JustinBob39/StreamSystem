package ntsc.cas.cn.pipelines.operator.binaryConverter.single.product.difference;

import ntsc.cas.cn.pipelines.operator.binaryConverter.Time;

public class SubEntry {
    public int childId;
    public Time childEventTime = new Time();
    public int childDuration;
    public int childValueFirst;
    public int childValueSecond;
}
