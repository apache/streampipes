package de.fzi.cep.sepa.sources.kd2.config;

/**
 * Created by riemer on 18.11.2016.
 */
public enum Kd2Variables {

    Biodata("biodata", "Biodata", ""),
    EmotionalArousal("arousal", "Emotional Arousal", ""),
    HeartRate("heartrate", "Heart Rate", "Current heart rate in beats per minute"),
    Pulse("pulse", "Pulse", "Current Pulse value iin volts"),
    SkinConductance("skinconductance", "Skin Conductance", "Current amplitude of the skin conductance response in microSiemens");

    String id;
    String eventName;
    String description;


    private final String topicPrefix = "kd2.biodata.";

    Kd2Variables(String id, String eventName, String description)
    {
        this.id = id;
        this.eventName = eventName;
        this.description = description;
    }

    public String id()
    {
        return id;
    }

    public String eventName()
    {
        return eventName;
    }

    public String description()
    {
        return description;
    }

    public String topic()
    {
        return topicPrefix + id;
    }
}
