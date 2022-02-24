package org.apache.streampipes.messaging.kafka.config;

import java.util.Properties;

public interface KafkaConfigAppender {

    void appendConfig(Properties props);
}
