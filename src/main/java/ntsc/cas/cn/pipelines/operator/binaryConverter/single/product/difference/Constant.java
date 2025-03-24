package ntsc.cas.cn.pipelines.operator.binaryConverter.single.product.difference;

import java.util.EnumMap;
import java.util.Map;

public class Constant {
    public enum EntryIndex {
        ParentId, ParentEventTime, ParentStatus, ChildFirst, ChildSecond, ChildThird
    }

    public enum SubEntryIndex {
        ChildId, ChildEventTime, ChildValueFirst, ChildValueSecond, ChildDuration
    }

    public static final Map<EntryIndex, Integer> entryIndexLength = new EnumMap<>(EntryIndex.class);
    public static final Map<SubEntryIndex, Integer> subEntryIndexLength = new EnumMap<>(SubEntryIndex.class);
    public static final int TYPE_BIT_COUNT = 12;
    public static int ENTRY_BIT_COUNT = 0;
    public static int BODY_BIT_COUNT = 0;

    static {
        entryIndexLength.put(EntryIndex.ParentId, 8);
        entryIndexLength.put(EntryIndex.ParentEventTime, 33);
        entryIndexLength.put(EntryIndex.ParentStatus, 2);
        entryIndexLength.put(EntryIndex.ChildFirst, 74);
        entryIndexLength.put(EntryIndex.ChildSecond, 74);
        entryIndexLength.put(EntryIndex.ChildThird, 74);

        subEntryIndexLength.put(SubEntryIndex.ChildId, 5);
        subEntryIndexLength.put(SubEntryIndex.ChildEventTime, 33);
        subEntryIndexLength.put(SubEntryIndex.ChildValueFirst, 18);
        subEntryIndexLength.put(SubEntryIndex.ChildValueSecond, 16);
        subEntryIndexLength.put(SubEntryIndex.ChildDuration, 2);

        BODY_BIT_COUNT = entryIndexLength.values().stream().mapToInt(Integer::intValue).sum();
        int bitCount = TYPE_BIT_COUNT + BODY_BIT_COUNT;
        int padBitCount = 8 - (bitCount % 8);
        ENTRY_BIT_COUNT = bitCount + padBitCount;
    }
}
