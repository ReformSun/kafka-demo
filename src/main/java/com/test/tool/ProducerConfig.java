package com.test.tool;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.apache.kafka.common.config.ConfigDef.Range.atLeast;
import static org.apache.kafka.common.config.ConfigDef.Range.between;
import static org.apache.kafka.common.config.ConfigDef.ValidString.in;

public class ProducerConfig {
    /*
     * NOTE: DO NOT CHANGE EITHER CONFIG STRINGS OR THEIR JAVA VARIABLE NAMES AS THESE ARE PART OF THE PUBLIC API AND
     * CHANGE WILL BREAK USER CODE.
     */

    private static final ConfigDef CONFIG;

    /** <code>bootstrap.servers</code> */
    public static final String BOOTSTRAP_SERVERS_CONFIG = CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

    /** <code>metadata.max.age.ms</code> */
    public static final String METADATA_MAX_AGE_CONFIG = CommonClientConfigs.METADATA_MAX_AGE_CONFIG;
    private static final String METADATA_MAX_AGE_DOC = CommonClientConfigs.METADATA_MAX_AGE_DOC;

    /** <code>batch.size</code> */
    public static final String BATCH_SIZE_CONFIG = "batch.size";
    /**
     * 这个配置决定了每个批为多大，当达到这个大小时，会发送一个请求
     * 比如设置成10KB大客户端积累的数据达到，10KB时，就会发送一个请求
     * 这个参数默认是16kb
     * 理论上来说。这个值越大，一次发送的数据量越大，减少了与broker的io次数
     * 所以要根据生产环境的实际情况设置一个合理的大小
     */
    private static final String BATCH_SIZE_DOC = "The producer will attempt to batch records together into fewer requests whenever multiple records are being sent"
            + " to the same partition. This helps performance on both the client and the server. This configuration controls the "
            + "default batch size in bytes. "
            + "<p>"
            + "No attempt will be made to batch records larger than this size. "
            + "<p>"
            + "Requests sent to brokers will contain multiple batches, one for each partition with data available to be sent. "
            + "<p>"
            + "A small batch size will make batching less common and may reduce throughput (a batch size of zero will disable "
            + "batching entirely). A very large batch size may use memory a bit more wastefully as we will always allocate a "
            + "buffer of the specified batch size in anticipation of additional records.";

    /**
     * 持久化机制
     * 这个参数决定了发送出去的数据要采用什么样的持久化方式
     */
    /** <code>acks</code> */
    public static final String ACKS_CONFIG = "acks";
    private static final String ACKS_DOC = "The number of acknowledgments the producer requires the leader to have received before considering a request complete. This controls the "
            + " durability of records that are sent. The following settings are allowed: "
            + " <ul>"
            + " <li><code>acks=0</code> If set to zero then the producer will not wait for any acknowledgment from the"
            + " server at all. The record will be immediately added to the socket buffer and considered sent. No guarantee can be"
            + " made that the server has received the record in this case, and the <code>retries</code> configuration will not"
            + " take effect (as the client won't generally know of any failures). The offset given back for each record will"
            + " always be set to -1."
            + " <li><code>acks=1</code> This will mean the leader will write the record to its local log but will respond"
            + " without awaiting full acknowledgement from all followers. In this case should the leader fail immediately after"
            + " acknowledging the record but before the followers have replicated it then the record will be lost."
            + " <li><code>acks=all</code> This means the leader will wait for the full set of in-sync replicas to"
            + " acknowledge the record. This guarantees that the record will not be lost as long as at least one in-sync replica"
            + " remains alive. This is the strongest available guarantee. This is equivalent to the acks=-1 setting.";

