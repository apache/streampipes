package de.fzi.proasense.bleconnector;

/**
 * Created by eberle on 17.02.2016.
 */
public class ConnectionInfo {

    private String type = "";
    private String address = "";
    private long timestamp;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
