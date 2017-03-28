package de.fzi.cep.sepa.flink.samples.file;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSecRuntime;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.connectors.fs.DateTimeBucketer;
import org.apache.flink.streaming.connectors.fs.RollingSink;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by riemer on 13.11.2016.
 */
public class FileSinkProgram extends FlinkSecRuntime implements Serializable {

    public FileSinkProgram(SecInvocation graph, FlinkDeploymentConfig config) {
        super(graph, config);
    }

    public FileSinkProgram(SecInvocation graph) {
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
