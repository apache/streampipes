package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.commons.config.Configuration;

/**
 * Created by riemer on 11.10.2016.
 */
public class KpiPublisherSettings extends Settings {

    private static final String KAFKA_OUT_TOPIC_PREFIX = "eu.proasense.internal.sp.kpi.";

    private String kafkaHost;
    private String kafkaPort;
    private String topic;
    private String kpiId;

    public KpiPublisherSettings(String kafkaHost, String kafkaPort, String topic, String kpiId) {
        this.kafkaHost = kafkaHost;
        this.kafkaPort = kafkaPort;
        this.topic = topic;
        this.kpiId = kpiId;
    }

    public KpiPublisherSettings() {
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

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public static KpiPublisherSettings makeSettings(String kpiId) {
        KpiPublisherSettings settings = new KpiPublisherSettings();
        settings.setKafkaHost(Configuration.getInstance().getBrokerConfig().getKafkaHost());
        settings.setKafkaPort(String.valueOf(Configuration.getInstance().getBrokerConfig().getKafkaPort()));
        settings.setKpiId(kpiId);
        settings.setTopic(KAFKA_OUT_TOPIC_PREFIX +kpiId);
        return settings;
    }
}
