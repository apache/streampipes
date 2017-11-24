package org.streampipes.config.model;

import java.util.List;
import java.util.Map;

public class PeConfig {

    String mainKey;
    String name;
    List<ConfigItem> configs;
    Map<String, String> meta;

    public List<ConfigItem> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ConfigItem> configs) {
        this.configs = configs;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String getMainKey() {
        return mainKey;
    }

    public void setMainKey(String mainKey) {
        this.mainKey = mainKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
