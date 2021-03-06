package smbjoin.beam;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.math.IntMath;
import com.google.common.primitives.UnsignedBytes;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Reshuffle;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import smbjoin.SerializableSchema;

public class SMBAvroInput<K, L, R> extends PTransform<PBegin, PCollection<KV<K, KV<L, R>>>> {

  private final String leftSpec; // gs://.../users/2017-01-01/*.avro
  private final String rightSpec; // gs://.../streams/2017-01-01/*.avro
  private SerializableSchema leftSchema;
  private SerializableSchema rightSchema;
  private SMBPartitioning<K, L> leftSMBPartitioning;
  private SMBPartitioning<K, R> rightSMBPartitioning;

  public SMBAvroInput(
      final String leftSpec,
      final String rightSpec,
      final SerializableSchema leftSchema,
      final SerializableSchema rightSchema,
      final SMBPartitioning<K, L> leftSMBPartitioning,
      final SMBPartitioning<K, R> rightSMBPartitioning) {
    this.leftSpec = leftSpec;
    this.rightSpec = rightSpec;
    this.leftSchema = leftSchema;
    this.rightSchema = rightSchema;
    this.leftSMBPartitioning = leftSMBPartitioning;
    this.rightSMBPartitioning = rightSMBPartitioning;
  }

  public static <K, L, R> SMBAvroInput<K, L, R> create(
      String leftSpec,
      String rightSpec,
      SerializableSchema leftSchema,
      SerializableSchema rightSchema,
      SMBPartitioning<K, L> leftSMBReader,
      SMBPartitioning<K, R> rightSMBReader) {
    return new SMBAvroInput<>(
        leftSpec, rightSpec, leftSchema, rightSchema, leftSMBReader, rightSMBReader);
  }

  @Override
  public PCollection<KV<K, KV<L, R>>> expand(final PBegin input) {
    PCollectionView<List<SMBFileMetadata>> left =
        input
            .apply("Left: Create match", Create.of(leftSpec))
            .apply("Left: Match files", FileIO.matchAll())
            .apply("Left: Extract metadata", ParDo.of(new ExtractAvroMetadataFn<L>(leftSchema)))
            .setCoder(SMBFileMetadata.coder())
            .apply("Left buckets", View.asList());

    PCollectionView<List<SMBFileMetadata>> right =
        input
            .apply("Right: Create match", Create.of(rightSpec))
            .apply("Right: Match files", FileIO.matchAll())
            .apply("Right: Extract metadata", ParDo.of(new ExtractAvroMetadataFn<R>(rightSchema)))
            .setCoder(SMBFileMetadata.coder())
            .apply("Right buckets", View.asList());

    return input
        .apply(Create.of(Collections.singletonList(0)))
        .apply(ParDo.of(new ResolveBucketing(left, right)).withSideInputs(left, right))
        .apply(Reshuffle.viaRandomKey())
        .apply(ParDo.of(new SortMergeJoinDoFn()))
        .setCoder(
            KvCoder.of(
                leftSMBPartitioning.getJoinKeyCoder(),
                KvCoder.of(
                    IterableCoder.of(leftSMBPartitioning.getRecordCoder()),
                    IterableCoder.of(rightSMBPartitioning.getRecordCoder()))))
        .apply(
            "Inner join",
            ParDo.of(
                new DoFn<KV<K, KV<Iterable<L>, Iterable<R>>>, KV<K, KV<L, R>>>() {
                  @ProcessElement
                  public void processElement(
                      @Element KV<K, KV<Iterable<L>, Iterable<R>>> input, ProcessContext c) {
                    K key = input.getKey();
                    Iterable<L> leftGroup = input.getValue().getKey();
                    Iterable<R> rightGroup = input.getValue().getValue();
                    for (L left : leftGroup) {
                      for (R right : rightGroup) {
                        c.output(KV.of(key, KV.of(left, right)));
                      }
                    }
                  }
                }))
        .setCoder(
            KvCoder.of(
                leftSMBPartitioning.getJoinKeyCoder(),
                KvCoder.of(
                    leftSMBPartitioning.getRecordCoder(), rightSMBPartitioning.getRecordCoder())));
  }

  private class ExtractAvroMetadataFn<T> extends DoFn<Metadata, SMBFileMetadata> {
    private SerializableSchema serializableSchema;
    private DatumReader<T> reader;

    ExtractAvroMetadataFn(SerializableSchema serializableSchema) {
      this.serializableSchema = serializableSchema;
    }

    @Setup
    public void setup() {
      this.reader = new SpecificDatumReader<>(serializableSchema.schema());
    }

    private SMBFileMetadata getBucketingMetadata(ResourceId resourceId, DatumReader<T> reader) {
      checkNotNull(resourceId);
      try (ReadableByteChannel channel = FileSystems.open(resourceId);
          InputStream inputStream = Channels.newInputStream(channel);
          DataFileStream<T> stream = new DataFileStream<>(inputStream, reader)) {
        return SMBFileMetadata.create(
            resourceId,
            Integer.parseInt(stream.getMetaString("smbjoin.bucketId")),
            Integer.parseInt(stream.getMetaString("smbjoin.shardId")));
      } catch (IOException e) {
        throw new RuntimeException("Can't read file");
      }
    }

