/**
 * Autogenerated by Avro
 *
 * <p>DO NOT EDIT DIRECTLY
 */
package smbjoin;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Event extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"Event\",\"namespace\":\"smbjoin\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"payload\",\"type\":\"bytes\"}]}");
  private static final long serialVersionUID = -8160154711047012487L;
  private static SpecificData MODEL$ = new SpecificData();
  private static final BinaryMessageEncoder<Event> ENCODER =
      new BinaryMessageEncoder<Event>(MODEL$, SCHEMA$);
  private static final BinaryMessageDecoder<Event> DECODER =
      new BinaryMessageDecoder<Event>(MODEL$, SCHEMA$);

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Event> WRITER$ =
      (org.apache.avro.io.DatumWriter<Event>) MODEL$.createDatumWriter(SCHEMA$);

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Event> READER$ =
      (org.apache.avro.io.DatumReader<Event>) MODEL$.createDatumReader(SCHEMA$);

  @Deprecated public int id;
  @Deprecated public java.nio.ByteBuffer payload;

  /**
   * Default constructor. Note that this does not initialize fields to their default values from the
   * schema. If that is desired then one should use <code>newBuilder()</code>.
   */
  public Event() {}

  /**
   * All-args constructor.
   *
   * @param id The new value for id
   * @param payload The new value for payload
   */
  public Event(java.lang.Integer id, java.nio.ByteBuffer payload) {
    this.id = id;
    this.payload = payload;
  }

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  /** Return the BinaryMessageDecoder instance used by this class. */
  public static BinaryMessageDecoder<Event> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link
   * SchemaStore}.
   *
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<Event> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Event>(MODEL$, SCHEMA$, resolver);
  }

  /** Deserializes a Event from a ByteBuffer. */
  public static Event fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /**
   * Creates a new Event RecordBuilder.
   *
   * @return A new Event RecordBuilder
   */
  public static smbjoin.Event.Builder newBuilder() {
    return new smbjoin.Event.Builder();
  }

  /**
   * Creates a new Event RecordBuilder by copying an existing Builder.
   *
   * @param other The existing builder to copy.
   * @return A new Event RecordBuilder
   */
  public static smbjoin.Event.Builder newBuilder(smbjoin.Event.Builder other) {
    return new smbjoin.Event.Builder(other);
  }

  /**
   * Creates a new Event RecordBuilder by copying an existing Event instance.
   *
   * @param other The existing instance to copy.
   * @return A new Event RecordBuilder
   */
  public static smbjoin.Event.Builder newBuilder(smbjoin.Event other) {
    return new smbjoin.Event.Builder(other);
  }

  /** Serializes this Event to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  public org.apache.avro.Schema getSchema() {
    return SCHEMA$;
  }

  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
      case 0:
        return id;
      case 1:
        return payload;
      default:
        throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value = "unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
      case 0:
        id = (java.lang.Integer) value$;
        break;
      case 1:
        payload = (java.nio.ByteBuffer) value$;
        break;
      default:
        throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   *
   * @return The value of the 'id' field.
   */
  public java.lang.Integer getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   *
   * @param value the value to set.
   */
  public void setId(java.lang.Integer value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'payload' field.
   *
   * @return The value of the 'payload' field.
   */
  public java.nio.ByteBuffer getPayload() {
    return payload;
  }

  /**
   * Sets the value of the 'payload' field.
   *
   * @param value the value to set.
   */
  public void setPayload(java.nio.ByteBuffer value) {
    this.payload = value;
  }

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @Override
  public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  /** RecordBuilder for Event instances. */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Event>
      implements org.apache.avro.data.RecordBuilder<Event> {

    private int id;
    private java.nio.ByteBuffer payload;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     *
     * @param other The existing Builder to copy.
     */
    private Builder(smbjoin.Event.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.payload)) {
        this.payload = data().deepCopy(fields()[1].schema(), other.payload);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Event instance
     *
     * @param other The existing instance to copy.
     */
    private Builder(smbjoin.Event other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.payload)) {
        this.payload = data().deepCopy(fields()[1].schema(), other.payload);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Gets the value of the 'id' field.
     *
     * @return The value.
     */
    public java.lang.Integer getId() {
      return id;
    }

    /**
     * Sets the value of the 'id' field.
     *
     * @param value The value of 'id'.
     * @return This builder.
     */
    public smbjoin.Event.Builder setId(int value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'id' field has been set.
     *
     * @return True if the 'id' field has been set, false otherwise.
     */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'id' field.
     *
     * @return This builder.
     */
    public smbjoin.Event.Builder clearId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
     * Gets the value of the 'payload' field.
     *
     * @return The value.
     */
    public java.nio.ByteBuffer getPayload() {
      return payload;
    }

    /**
     * Sets the value of the 'payload' field.
     *
     * @param value The value of 'payload'.
     * @return This builder.
     */
    public smbjoin.Event.Builder setPayload(java.nio.ByteBuffer value) {
      validate(fields()[1], value);
      this.payload = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
     * Checks whether the 'payload' field has been set.
     *
     * @return True if the 'payload' field has been set, false otherwise.
     */
    public boolean hasPayload() {
      return fieldSetFlags()[1];
    }

    /**
     * Clears the value of the 'payload' field.
     *
     * @return This builder.
     */
    public smbjoin.Event.Builder clearPayload() {
      payload = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Event build() {
      try {
        Event record = new Event();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.Integer) defaultValue(fields()[0]);
        record.payload =
            fieldSetFlags()[1] ? this.payload : (java.nio.ByteBuffer) defaultValue(fields()[1]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
