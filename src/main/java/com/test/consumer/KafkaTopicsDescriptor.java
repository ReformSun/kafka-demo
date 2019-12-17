package com.test.consumer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

import static com.sun.util.Preconditions.checkArgument;

public class KafkaTopicsDescriptor {
    private static final long serialVersionUID = -3807227764764900975L;

    private final List<String> fixedTopics;
    private final Pattern topicPattern;

    public KafkaTopicsDescriptor(@Nullable List<String> fixedTopics, @Nullable Pattern topicPattern) {
        checkArgument((fixedTopics != null && topicPattern == null) || (fixedTopics == null && topicPattern != null),
                "Exactly one of either fixedTopics or topicPattern must be specified.");

        if (fixedTopics != null) {
            checkArgument(!fixedTopics.isEmpty(), "If subscribing to a fixed topics list, the supplied list cannot be empty.");
        }

        this.fixedTopics = fixedTopics;
        this.topicPattern = topicPattern;
    }

    public boolean isFixedTopics() {
        return fixedTopics != null;
    }

    public boolean isTopicPattern() {
        return topicPattern != null;
    }

    public List<String> getFixedTopics() {
        return fixedTopics;
    }

    public Pattern getTopicPattern() {
        return topicPattern;
    }

    @Override
    public String toString() {
        return (fixedTopics == null)
                ? "Topic Regex Pattern (" + topicPattern.pattern() + ")"
                : "Fixed Topics (" + fixedTopics + ")";
    }
}