    /** <code>linger.ms</code> */
    /**
     * 最多逗留的时间
     * 表示：当bacth设置的过大时，批缓存一直没有被填满，不能无限制的等待下去，需要设置一个最长的等待时间
     * 这个配置就是设置这个时间的
     */
    public static final String LINGER_MS_CONFIG = "linger.ms";
    private static final String LINGER_MS_DOC = "The producer groups together any records that arrive in between request transmissions into a single batched request. "
            + "Normally this occurs only under load when records arrive faster than they can be sent out. However in some circumstances the client may want to "
            + "reduce the number of requests even under moderate load. This setting accomplishes this by adding a small amount "
            + "of artificial delay&mdash;that is, rather than immediately sending out a record the producer will wait for up to "
            + "the given delay to allow other records to be sent so that the sends can be batched together. This can be thought "
            + "of as analogous to Nagle's algorithm in TCP. This setting gives the upper bound on the delay for batching: once "
            + "we get <code>" + BATCH_SIZE_CONFIG + "</code> worth of records for a partition it will be sent immediately regardless of this "
            + "setting, however if we have fewer than this many bytes accumulated for this partition we will 'linger' for the "
            + "specified time waiting for more records to show up. This setting defaults to 0 (i.e. no delay). Setting <code>" + LINGER_MS_CONFIG + "=5</code>, "
            + "for example, would have the effect of reducing the number of requests sent but would add up to 5ms of latency to records sent in the absense of load.";

    /** <code>client.id</code> */
    public static final String CLIENT_ID_CONFIG = CommonClientConfigs.CLIENT_ID_CONFIG;

    /** <code>send.buffer.bytes</code> */
    public static final String SEND_BUFFER_CONFIG = CommonClientConfigs.SEND_BUFFER_CONFIG;

    /** <code>receive.buffer.bytes</code> */
    public static final String RECEIVE_BUFFER_CONFIG = CommonClientConfigs.RECEIVE_BUFFER_CONFIG;

    /** <code>max.request.size</code> */
    public static final String MAX_REQUEST_SIZE_CONFIG = "max.request.size";
    private static final String MAX_REQUEST_SIZE_DOC = "The maximum size of a request in bytes. This setting will limit the number of record "
            + "batches the producer will send in a single request to avoid sending huge requests. "
            + "This is also effectively a cap on the maximum record batch size. Note that the server "
            + "has its own cap on record batch size which may be different from this.";

    /** <code>reconnect.backoff.ms</code> */
    public static final String RECONNECT_BACKOFF_MS_CONFIG = CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG;

    /** <code>reconnect.backoff.max.ms</code> */
    public static final String RECONNECT_BACKOFF_MAX_MS_CONFIG = CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_CONFIG;

    /** <code>max.block.ms</code> */
    /**
     * 当设置的buffer.memory比较小
     * 然而记录的发送速度比他们能交付服务器的速度快时，消费者将会被堵塞
     * 当堵塞超过设置的max.block.ms时间时，会抛出异常
     */
    public static final String MAX_BLOCK_MS_CONFIG = "max.block.ms";
    private static final String MAX_BLOCK_MS_DOC = "The configuration controls how long <code>KafkaProducer.send()</code> and <code>KafkaProducer.partitionsFor()</code> will block."
            + "These methods can be blocked either because the buffer is full or metadata unavailable."
            + "Blocking in the user-supplied serializers or partitioner will not be counted against this timeout.";

    /** <code>buffer.memory</code> */
    /**
     * 缓存记录直到被发送，消费者能够使用的总的内存字节数
     * 理解： 生产者发送数据，会在本地建立一个缓冲区，发送记录会被放入缓冲区，然后发送到服务端
     * 这个缓冲区决定了最大的记录缓冲数据
     * 当缓冲区被填满的时候，就会阻塞用户线程，不让继续向里面写入数据
     */
    public static final String BUFFER_MEMORY_CONFIG = "buffer.memory";
    private static final String BUFFER_MEMORY_DOC = "The total bytes of memory the producer can use to buffer records waiting to be sent to the server. If records are "
            + "sent faster than they can be delivered to the server the producer will block for <code>" + MAX_BLOCK_MS_CONFIG + "</code> after which it will throw an exception."
            + "<p>"
            + "This setting should correspond roughly to the total memory the producer will use, but is not a hard bound since "
            + "not all memory the producer uses is used for buffering. Some additional memory will be used for compression (if "
            + "compression is enabled) as well as for maintaining in-flight requests.";



    /** <code>compression.type</code> */
    /**
     * 也可以设置压缩类型
     * 压缩类型设置默认设置为none
     * 压缩是针对全量的批数据进行压缩
     */
    public static final String COMPRESSION_TYPE_CONFIG = "compression.type";
    private static final String COMPRESSION_TYPE_DOC = "The compression type for all data generated by the producer. The default is none (i.e. no compression). Valid "
            + " values are <code>none</code>, <code>gzip</code>, <code>snappy</code>, or <code>lz4</code>. "
            + "Compression is of full batches of data, so the efficacy of batching will also impact the compression ratio (more batching means better compression).";

