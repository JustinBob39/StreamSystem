package ntsc.cas.cn.pipelines.operator.binaryConverter.single.product.difference;

import ntsc.cas.cn.pipelines.operator.binaryConverter.Time;

public class Entry {
    public int parentId;
    public Time parentEventTime = new Time();
    public int parentStatus;
    public SubEntry[] subEntries = new SubEntry[3];

    public Entry() {
        // alert
        for (int i = 0; i < 3; i++) {
            subEntries[i] = new SubEntry();
        }
    }
}