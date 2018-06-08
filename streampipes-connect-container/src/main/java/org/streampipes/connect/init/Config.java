package org.streampipes.connect.init;

public class Config {

    public static String CONNECTOR_CONTAINER_ID = "MAIN_CONTAINER";

    public static int PORT = 8099;
    public static String HOST = "localhost";

    public static String getBaseUrl() {
        return "http://" + HOST + ":" + PORT + "/";
    }

}
