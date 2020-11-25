package org.apache.streampipes.connect.adapters.opcua;

import javax.annotation.Nullable;

public enum OpcUaNodeVariants {
    Property(68),
    EUInformation(887);

    // ID as specific in OPC UA standard
    private final int id;

    private OpcUaNodeVariants(int id){
        this.id = id;
    }

    public int getId() { return this.id;}

    @Nullable
    public static OpcUaNodeVariants from(int id){
        switch (id){
            case 68:
                return Property;
            case 887:
                return EUInformation;
            default:
                return null;
        }
    }
}
