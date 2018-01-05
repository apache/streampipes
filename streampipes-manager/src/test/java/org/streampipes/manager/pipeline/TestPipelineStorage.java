package org.streampipes.manager.pipeline;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.serializers.json.Utils;

import java.io.IOException;
import java.net.URL;

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
