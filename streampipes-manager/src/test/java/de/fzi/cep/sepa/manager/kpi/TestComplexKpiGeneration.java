package de.fzi.cep.sepa.manager.kpi;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.fzi.cep.sepa.kpi.KpiRequest;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.util.Utils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by riemer on 06.10.2016.
 */
public class TestComplexKpiGeneration {

    public static void main(String[] args) throws IOException {
        URL url = Resources.getResource("complex-kpi-aggregated.json");
        String kpiString = Resources.toString(url, Charsets.UTF_8);
        System.out.println(kpiString);

        KpiRequest request = Utils.getGson().fromJson(kpiString, KpiRequest.class);

        Operations.createAndStartKpiFromPipeline(request);
    }
}
