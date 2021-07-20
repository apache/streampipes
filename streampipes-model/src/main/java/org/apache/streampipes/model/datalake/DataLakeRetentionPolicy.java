package org.apache.streampipes.model.datalake;

public class DataLakeRetentionPolicy {

    private String name;
    private String durationLiteral;
    private boolean isDefault;

    public DataLakeRetentionPolicy(String name, int duration, String durationUnit, boolean isDefault) {
        this.name = name;
        this.durationLiteral = duration + durationUnit;
        this.isDefault = isDefault;
    }

    public DataLakeRetentionPolicy(String name) {
        this.name = name;
        this.isDefault = false;
        setDurationLiteralToInfinity();
    }

    public DataLakeRetentionPolicy(String name, String durationLiteral, boolean isDefault) {
        this.name = name;
        this.durationLiteral = durationLiteral;
        this.isDefault = isDefault;
    }

    public String getDurationLiteral() {
        return durationLiteral;
    }

    public void setDurationLiteral(int duration, String durationUnit) {
        this.durationLiteral = duration + durationUnit;
    }

    public void setDurationLiteralToInfinity() {
        this.durationLiteral = "INF";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "DataLakeRetentionPolicy{" +
                "name='" + name + '\'' +
                ", durationLiteral='" + durationLiteral + '\'' +
                '}';
    }
}