    /** <code>metrics.sample.window.ms</code> */
    /**
     * 可以设置kafka的测量值采集窗口大小
     *
     */
    public static final String METRICS_SAMPLE_WINDOW_MS_CONFIG = CommonClientConfigs.METRICS_SAMPLE_WINDOW_MS_CONFIG;

    /** <code>metrics.num.samples</code> */
    public static final String METRICS_NUM_SAMPLES_CONFIG = CommonClientConfigs.METRICS_NUM_SAMPLES_CONFIG;

    /**
     * <code>metrics.log.level</code>
     */
    public static final String METRICS_RECORDING_LEVEL_CONFIG = CommonClientConfigs.METRICS_RECORDING_LEVEL_CONFIG;

    /** <code>metric.reporters</code> */
    /**
     * 设置采集器报告类型
     */
    public static final String METRIC_REPORTER_CLASSES_CONFIG = CommonClientConfigs.METRIC_REPORTER_CLASSES_CONFIG;

    /** <code>max.in.flight.requests.per.connection</code> */
    public static final String MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = "max.in.flight.requests.per.connection";
    private static final String MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION_DOC = "The maximum number of unacknowledged requests the client will send on a single connection before blocking."
            + " Note that if this setting is set to be greater than 1 and there are failed sends, there is a risk of"
            + " message re-ordering due to retries (i.e., if retries are enabled).";

    /** <code>retries</code> */
    /**
     * 重试机制
     * 也就是一次请求失败了，重新尝试连接的次数
     *
     */
    public static final String RETRIES_CONFIG = "retries";
    private static final String RETRIES_DOC = "Setting a value greater than zero will cause the client to resend any record whose send fails with a potentially transient error."
            + " Note that this retry is no different than if the client resent the record upon receiving the error."
            + " Allowing retries without setting <code>" + MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION + "</code> to 1 will potentially change the"
            + " ordering of records because if two batches are sent to a single partition, and the first fails and is retried but the second"
            + " succeeds, then the records in the second batch may appear first.";
    /**
     * 每次重试的间隔时间
     */
    /** <code>retry.backoff.ms</code> */
    public static final String RETRY_BACKOFF_MS_CONFIG = CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG;

    /** <code>key.serializer</code> */
    public static final String KEY_SERIALIZER_CLASS_CONFIG = "key.serializer";
    public static final String KEY_SERIALIZER_CLASS_DOC = "Serializer class for key that implements the <code>Serializer</code> interface.";

    /** <code>value.serializer</code> */
    public static final String VALUE_SERIALIZER_CLASS_CONFIG = "value.serializer";
    public static final String VALUE_SERIALIZER_CLASS_DOC = "Serializer class for value that implements the <code>Serializer</code> interface.";

    /** <code>connections.max.idle.ms</code> */
    public static final String CONNECTIONS_MAX_IDLE_MS_CONFIG = CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_CONFIG;

    /** <code>partitioner.class</code> */
    public static final String PARTITIONER_CLASS_CONFIG = "partitioner.class";
    private static final String PARTITIONER_CLASS_DOC = "Partitioner class that implements the <code>Partitioner</code> interface.";

    /** <code>request.timeout.ms</code> */
    public static final String REQUEST_TIMEOUT_MS_CONFIG = CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG;
    private static final String REQUEST_TIMEOUT_MS_DOC = CommonClientConfigs.REQUEST_TIMEOUT_MS_DOC
            + " This should be larger than replica.lag.time.max.ms (a broker configuration)"
            + " to reduce the possibility of message duplication due to unnecessary producer retries.";

    /** <code>interceptor.classes</code> */
    public static final String INTERCEPTOR_CLASSES_CONFIG = "interceptor.classes";
    public static final String INTERCEPTOR_CLASSES_DOC = "A list of classes to use as interceptors. "
            + "Implementing the <code>ProducerInterceptor</code> interface allows you to intercept (and possibly mutate) the records "
            + "received by the producer before they are published to the Kafka cluster. By default, there are no interceptors.";

