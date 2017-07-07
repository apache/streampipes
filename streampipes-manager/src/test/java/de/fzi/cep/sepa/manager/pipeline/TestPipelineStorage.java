package de.fzi.cep.sepa.manager.pipeline;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.client.util.Utils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by riemer on 30.09.2016.
 */
public class TestPipelineStorage {

    public static void main(String[] args) {
        URL url = Resources.getResource("pipeline.json");
        try {
            String pipelineString = Resources.toString(url, Charsets.UTF_8);
            Pipeline pipeline = Utils.getGson().fromJson(pipelineString, Pipeline.class);
            Operations.storePipeline(pipeline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
