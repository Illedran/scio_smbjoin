import argparse
import os
import random
import tempfile
from collections import namedtuple
from concurrent.futures import ProcessPoolExecutor
from pathlib import Path
from typing import *

import itertools
import math
from fastavro import parse_schema, writer
import json

GeneratorArgs = namedtuple('GeneratorArgs',
                           ['input_count', 'schema', 'output_path',
                            'key_bits', 'num_buckets', 'zipf_shape',
                            'data_skew'])


def _partition_n_over_k(n: int, k: int, weights: Collection[float] = None) -> \
        List[int]:
    if weights is None:
        return [n // k + (i < n % k) for i in range(k)]
    else:
        data = [int(n * w / sum(weights)) for w in weights]
        assert (len(data) == k)
        for i in range(n - sum(data)):
            data[i] += 1
        return data


def generator(count, bucket_idx, gen_args: GeneratorArgs):
    max_key = 1 << gen_args.key_bits
    key_length = math.ceil(gen_args.key_bits / 4)

    def get_record(seed):
        key = (bucket_idx + gen_args.num_buckets * seed) % max_key
        return {"id": "%0*x" % (key_length, key)}

    skewed_count = int(count * gen_args.data_skew)
    data_count = count - skewed_count

    skewed_seed = random.getrandbits(gen_args.key_bits)

    skewed_gen = map(get_record, itertools.repeat(skewed_seed, skewed_count))
    data_gen = map(get_record, (random.getrandbits(gen_args.key_bits) for _ in
                                itertools.repeat(None, data_count)))

    while True:
        if skewed_count + data_count == 0:
            break
        if skewed_count == 0:
            yield from data_gen
            break
        if data_count == 0:
            yield from skewed_gen
            break
        p = skewed_count / (skewed_count + data_count)
        if p > random.random():
            yield next(skewed_gen)
            skewed_count -= 1
        else:
            yield next(data_gen)
            data_count -= 1


def generate(worker_id, worker_share, gen_args: GeneratorArgs):
    H = [1. / (i ** gen_args.zipf_shape) for i in
         range(1, gen_args.num_buckets + 1)]

    data = _partition_n_over_k(worker_share, gen_args.num_buckets, H)

    gen_args.output_path.mkdir(parents=True, exist_ok=True)

    with tempfile.NamedTemporaryFile(delete=False) as f:  # Atomic write
        for i in sorted(range(gen_args.num_buckets),
                        key=lambda x: random.random()):
            writer(f, gen_args.schema, generator(data[i], i, gen_args), codec='deflate')

        save_path = gen_args.output_path.joinpath(
                "part{}-c{}-b{}-s{:.2f}.avro".format(worker_id,
                                                     gen_args.input_count,
                                                     gen_args.num_buckets,
                                                     gen_args.zipf_shape))
        os.replace(f.name, str(save_path))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('count', type=int,
                        help="Number of records to generate.")
    parser.add_argument('--schema-file', metavar='path/to/schema', type=Path,
                        default=Path(__file__).resolve().parent.parent.joinpath(
                                "schemas", "Record.avsc"),
                        help="Path to schema file. (default: %(default)s)")
    parser.add_argument('--dir', metavar='path/to/output', type=Path,
                        help="Save output to a directory. (default: this directory)",
                        default=Path('.').resolve())
    parser.add_argument('-k', '--key-bits', type=int,
                        help="Size of join key in bits. (default: %(default)s)",
                        default=31)

    group = parser.add_argument_group('How to generate skewed data',
                                      description="You can specify the following arguments to create data that is artificially skewed through partitioning skew for a identity function partitioner. "
                                                  "The frequencies of each bucket will follow a Zipf distribution with parameters b and s. "
                                                  "The default parameters result in a uniform distribution of frequencies.")

    group.add_argument('-b', '--num-buckets', type=int,
                       help="Number of buckets. Should be power of 2. (default: %(default)s)",
                       default=1)
    group.add_argument('-s', '--zipf_shape', type=float,
                       help="Shape parameter of the Zipf distribution. (default: %(default)s)",
                       default=0.0)
    group.add_argument('-d', '--data-skew', type=float,
                       help="Fraction of skewed data. (default: %(default)s)",
                       default=0.0)

    args = parser.parse_args()

    if args.key_bits < 1:
        raise ValueError("Key size must be > 0.")
    if args.num_buckets < 1 or args.num_buckets & args.num_buckets - 1:
        raise ValueError("Number of buckets must be power of 2.")
    if not 0 <= args.data_skew <= 1:
        raise ValueError("Data skew fraction must be 0 <= d <= 1.")
    if args.zipf_shape < 0:
        raise ValueError("Zipf shape parameter must be > 0.")

    with open(args.schema_file) as f:
        parsed_schema = parse_schema(json.load(f))

    gen_args = GeneratorArgs(args.count, parsed_schema, args.dir,
                             args.key_bits, args.num_buckets, args.zipf_shape,
                             args.data_skew)

    worker_count = os.cpu_count()
    with ProcessPoolExecutor(worker_count) as pool:
        for worker_id, worker_share in enumerate(
                _partition_n_over_k(args.count, worker_count)):
            pool.submit(generate, worker_id, worker_share, gen_args)
    # generate(0, args.count, gen_args)


if __name__ == '__main__':
    main()
