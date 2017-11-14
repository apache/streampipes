package org.streampipes.pe.mixed.flink.samples.file;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSecRuntime;
import org.streampipes.model.graph.DataSinkInvocation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.fs.DateTimeBucketer;
import org.apache.flink.streaming.connectors.fs.RollingSink;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by riemer on 13.11.2016.
 */
public class FileSinkProgram extends FlinkSecRuntime implements Serializable {

    public FileSinkProgram(DataSinkInvocation graph, FlinkDeploymentConfig config) {
        super(graph, config);
    }

    public FileSinkProgram(DataSinkInvocation graph) {
        super(graph);
    }

    @Override
    public void getSink(DataStream<Map<String, Object>>... convertedStream) {
        RollingSink sink = new RollingSink<String>("./");
        sink.setBucketer(new DateTimeBucketer("yyyy-MM-dd--HHmm"));
        sink.setWriter(new CsvWriter());
        sink.setBatchSize(1024 * 1024 * 400); // this is 400 MB,
        convertedStream[0].addSink(sink);
    }
}
