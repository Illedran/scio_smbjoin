#!/usr/bin/env bash
set -e
CURRENT_DIR=$(pwd)
FILE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Configuration
NUM_WORKERS=32
NUM_WORKERS_KEYS=8


BUCKET_SIZE_MB=300    # For SkewAdj buckets
ZIPF_SHAPES="1.10 1.20 1.30 1.40"

# Vars
GCS_BUCKET='gs://andrea_smb_test'
DATA_BUCKET="${GCS_BUCKET}/generated_data"
SCHEMA_DIR="${GCS_BUCKET}/schemas"

TMP_LOCATION="${GCS_BUCKET}/tmp"
STAGING_LOCATION="${TMP_LOCATION}/staging"

EVENT_SCHEMA="${SCHEMA_DIR}/Event.avsc"
KEY_SCHEMA="${SCHEMA_DIR}/Key.avsc"

DATAFLOW_ARGS="--numWorkers=${NUM_WORKERS}  --maxNumWorkers=${NUM_WORKERS} --tempLocation=${TMP_LOCATION} --stagingLocation=${STAGING_LOCATION} --project=***REMOVED*** --runner=DataflowRunner --region=europe-west1 --workerMachineType=n1-standard-4"
DATAFLOW_ARGS_KEYS="--numWorkers=${NUM_WORKERS_KEYS}  --maxNumWorkers=${NUM_WORKERS_KEYS} --autoscalingAlgorithm=NONE --tempLocation=${TMP_LOCATION} --stagingLocation=${STAGING_LOCATION} --project=***REMOVED*** --runner=DataflowRunner --region=europe-west1 --workerMachineType=n1-standard-4"

INPUT_KEYS="${DATA_BUCKET}/keys/*.avro"

OUTPUT_KEYS_BUCKETED_SKEWADJ="${DATA_BUCKET}/bucketed_keys_skewadj"

echo "Compiling..." && cd ${FILE_DIR}/.. && sbt ";compile ;pack"
echo "Generating data..."
time=$(date +%s)

#gsutil -m rm -r ${OUTPUT_KEYS_BUCKETED_SKEWADJ} || true
#target/pack/bin/smb-make-buckets-skew-adj-job --jobName="smbmakebuckets-skewadj-keys-$time-$( printf "%04x%04x" $RANDOM $RANDOM )"  --input=${INPUT_KEYS} --output=${OUTPUT_KEYS_BUCKETED_SKEWADJ} --bucketSizeMB=${BUCKET_SIZE_MB} --schemaFile=${KEY_SCHEMA} ${DATAFLOW_ARGS_KEYS}
for i in ${ZIPF_SHAPES}; do
  INPUT_EVENTS="${DATA_BUCKET}/events/s$i/*.avro"
  OUTPUT_EVENTS_BUCKETED_SKEWADJ="${DATA_BUCKET}/bucketed_events_skewadj/s$i"

  sStr=${i/./}
  time=$(date +%s)

  gsutil -m rm -r ${OUTPUT_EVENTS_BUCKETED_SKEWADJ} || true
  target/pack/bin/smb-make-buckets-skew-adj-job --jobName="smbmakebuckets-skewadj-events-s$sStr-$time-$( printf "%04x%04x" $RANDOM $RANDOM )" --input=${INPUT_EVENTS} --output=${OUTPUT_EVENTS_BUCKETED_SKEWADJ} --bucketSizeMB=${BUCKET_SIZE_MB} --schemaFile=${EVENT_SCHEMA} ${DATAFLOW_ARGS}
done;


cd ${CURRENT_DIR}
