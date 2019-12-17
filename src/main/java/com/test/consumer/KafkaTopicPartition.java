package com.test.consumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class KafkaTopicPartition implements Serializable {
    private static final long serialVersionUID = 722083576322742325L;

    // ------------------------------------------------------------------------

    private final String topic;
    private final int partition;
    private final int cachedHash;

    public KafkaTopicPartition(String topic, int partition) {
        this.topic = requireNonNull(topic);
        this.partition = partition;
        this.cachedHash = 31 * topic.hashCode() + partition;
    }

    // ------------------------------------------------------------------------

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "KafkaTopicPartition{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        else if (o instanceof KafkaTopicPartition) {
            KafkaTopicPartition that = (KafkaTopicPartition) o;
            return this.partition == that.partition && this.topic.equals(that.topic);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return cachedHash;
    }

    // ------------------------------------------------------------------------
    //  Utilities
    // ------------------------------------------------------------------------

    public static String toString(Map<KafkaTopicPartition, Long> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<KafkaTopicPartition, Long> p: map.entrySet()) {
            KafkaTopicPartition ktp = p.getKey();
            sb.append(ktp.getTopic()).append(":").append(ktp.getPartition()).append("=").append(p.getValue()).append(", ");
        }
        return sb.toString();
    }

    public static String toString(List<KafkaTopicPartition> partitions) {
        StringBuilder sb = new StringBuilder();
        for (KafkaTopicPartition p: partitions) {
            sb.append(p.getTopic()).append(":").append(p.getPartition()).append(", ");
        }
        return sb.toString();
    }

    public static List<KafkaTopicPartition> dropLeaderData(List<KafkaTopicPartitionLeader> partitionInfos) {
        List<KafkaTopicPartition> ret = new ArrayList<>(partitionInfos.size());
        for (KafkaTopicPartitionLeader ktpl: partitionInfos) {
            ret.add(ktpl.getTopicPartition());
        }
        return ret;
    }

    /**
     * A {@link java.util.Comparator} for {@link KafkaTopicPartition}s.
     */
    public static class Comparator implements java.util.Comparator<KafkaTopicPartition> {
        @Override
        public int compare(KafkaTopicPartition p1, KafkaTopicPartition p2) {
            if (!p1.getTopic().equals(p2.getTopic())) {
                return p1.getTopic().compareTo(p2.getTopic());
            } else {
                return Integer.compare(p1.getPartition(), p2.getPartition());
            }
        }
    }
}
