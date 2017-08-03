package org.streampipes.commons.config;

import org.streampipes.commons.config.consul.ConsulSpConfig;

import java.util.Map;

public abstract class SpConfig {


    public SpConfig(String name) {

    }

    // TODO need an idea
//    public abstract void onConfigChange();
    public static SpConfig getSpConfig(String serviceName) {
        return new ConsulSpConfig(serviceName);
    }

    public abstract void register(String key, boolean defaultValue, String description);

    public abstract void register(String key, int defaultValue, String description);

    public abstract void register(String key, double defaultValue, String description);

    public abstract void register(String key, String defaultValue, String description);

    public abstract boolean getBoolean(String key);

    public abstract int getInteger(String key);

    public abstract double getDouble(String key);

    public abstract String getString(String key);

}
