package de.fzi.cep.sepa.manager.kpi.context;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.List;

/**
 * Created by riemer on 03.10.2016.
 */
public class ContextModelFetcher {

    private String useCase;
    private List<String> requiredSensorIds;
    private ContextModel contextModel;

    public ContextModelFetcher(String useCase, List<String> requiredSensorIds) {
        this.useCase = useCase;
        this.requiredSensorIds = requiredSensorIds;
        this.contextModel = new ContextModel();
    }

    public ContextModel fetchContextModel() throws IOException {
        contextModel.setContextModelSensor(getSensorList());
        requiredSensorIds.forEach(sensorId -> {
            try {
                contextModel.addSensorDetails(sensorId, getSensorDetails(sensorId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return contextModel;
    }

    private ContextModelSensorDetails getSensorDetails(String sensorId) throws IOException {
        System.out.println(ContextModelEndpointBuilder.buildSensorDetailsUrl(useCase, sensorId));
        String sensorDetails = Request
                .Get(ContextModelEndpointBuilder.buildSensorDetailsUrl(useCase, sensorId))
                .execute()
                .returnContent()
                .asString();

        return new Gson().fromJson(sensorDetails, ContextModelSensorDetails.class);
    }

    private ContextModelSensor getSensorList() throws IOException {
        String sensorList = Request
                .Get(ContextModelEndpointBuilder.buildListUrl(useCase))
                .execute()
                .returnContent()
                .asString();

        return new Gson().fromJson(sensorList, ContextModelSensor.class);
    }
}
