package org.apache.streampipes.connect.protocol.stream;

import org.apache.streampipes.model.graph.DataSinkInvocation;

public class KafkaConfig {

    private String kafkaHost;
    private Integer kafkaPort;
    private String topic;
    private String authentication;
    private String username;
    private String password;

    public KafkaConfig(String kafkaHost, Integer kafkaPort, String topic,
                       String authentication, String username, String password) {
        this.kafkaHost = kafkaHost;
        this.kafkaPort = kafkaPort;
        this.topic = topic;
        this.authentication = authentication;
        this.username = username;
        this.password = password;
    }

    public String getKafkaHost() {
        return kafkaHost;
    }

    public Integer getKafkaPort() {
        return kafkaPort;
    }

    public String getTopic() {
        return topic;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getAuthentication() { return authentication; }

}
