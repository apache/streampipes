package org.streampipes.pe.mixed.flink.samples.file;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.flink.FlinkDataSinkDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataSinkRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 13.11.2016.
 */
public class FileSinkController extends FlinkDataSinkDeclarer {


    @Override
    public DataSinkDescription declareModel() {
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        EventSchema schema1 = new EventSchema();
        schema1.setEventProperties(eventProperties);

        SpDataStream stream1 = new SpDataStream();
        stream1.setEventSchema(schema1);

        DataSinkDescription desc = new DataSinkDescription("file", "HDFS File Sink", "Writes data to an HDFS file system.");
        desc.setIconUrl(FlinkConfig.getIconUrl("hadoop-icon"));

        desc.addEventStream(stream1);

        List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();

        staticProperties.add(new FreeTextStaticProperty("file-name", "File Name", ""));

        desc.setStaticProperties(staticProperties);
        desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

        return desc;
    }

    @Override
    protected FlinkDataSinkRuntime getRuntime(DataSinkInvocation graph) {
        return new FileSinkProgram(graph, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
    }
}
