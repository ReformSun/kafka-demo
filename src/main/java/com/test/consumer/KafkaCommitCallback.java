package com.test.consumer;

public interface KafkaCommitCallback {
    void onSuccess();
    void onException(Throwable cause);
}
