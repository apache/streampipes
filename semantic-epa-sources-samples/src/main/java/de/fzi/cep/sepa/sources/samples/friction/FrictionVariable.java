package de.fzi.cep.sepa.sources.samples.friction;

/**
 * Created by riemer on 26.10.2016.
 */
public enum FrictionVariable {

    Gearbox("Friction Coefficient Gearbox (Replay)", "", "gearbox", "de.fzi.cep.sepa.mhwirth.friction.gearbox"),
    Swivel("Friction Coefficient Swivel (Replay)", "", "swivel", "de.fzi.cep.sepa.mhwirth.friction.swivel");

    private String label;
    private String description;
    private String path;
    private String topic;

    FrictionVariable(String label, String description, String path, String topic) {
        this.label = label;
        this.description = description;
        this.path = path;
        this.topic = topic;
    }

    public String label() {
        return label;
    }

    public String description() {
        return description;
    }

    public String path() {
        return path;
    }

    public String topic() {
        return topic;
    }

}
