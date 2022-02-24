package org.apache.streampipes.messaging.kafka.security;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;

import java.util.Properties;

public class KafkaSecuritySaslPlainConfig extends KafkaSecurityConfig {

    private final String username;
    private final String password;

    public KafkaSecuritySaslPlainConfig(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void appendConfig(Properties props) {

        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.toString());

        String SASL_JAAS_CONFIG = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username + "\" password=\"" + password + "\";";
        props.put(SaslConfigs.SASL_JAAS_CONFIG, SASL_JAAS_CONFIG);
    }
}