    /** <code>enable.idempotence</code> */
    public static final String ENABLE_IDEMPOTENCE_CONFIG = "enable.idempotence";
    public static final String ENABLE_IDEMPOTENCE_DOC = "When set to 'true', the producer will ensure that exactly one copy of each message is written in the stream. If 'false', producer "
            + "retries due to broker failures, etc., may write duplicates of the retried message in the stream. This is set to 'false' by default. "
            + "Note that enabling idempotence requires <code>" + MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION + "</code> to be set to 1 and "
            + "<code>" + RETRIES_CONFIG + "</code> cannot be zero. Additionally " + ACKS_CONFIG + " must be set to 'all'. If these values "
            + "are left at their defaults, we will override the default to be suitable. "
            + "If the values are set to something incompatible with the idempotent producer, a ConfigException will be thrown.";

    /** <code> transaction.timeout.ms </code> */
    public static final String TRANSACTION_TIMEOUT_CONFIG = "transaction.timeout.ms";
    public static final String TRANSACTION_TIMEOUT_DOC = "The maximum amount of time in ms that the transaction coordinator will wait for a transaction status update from the producer before proactively aborting the ongoing transaction." +
            "If this value is larger than the max.transaction.timeout.ms setting in the broker, the request will fail with a `InvalidTransactionTimeout` error.";

    /** <code> transactional.id </code> */
    public static final String TRANSACTIONAL_ID_CONFIG = "transactional.id";
    public static final String TRANSACTIONAL_ID_DOC = "The TransactionalId to use for transactional delivery. This enables reliability semantics which span multiple producer sessions since it allows the client to guarantee that transactions using the same TransactionalId have been completed prior to starting any new transactions. If no TransactionalId is provided, then the producer is limited to idempotent delivery. " +
            "Note that enable.idempotence must be enabled if a TransactionalId is configured. " +
            "The default is empty, which means transactions cannot be used.";

