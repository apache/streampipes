package org.streampipes.pe.mixed.flink.samples.file;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.fs.DateTimeBucketer;
import org.apache.flink.streaming.connectors.fs.RollingSink;
import org.streampipes.wrapper.flink.FlinkDataSinkRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.io.Serializable;
import java.util.Map;

public class FileSinkProgram extends FlinkDataSinkRuntime<FileSinkParameters> implements Serializable {


    public FileSinkProgram(FileSinkParameters params) {
        super(params);
    }

    public FileSinkProgram(FileSinkParameters params, FlinkDeploymentConfig config) {
        super(params, config);
    }

    @Override
    public void getSink(DataStream<Map<String, Object>>... convertedStream) {
        RollingSink sink = new RollingSink<String>("./");
        sink.setBucketer(new DateTimeBucketer("yyyy-MM-dd--HHmm"));
        //sink.setWriter(new CsvWriter());
        sink.setBatchSize(1024 * 1024 * 400); // this is 400 MB,
        convertedStream[0].addSink(sink);
    }
}
