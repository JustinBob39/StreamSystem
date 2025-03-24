package ntsc.cas.cn.pipelines.operator.binaryConverter.single.product.difference;

import ntsc.cas.cn.avro.difference.oneRound.Child;
import ntsc.cas.cn.avro.difference.oneRound.OneRound;
import ntsc.cas.cn.avro.difference.oneRound.SingleStation;
import ntsc.cas.cn.pipelines.DataType;
import ntsc.cas.cn.pipelines.operator.binaryConverter.single.BinaryConverter;
import ntsc.cas.cn.pipelines.operator.binaryConverter.Util;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class DifferenceBinaryConverter implements BinaryConverter {
    @Override
    public byte[] convertToBinary(final Object avro, final DataType type) {
        final SingleStation station = (SingleStation) avro;
        fillInterEntry(station);

        // type + entry + pad
        Util.extractInt(type.getCode(), 0, Constant.TYPE_BIT_COUNT, interBinary);
        idx += Constant.TYPE_BIT_COUNT;
        extractInterBinary();

        Util.flipBitSet(interBinary);
        return Arrays.copyOf(interBinary.toByteArray(), Constant.ENTRY_BIT_COUNT / 8);
    }

    private int idx = 0;
    private final Entry interEntry = new Entry();
    private final BitSet interBinary = new BitSet(Constant.ENTRY_BIT_COUNT);

    private void fillSubEntry(final SubEntry subEntry, final Child child) {
        subEntry.childId = child.getChildId();
        Util.fillTime(subEntry.childEventTime, child.getChildEventTime());
        subEntry.childDuration = child.getChildDuration().ordinal();
        final Conversions.DecimalConversion conversion = new Conversions.DecimalConversion();
        final ByteBuffer childValueFirst = child.getChildValueFirst();
        final ByteBuffer childValueSecond = child.getChildValueSecond();
        final Schema allStationsSchema = OneRound.getClassSchema().getField("allStations").schema().getElementType();
        final Schema childrenSchema = allStationsSchema.getField("children").schema().getElementType();
        final LogicalType typeFirst = childrenSchema.getField("childValueFirst").schema().getLogicalType();
        final LogicalType typeSecond = childrenSchema.getField("childValueSecond").schema().getLogicalType();
        final BigDecimal decimalFirst = conversion.fromBytes(childValueFirst, null, typeFirst);
        final BigDecimal decimalSecond = conversion.fromBytes(childValueSecond, null, typeSecond);
        subEntry.childValueFirst = decimalFirst.movePointRight(decimalFirst.scale()).intValue();
        subEntry.childValueSecond = decimalSecond.movePointRight(decimalSecond.scale()).intValue();
    }

    private void fillInterEntry(final SingleStation station) {
        interEntry.parentId = station.getParentId();
        // interEntry.parentEventTime
        Util.fillTime(interEntry.parentEventTime, station.getParentEventTime());
        interEntry.parentStatus = station.getParentStatus().ordinal();
        assert (station.getChildren().size() == 3);
        // interEntry.children[0]
        fillSubEntry(interEntry.subEntries[0], station.getChildren().get(0));
        // interEntry.children[1]
        fillSubEntry(interEntry.subEntries[1], station.getChildren().get(1));
        // interEntry.children[2]
        fillSubEntry(interEntry.subEntries[2], station.getChildren().get(2));
    }

    private void extractInterBinaryParentId() {
        final int bitCount = Constant.entryIndexLength.get(Constant.EntryIndex.ParentId);
        Util.extractInt(interEntry.parentId, idx, bitCount, interBinary);
        idx += bitCount;
    }

    private void extractInterBinaryParentEventTime() {
        final int bitCount = Constant.entryIndexLength.get(Constant.EntryIndex.ParentEventTime);
        Util.extractTime(interEntry.parentEventTime, idx, interBinary);
        idx += bitCount;
    }

    private void extractInterBinaryParentStatus() {
        final int bitCount = Constant.entryIndexLength.get(Constant.EntryIndex.ParentStatus);
        Util.extractInt(interEntry.parentStatus, idx, bitCount, interBinary);
        idx += bitCount;
    }

    private void extractInterBinaryChildId(int ith) {
        final int bitCount = Constant.subEntryIndexLength.get(Constant.SubEntryIndex.ChildId);
        Util.extractInt(interEntry.subEntries[ith].childId, idx, bitCount, interBinary);
        idx += bitCount;
    }


    private void extractInterBinaryChild(int ith) {
        extractInterBinaryChildId(ith);
        int bitCount = Constant.subEntryIndexLength.get(Constant.SubEntryIndex.ChildEventTime);
        Util.extractTime(interEntry.subEntries[ith].childEventTime, idx, interBinary);
        idx += bitCount;
        bitCount = Constant.subEntryIndexLength.get(Constant.SubEntryIndex.ChildValueFirst);
        Util.extractInt(interEntry.subEntries[ith].childValueFirst, idx, bitCount, interBinary);
        idx += bitCount;
        bitCount = Constant.subEntryIndexLength.get(Constant.SubEntryIndex.ChildValueSecond);
        Util.extractInt(interEntry.subEntries[ith].childValueSecond, idx, bitCount, interBinary);
        idx += bitCount;
        bitCount = Constant.subEntryIndexLength.get(Constant.SubEntryIndex.ChildDuration);
        Util.extractInt(interEntry.subEntries[ith].childDuration, idx, bitCount, interBinary);
        idx += bitCount;
    }

    private void extractInterBinaryChildren() {
        for (int i = 0; i < 3; i++) {
            extractInterBinaryChild(i);
        }
    }

    private void extractInterBinary() {
        extractInterBinaryParentId();
        extractInterBinaryParentEventTime();
        extractInterBinaryParentStatus();
        extractInterBinaryChildren();
    }
}