    static {
        CONFIG = new ConfigDef().define(BOOTSTRAP_SERVERS_CONFIG, ConfigDef.Type.LIST, ConfigDef.Importance.HIGH, CommonClientConfigs.BOOTSTRAP_SERVERS_DOC)
                .define(BUFFER_MEMORY_CONFIG, ConfigDef.Type.LONG, 32 * 1024 * 1024L, atLeast(0L), ConfigDef.Importance.HIGH, BUFFER_MEMORY_DOC)
                .define(RETRIES_CONFIG, ConfigDef.Type.INT, 0, between(0, Integer.MAX_VALUE), ConfigDef.Importance.HIGH, RETRIES_DOC)
                .define(ACKS_CONFIG,
                        ConfigDef.Type.STRING,
                        "1",
                        in("all", "-1", "0", "1"),
                        ConfigDef.Importance.HIGH,
                        ACKS_DOC)
                .define(COMPRESSION_TYPE_CONFIG, ConfigDef.Type.STRING, "none", ConfigDef.Importance.HIGH, COMPRESSION_TYPE_DOC)
                .define(BATCH_SIZE_CONFIG, ConfigDef.Type.INT, 16384, atLeast(0), ConfigDef.Importance.MEDIUM, BATCH_SIZE_DOC)
                .define(LINGER_MS_CONFIG, ConfigDef.Type.LONG, 0, atLeast(0L), ConfigDef.Importance.MEDIUM, LINGER_MS_DOC)
                .define(CLIENT_ID_CONFIG, ConfigDef.Type.STRING, "", ConfigDef.Importance.MEDIUM, CommonClientConfigs.CLIENT_ID_DOC)
                .define(SEND_BUFFER_CONFIG, ConfigDef.Type.INT, 128 * 1024, atLeast(-1), ConfigDef.Importance.MEDIUM, CommonClientConfigs.SEND_BUFFER_DOC)
                .define(RECEIVE_BUFFER_CONFIG, ConfigDef.Type.INT, 32 * 1024, atLeast(-1), ConfigDef.Importance.MEDIUM, CommonClientConfigs.RECEIVE_BUFFER_DOC)
                .define(MAX_REQUEST_SIZE_CONFIG,
                        ConfigDef.Type.INT,
                        1 * 1024 * 1024,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        MAX_REQUEST_SIZE_DOC)
                .define(RECONNECT_BACKOFF_MS_CONFIG, ConfigDef.Type.LONG, 50L, atLeast(0L), ConfigDef.Importance.LOW, CommonClientConfigs.RECONNECT_BACKOFF_MS_DOC)
                .define(RECONNECT_BACKOFF_MAX_MS_CONFIG, ConfigDef.Type.LONG, 1000L, atLeast(0L), ConfigDef.Importance.LOW, CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_DOC)
                .define(RETRY_BACKOFF_MS_CONFIG, ConfigDef.Type.LONG, 100L, atLeast(0L), ConfigDef.Importance.LOW, CommonClientConfigs.RETRY_BACKOFF_MS_DOC)
                .define(MAX_BLOCK_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        60 * 1000,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        MAX_BLOCK_MS_DOC)
                .define(REQUEST_TIMEOUT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        30 * 1000,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        REQUEST_TIMEOUT_MS_DOC)
                .define(METADATA_MAX_AGE_CONFIG, ConfigDef.Type.LONG, 5 * 60 * 1000, atLeast(0), ConfigDef.Importance.LOW, METADATA_MAX_AGE_DOC)
                .define(METRICS_SAMPLE_WINDOW_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        30000,
                        atLeast(0),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METRICS_SAMPLE_WINDOW_MS_DOC)
                .define(METRICS_NUM_SAMPLES_CONFIG, ConfigDef.Type.INT, 2, atLeast(1), ConfigDef.Importance.LOW, CommonClientConfigs.METRICS_NUM_SAMPLES_DOC)
                .define(METRICS_RECORDING_LEVEL_CONFIG,
                        ConfigDef.Type.STRING,
                        Sensor.RecordingLevel.INFO.toString(),
                        in(Sensor.RecordingLevel.INFO.toString(), Sensor.RecordingLevel.DEBUG.toString()),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METRICS_RECORDING_LEVEL_DOC)
                .define(METRIC_REPORTER_CLASSES_CONFIG,
                        ConfigDef.Type.LIST,
                        "",
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METRIC_REPORTER_CLASSES_DOC)
                .define(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                        ConfigDef.Type.INT,
                        5,
                        atLeast(1),
                        ConfigDef.Importance.LOW,
                        MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION_DOC)
                .define(KEY_SERIALIZER_CLASS_CONFIG,
                        ConfigDef.Type.CLASS,
                        ConfigDef.Importance.HIGH,
                        KEY_SERIALIZER_CLASS_DOC)
                .define(VALUE_SERIALIZER_CLASS_CONFIG,
                        ConfigDef.Type.CLASS,
                        ConfigDef.Importance.HIGH,
                        VALUE_SERIALIZER_CLASS_DOC)
                                /* default is set to be a bit lower than the server default (10 min), to avoid both client and server closing connection at same time */
                .define(CONNECTIONS_MAX_IDLE_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        9 * 60 * 1000,
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_DOC)
                .define(PARTITIONER_CLASS_CONFIG,
                        ConfigDef.Type.CLASS,
                        DefaultPartitioner.class,
                        ConfigDef.Importance.MEDIUM, PARTITIONER_CLASS_DOC)
                .define(INTERCEPTOR_CLASSES_CONFIG,
                        ConfigDef.Type.LIST,
                        null,
                        ConfigDef.Importance.LOW,
                        INTERCEPTOR_CLASSES_DOC)
                .define(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
                        ConfigDef.Type.STRING,
                        CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL,
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.SECURITY_PROTOCOL_DOC)
                .withClientSslSupport()
                .withClientSaslSupport()
                .define(ENABLE_IDEMPOTENCE_CONFIG,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.LOW,
                        ENABLE_IDEMPOTENCE_DOC)
                .define(TRANSACTION_TIMEOUT_CONFIG,
                        ConfigDef.Type.INT,
                        60000,
                        ConfigDef.Importance.LOW,
                        TRANSACTION_TIMEOUT_DOC)
                .define(TRANSACTIONAL_ID_CONFIG,
                        ConfigDef.Type.STRING,
                        null,
                        new ConfigDef.NonEmptyString(),
                        ConfigDef.Importance.LOW,
                        TRANSACTIONAL_ID_DOC);
    }

    public static void main(String[] args) {
        System.out.println(CONFIG.toHtmlTable());
    }

}
