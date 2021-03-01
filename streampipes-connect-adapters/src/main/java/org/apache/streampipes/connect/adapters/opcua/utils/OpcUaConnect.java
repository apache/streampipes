package org.apache.streampipes.connect.adapters.opcua.utils;

public class OpcUaConnect {

    public static String formatServerAddress(String serverAddress) {

        if (!serverAddress.startsWith("opc.tcp://")) {
            serverAddress = "opc.tcp://" + serverAddress;
        }

        return serverAddress;
    }

    public enum OpcUaLabels {
        OPC_HOST_OR_URL,
        OPC_URL,
        OPC_HOST,
        OPC_SERVER_URL,
        OPC_SERVER_HOST,
        OPC_SERVER_PORT,
        NAMESPACE_INDEX,
        NODE_ID,
        ACCESS_MODE,
        USERNAME_GROUP,
        USERNAME,
        PASSWORD,
        UNAUTHENTICATED,
        AVAILABLE_NODES;
    }
}
