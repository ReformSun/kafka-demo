package com.test.consumer;

import org.apache.kafka.common.Node;

import java.io.Serializable;

public class KafkaTopicPartitionLeader implements Serializable {
    private static final long serialVersionUID = 9145855900303748582L;

    private final int leaderId;
    private final int leaderPort;
    private final String leaderHost;
    private final KafkaTopicPartition topicPartition;
    private final int cachedHash;

    public KafkaTopicPartitionLeader(KafkaTopicPartition topicPartition, Node leader) {
        this.topicPartition = topicPartition;
        if (leader == null) {
            this.leaderId = -1;
            this.leaderHost = null;
            this.leaderPort = -1;
        } else {
            this.leaderId = leader.id();
            this.leaderPort = leader.port();
            this.leaderHost = leader.host();
        }
        int cachedHash = (leader == null) ? 14 : leader.hashCode();
        this.cachedHash = 31 * cachedHash + topicPartition.hashCode();
    }

    public KafkaTopicPartition getTopicPartition() {
        return topicPartition;
    }

    public Node getLeader() {
        if (this.leaderId == -1) {
            return null;
        } else {
            return new Node(leaderId, leaderHost, leaderPort);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KafkaTopicPartitionLeader)) {
            return false;
        }

        KafkaTopicPartitionLeader that = (KafkaTopicPartitionLeader) o;

        if (!topicPartition.equals(that.topicPartition)) {
            return false;
        }
        return leaderId == that.leaderId && leaderPort == that.leaderPort && leaderHost.equals(that.leaderHost);
    }

    @Override
    public int hashCode() {
        return cachedHash;
    }

    @Override
    public String toString() {
        return "KafkaTopicPartitionLeader{" +
                "leaderId=" + leaderId +
                ", leaderPort=" + leaderPort +
                ", leaderHost='" + leaderHost + '\'' +
                ", topic=" + topicPartition.getTopic() +
                ", partition=" + topicPartition.getPartition() +
                '}';
    }
}
