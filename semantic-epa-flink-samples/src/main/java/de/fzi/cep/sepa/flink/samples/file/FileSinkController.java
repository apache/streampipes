package de.fzi.cep.sepa.flink.samples.file;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkConsumerDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSecRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 13.11.2016.
 */
public class FileSinkController extends AbstractFlinkConsumerDeclarer {


    @Override
    public SecDescription declareModel() {
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        EventSchema schema1 = new EventSchema();
        schema1.setEventProperties(eventProperties);

        EventStream stream1 = new EventStream();
        stream1.setEventSchema(schema1);

        SecDescription desc = new SecDescription("file", "HDFS File Sink", "Writes data to an HDFS file system.");
        desc.setIconUrl(Config.iconBaseUrl + "/file_icon.png");

        desc.addEventStream(stream1);

        List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();

        staticProperties.add(new FreeTextStaticProperty("file-name", "File Name", ""));

        desc.setStaticProperties(staticProperties);
        desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

        return desc;
    }

    @Override
    public boolean isVisualizable() {
        return false;
    }

    @Override
    public String getHtml(SecInvocation graph) {
        return null;
    }

    @Override
    protected FlinkSecRuntime getRuntime(SecInvocation graph) {
        return new FileSinkProgram(graph, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
    }
}
