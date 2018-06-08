package org.streampipes.connect.firstconnector.guess;

import com.google.gson.annotations.SerializedName;

public class PropertyGuesses {

    @SerializedName("class")
    private String clazz;

    private double probability;


    public PropertyGuesses() {
    }

    public PropertyGuesses(String clazz, double probability) {
        this.clazz = clazz;
        this.probability = probability;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
