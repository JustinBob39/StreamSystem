/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package ntsc.cas.cn.avro.difference.oneRound;
@org.apache.avro.specific.AvroGenerated
public enum FrameStatus implements org.apache.avro.generic.GenericEnumSymbol<FrameStatus> {
  INITIAL, NORMAL, TIMEOUT  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"FrameStatus\",\"namespace\":\"ntsc.cas.cn.avro.difference.oneRound\",\"symbols\":[\"INITIAL\",\"NORMAL\",\"TIMEOUT\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
