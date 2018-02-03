package org.streampipes.config.model;

public class ConfigItem {

    private String key;
    private String description;
    private String value;
    private String valueType;
    private boolean isPassword;

    public ConfigItem() {
        setPassword(false);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public boolean isPassword() {
        return isPassword;
    }

    public void setPassword(boolean state) {
        isPassword = state;
    }
}
