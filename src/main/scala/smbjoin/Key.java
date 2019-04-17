/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package smbjoin;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Key extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -6507936232273950876L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Key\",\"namespace\":\"smbjoin\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"key\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Key> ENCODER =
      new BinaryMessageEncoder<Key>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Key> DECODER =
      new BinaryMessageDecoder<Key>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<Key> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<Key> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Key>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this Key to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a Key from a ByteBuffer. */
  public static Key fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence id;
  @Deprecated public java.lang.CharSequence key;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Key() {}

  /**
   * All-args constructor.
   * @param id The new value for id
   * @param key The new value for key
   */
  public Key(java.lang.CharSequence id, java.lang.CharSequence key) {
    this.id = id;
    this.key = key;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return key;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.CharSequence)value$; break;
    case 1: key = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public java.lang.CharSequence getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.CharSequence value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'key' field.
   * @return The value of the 'key' field.
   */
  public java.lang.CharSequence getKey() {
    return key;
  }

  /**
   * Sets the value of the 'key' field.
   * @param value the value to set.
   */
  public void setKey(java.lang.CharSequence value) {
    this.key = value;
  }

  /**
   * Creates a new Key RecordBuilder.
   * @return A new Key RecordBuilder
   */
  public static smbjoin.Key.Builder newBuilder() {
    return new smbjoin.Key.Builder();
  }

  /**
   * Creates a new Key RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Key RecordBuilder
   */
  public static smbjoin.Key.Builder newBuilder(smbjoin.Key.Builder other) {
    return new smbjoin.Key.Builder(other);
  }

  /**
   * Creates a new Key RecordBuilder by copying an existing Key instance.
   * @param other The existing instance to copy.
   * @return A new Key RecordBuilder
   */
  public static smbjoin.Key.Builder newBuilder(smbjoin.Key other) {
    return new smbjoin.Key.Builder(other);
  }

  /**
   * RecordBuilder for Key instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Key>
    implements org.apache.avro.data.RecordBuilder<Key> {

    private java.lang.CharSequence id;
    private java.lang.CharSequence key;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(smbjoin.Key.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.key)) {
        this.key = data().deepCopy(fields()[1].schema(), other.key);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Key instance
     * @param other The existing instance to copy.
     */
    private Builder(smbjoin.Key other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.key)) {
        this.key = data().deepCopy(fields()[1].schema(), other.key);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public java.lang.CharSequence getId() {
      return id;
    }

    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public smbjoin.Key.Builder setId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'id' field.
      * @return This builder.
      */
    public smbjoin.Key.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'key' field.
      * @return The value.
      */
    public java.lang.CharSequence getKey() {
      return key;
    }

    /**
      * Sets the value of the 'key' field.
      * @param value The value of 'key'.
      * @return This builder.
      */
    public smbjoin.Key.Builder setKey(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.key = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'key' field has been set.
      * @return True if the 'key' field has been set, false otherwise.
      */
    public boolean hasKey() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'key' field.
      * @return This builder.
      */
    public smbjoin.Key.Builder clearKey() {
      key = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Key build() {
      try {
        Key record = new Key();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.key = fieldSetFlags()[1] ? this.key : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Key>
    WRITER$ = (org.apache.avro.io.DatumWriter<Key>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Key>
    READER$ = (org.apache.avro.io.DatumReader<Key>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}