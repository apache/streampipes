package org.apache.streampipes.messaging.kafka.security;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;

import java.util.Properties;

public class KafkaSecurityUnauthenticatedSSLConfig extends KafkaSecurityConfig {

    @Override
    public void appendConfig(Properties props) {
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.toString());
    }
}
