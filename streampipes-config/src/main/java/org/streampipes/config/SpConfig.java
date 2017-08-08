package org.streampipes.config;

import org.streampipes.config.consul.ConsulSpConfig;


public abstract class SpConfig {


    public SpConfig(String name) {

    }

    public SpConfig(String serviceName, SpConfigChangeCallback callback) {

    }

    public static SpConfig getSpConfig(String serviceName) {
        return new ConsulSpConfig(serviceName);
    }

    public static SpConfig getSpConfig(String serviceName, SpConfigChangeCallback callback) {
        return new ConsulSpConfig(serviceName, callback);
    }

    public abstract void register(String key, boolean defaultValue, String description);

    public abstract void register(String key, int defaultValue, String description);

    public abstract void register(String key, double defaultValue, String description);

    public abstract void register(String key, String defaultValue, String description);

    public abstract boolean getBoolean(String key);

    public abstract int getInteger(String key);

    public abstract double getDouble(String key);

    public abstract String getString(String key);

    public abstract void setBoolean(String key, Boolean value);

    public abstract void setInteger(String key, int value);

    public abstract void setDouble(String key, double value);

    public abstract void setString(String key, String value);

}
