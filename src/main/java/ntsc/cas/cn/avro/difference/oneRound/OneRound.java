/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package ntsc.cas.cn.avro.difference.oneRound;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class OneRound extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 307191156125520642L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OneRound\",\"namespace\":\"ntsc.cas.cn.avro.difference.oneRound\",\"fields\":[{\"name\":\"allStations\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"SingleStation\",\"fields\":[{\"name\":\"frameStatus\",\"type\":{\"type\":\"enum\",\"name\":\"FrameStatus\",\"symbols\":[\"INITIAL\",\"NORMAL\",\"TIMEOUT\"]},\"default\":\"NORMAL\"},{\"name\":\"parentId\",\"type\":\"int\",\"doc\":\"range 001,002,003,...,175\"},{\"name\":\"parentEventTime\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"parentStatus\",\"type\":{\"type\":\"enum\",\"name\":\"Status\",\"symbols\":[\"NORMAL\",\"FAULT\",\"MAINTENANCE\"]}},{\"name\":\"children\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Child\",\"fields\":[{\"name\":\"childId\",\"type\":\"int\",\"doc\":\"range 01,02,03,...,16\"},{\"name\":\"childEventTime\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"childDuration\",\"type\":{\"type\":\"enum\",\"name\":\"Duration\",\"symbols\":[\"_3MIN\",\"_5MIN\",\"_10MIN\"]}},{\"name\":\"childValueFirst\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":5,\"scale\":1}},{\"name\":\"childValueSecond\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":5,\"scale\":4}}]}},\"doc\":\"3 child\"}]}},\"doc\":\"175 stations\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();
  static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.DecimalConversion());
  }

  private static final BinaryMessageEncoder<OneRound> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<OneRound> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<OneRound> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<OneRound> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<OneRound> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this OneRound to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a OneRound from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a OneRound instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static OneRound fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /** 175 stations */
  private java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation> allStations;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public OneRound() {}

  /**
   * All-args constructor.
   * @param allStations 175 stations
   */
  public OneRound(java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation> allStations) {
    this.allStations = allStations;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return allStations;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: allStations = (java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'allStations' field.
   * @return 175 stations
   */
  public java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation> getAllStations() {
    return allStations;
  }


  /**
   * Sets the value of the 'allStations' field.
   * 175 stations
   * @param value the value to set.
   */
  public void setAllStations(java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation> value) {
    this.allStations = value;
  }

  /**
   * Creates a new OneRound RecordBuilder.
   * @return A new OneRound RecordBuilder
   */
  public static ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder newBuilder() {
    return new ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder();
  }

  /**
   * Creates a new OneRound RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new OneRound RecordBuilder
   */
  public static ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder newBuilder(ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder other) {
    if (other == null) {
      return new ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder();
    } else {
      return new ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder(other);
    }
  }

  /**
   * Creates a new OneRound RecordBuilder by copying an existing OneRound instance.
   * @param other The existing instance to copy.
   * @return A new OneRound RecordBuilder
   */
  public static ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder newBuilder(ntsc.cas.cn.avro.difference.oneRound.OneRound other) {
    if (other == null) {
      return new ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder();
    } else {
      return new ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder(other);
    }
  }

  /**
   * RecordBuilder for OneRound instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<OneRound>
    implements org.apache.avro.data.RecordBuilder<OneRound> {

    /** 175 stations */
    private java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation> allStations;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.allStations)) {
        this.allStations = data().deepCopy(fields()[0].schema(), other.allStations);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
    }

    /**
     * Creates a Builder by copying an existing OneRound instance
     * @param other The existing instance to copy.
     */
    private Builder(ntsc.cas.cn.avro.difference.oneRound.OneRound other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.allStations)) {
        this.allStations = data().deepCopy(fields()[0].schema(), other.allStations);
        fieldSetFlags()[0] = true;
      }
    }

    /**
      * Gets the value of the 'allStations' field.
      * 175 stations
      * @return The value.
      */
    public java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation> getAllStations() {
      return allStations;
    }


    /**
      * Sets the value of the 'allStations' field.
      * 175 stations
      * @param value The value of 'allStations'.
      * @return This builder.
      */
    public ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder setAllStations(java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation> value) {
      validate(fields()[0], value);
      this.allStations = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'allStations' field has been set.
      * 175 stations
      * @return True if the 'allStations' field has been set, false otherwise.
      */
    public boolean hasAllStations() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'allStations' field.
      * 175 stations
      * @return This builder.
      */
    public ntsc.cas.cn.avro.difference.oneRound.OneRound.Builder clearAllStations() {
      allStations = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OneRound build() {
      try {
        OneRound record = new OneRound();
        record.allStations = fieldSetFlags()[0] ? this.allStations : (java.util.List<ntsc.cas.cn.avro.difference.oneRound.SingleStation>) defaultValue(fields()[0]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<OneRound>
    WRITER$ = (org.apache.avro.io.DatumWriter<OneRound>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<OneRound>
    READER$ = (org.apache.avro.io.DatumReader<OneRound>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










