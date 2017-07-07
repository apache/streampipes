package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.commons.config.Configuration;

/**
 * Created by riemer on 03.10.2016.
 */
public class KafkaSettings extends Settings {

    private static final String KAFKA_OUT_TOPIC_PREFIX = "eu.proasense.internal.sp.kpi.";

    private String kafkaHost;
    private String kafkaPort;
    private String topic;

    public KafkaSettings(String kafkaHost, String kafkaPort, String topic) {
        this.kafkaHost = kafkaHost;
        this.kafkaPort = kafkaPort;
        this.topic = topic;
    }

    public KafkaSettings() {

    }

    public String getKafkaHost() {
        return kafkaHost;
    }

    public void setKafkaHost(String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }

    public String getKafkaPort() {
        return kafkaPort;
    }

    public void setKafkaPort(String kafkaPort) {
        this.kafkaPort = kafkaPort;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public static KafkaSettings makeSettings(String kpiId) {
        KafkaSettings settings = new KafkaSettings();
        settings.setKafkaHost(Configuration.getInstance().getBrokerConfig().getKafkaHost());
        settings.setKafkaPort(String.valueOf(Configuration.getInstance().getBrokerConfig().getKafkaPort()));
        settings.setTopic(KAFKA_OUT_TOPIC_PREFIX +kpiId);
        return settings;
    }
}
