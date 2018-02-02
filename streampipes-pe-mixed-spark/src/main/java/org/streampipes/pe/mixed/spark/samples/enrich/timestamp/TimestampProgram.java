package org.streampipes.pe.mixed.spark.samples.enrich.timestamp;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.streampipes.wrapper.spark.SparkDataProcessorRuntime;
import org.streampipes.wrapper.spark.SparkDeploymentConfig;

import java.util.Map;

/**
 * Created by Jochen Lutz on 2018-01-22.
 */
public class TimestampProgram extends SparkDataProcessorRuntime<TimestampParameters>  {
    private static final long serialVersionUID = 1L;

    public TimestampProgram(TimestampParameters params, SparkDeploymentConfig sparkDeploymentConfig) {
        super(params, sparkDeploymentConfig);
    }

    protected JavaDStream<Map<String, Object>> getApplicationLogic(JavaDStream<Map<String, Object>>... dStreams) {
        return dStreams[0].flatMap(new TimestampEnricher());
    }
}
