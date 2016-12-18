package de.fzi.cep.sepa.manager.kpi.context;

import de.fzi.cep.sepa.commons.config.Configuration;

/**
 * Created by riemer on 03.10.2016.
 */
public class ContextModelEndpointBuilder {

    public static final String CONTEXT_MODEL_HELLA = "hella";
    public static final String CONTEXT_MODEL_MHWIRTH = "mhwirth";


    public static final String CONTEXT_MODEL_URL = "http://" + Configuration.getInstance().getHostname() +":8082/storage-registry/query/sensor/";
    //public static final String CONTEXT_MODEL_URL = "http://ipe-koi15.fzi.de" +":8082/storage-registry/query/sensor/";

    public static final String LIST_PATH = "list";
    public static final String DETAILS_PATH = "properties";

    public static final String DATASET_PARAM = "dataset=";
    public static final String SENSORID_PARAM = "sensorId=";


    public static String buildListUrl(String useCase) {
        return CONTEXT_MODEL_URL
                +LIST_PATH
                +"?"
                +DATASET_PARAM
                +useCase;
    }

    public static String buildSensorDetailsUrl(String useCase, String sensorId) {
        return CONTEXT_MODEL_URL
                +DETAILS_PATH
                +"?"
                +DATASET_PARAM
                +useCase
                +"&"
                +SENSORID_PARAM
                +sensorId;
    }
}
