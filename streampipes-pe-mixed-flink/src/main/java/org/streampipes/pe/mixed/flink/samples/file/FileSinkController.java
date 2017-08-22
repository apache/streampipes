package org.streampipes.pe.mixed.flink.samples.file;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.flink.AbstractFlinkConsumerDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSecRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;

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
        desc.setIconUrl(FlinkConfig.getIconUrl("hadoop-icon"));

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
        return new FileSinkProgram(graph, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
    }
}