    @ProcessElement
    public void processElement(@Element Metadata input, ProcessContext c) {
      c.output(getBucketingMetadata(input.resourceId(), reader));
    }
  }

  private class ResolveBucketing extends DoFn<Integer, KV<ResourceId, ResourceId>> {

    private PCollectionView<List<SMBFileMetadata>> leftShards;
    private PCollectionView<List<SMBFileMetadata>> rightShards;

    ResolveBucketing(
        PCollectionView<List<SMBFileMetadata>> leftShards,
        PCollectionView<List<SMBFileMetadata>> rightShards) {
      this.leftShards = leftShards;
      this.rightShards = rightShards;
    }

    @ProcessElement
    public void processElement(@Element final Integer input, ProcessContext c) {
      List<SMBFileMetadata> leftIt = c.sideInput(leftShards);
      List<SMBFileMetadata> rightIt = c.sideInput(rightShards);

      int gcf =
          IntMath.gcd(
              1 + leftIt.stream().mapToInt(SMBFileMetadata::bucketId).max().orElse(0),
              1 + rightIt.stream().mapToInt(SMBFileMetadata::bucketId).max().orElse(0));

      for (SMBFileMetadata left : leftIt) {
        for (SMBFileMetadata right : rightIt) {
          if (Math.floorMod(left.bucketId(), gcf) == Math.floorMod(right.bucketId(), gcf)) {
            c.output(KV.of(left.resourceId(), right.resourceId()));
          }
        }
      }
    }
  }

  private class SortMergeJoinDoFn
      extends DoFn<KV<ResourceId, ResourceId>, KV<K, KV<Iterable<L>, Iterable<R>>>> {

    private Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    private SpecificDatumReader<L> leftReader;
    private SpecificDatumReader<R> rightReader;

    private <T> K consumeIterator(
        PeekingIterator<T> iterator, ArrayList<T> buffer, SMBPartitioning<K, T> partitioning) {
      K groupKey = partitioning.getJoinKey(iterator.peek());
      byte[] encodedGroupKey = partitioning.encodeJoinKey(groupKey);
      buffer.add(iterator.next());

      boolean done = false;
      while (iterator.hasNext() && !done) {
        int compareResults =
            comparator.compare(encodedGroupKey, partitioning.getEncodedJoinKey(iterator.peek()));
        if (compareResults < 0) {
          done = true;
        } else if (compareResults == 0) {
          buffer.add(iterator.next());
        } else {
          throw new RuntimeException("Data is not sorted");
        }
      }
      return groupKey;
    }

    @Setup
    public void setup() {
      this.leftReader = new SpecificDatumReader<>(leftSchema.schema());
      this.rightReader = new SpecificDatumReader<>(rightSchema.schema());
    }

    @ProcessElement
    public void processElement(@Element KV<ResourceId, ResourceId> filePair, ProcessContext c)
        throws IOException {
      final ResourceId leftFile = filePair.getKey();
      final ResourceId rightFile = filePair.getValue();

      try (ReadableByteChannel leftChannel = FileSystems.open(leftFile);
          InputStream leftInputStream = Channels.newInputStream(leftChannel);
          DataFileStream<L> leftStream = new DataFileStream<>(leftInputStream, leftReader);
          ReadableByteChannel rightChannel = FileSystems.open(rightFile);
          InputStream rightInputStream = Channels.newInputStream(rightChannel);
          DataFileStream<R> rightStream = new DataFileStream<>(rightInputStream, rightReader)) {

        ArrayList<L> leftBuffer = new ArrayList<>();
        ArrayList<R> rightBuffer = new ArrayList<>();
        PeekingIterator<L> leftIt = Iterators.peekingIterator(leftStream);
        PeekingIterator<R> rightIt = Iterators.peekingIterator(rightStream);

        K groupKey;

        while (leftIt.hasNext() || rightIt.hasNext()) {
          leftBuffer.clear();
          rightBuffer.clear();

          if (leftIt.hasNext() && !rightIt.hasNext()) { // Right is empty, left outer join
            groupKey = consumeIterator(leftIt, leftBuffer, leftSMBPartitioning);
          } else if (!leftIt.hasNext() && rightIt.hasNext()) { // Left is empty, right outer join
            groupKey = consumeIterator(rightIt, rightBuffer, rightSMBPartitioning);
          } else {
            int compareResults =
                comparator.compare(
                    leftSMBPartitioning.getEncodedJoinKey(leftIt.peek()),
                    rightSMBPartitioning.getEncodedJoinKey(rightIt.peek()));
            if (compareResults < 0) {
              groupKey = consumeIterator(leftIt, leftBuffer, leftSMBPartitioning);
            } else if (compareResults == 0) {
              groupKey = consumeIterator(leftIt, leftBuffer, leftSMBPartitioning);
              consumeIterator(rightIt, rightBuffer, rightSMBPartitioning);
            } else {
              groupKey = consumeIterator(rightIt, rightBuffer, rightSMBPartitioning);
            }
          }
          c.output(
              KV.of(
                  groupKey,
                  KV.of(ImmutableList.copyOf(leftBuffer), ImmutableList.copyOf(rightBuffer))));
        }
      }
    }
  }
}
