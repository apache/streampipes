package org.streampipes.commons.config.consul;

import com.google.common.base.Optional;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import org.streampipes.commons.config.SpConfig;

public class ConsulSpConfig extends SpConfig {
    private static final String SERVICE_ROUTE_PREFIX = "sp/v1/";
    private String serviceName;
    KeyValueClient kvClient;

    public static void main(String[] args) {
         Consul consul = Consul.builder().build(); // connect to Consul on localhost
        KeyValueClient kvClient = consul.keyValueClient();;


    }

    public ConsulSpConfig(String serviceName) {
        super(serviceName);
        //TDOO use consul adress from an environment variable
//        Consul consul = Consul.builder().withUrl("http://localhost:8500").build(); // connect to Consul on localhost
        Consul consul = Consul.builder().build(); // connect to Consul on localhost
        kvClient = consul.keyValueClient();

        this.serviceName = serviceName;
    }

    @Override
    public void register(String key, boolean defaultValue, String description) {
        register(key, Boolean.toString(defaultValue), description);
    }

    @Override
    public void register(String key, int defaultValue, String description) {
        register(key, Integer.toString(defaultValue), description);
    }

    @Override
    public void register(String key, double defaultValue, String description) {
        register(key, Double.toString(defaultValue), description);

    }

    @Override
    public void register(String key, String defaultValue, String description) {
        Optional<String> i = kvClient.getValueAsString(key);
        if (!i.isPresent()) {
            kvClient.putValue(addSn(key), defaultValue);
            kvClient.putValue(addSn(key) + "_description", description);
        }
    }

    @Override
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    @Override
    public int getInteger(String key) {
        return Integer.parseInt(getString(key));
    }

    @Override
    public double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }

    @Override
    public String getString(String key) {
        return String.valueOf(kvClient.getValueAsString(addSn(key)).get());
    }

    private String addSn(String key) {
       return SERVICE_ROUTE_PREFIX + serviceName + "/" + key;
    }
}
