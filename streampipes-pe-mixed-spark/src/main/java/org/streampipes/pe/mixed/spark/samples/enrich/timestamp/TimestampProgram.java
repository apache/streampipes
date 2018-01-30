package org.streampipes.pe.mixed.spark.samples.enrich.timestamp;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.streampipes.wrapper.spark.SparkDataProcessorRuntime;
import org.streampipes.wrapper.spark.SparkDeploymentConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
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

    public static void main(String[] args) {
        String enc = args[0];
        //System.out.println("Received data: '" + enc + "'");

        byte[] data = Base64.getDecoder().decode(enc);

        TimestampParameters params = null;
        SparkDeploymentConfig sparkDeploymentConfig = null;
        InputStream fis = null;
        fis = new ByteArrayInputStream(data);
        ObjectInputStream o = null;
        try {
            o = new ObjectInputStream(fis);
            params = (TimestampParameters) o.readObject();
            sparkDeploymentConfig = (SparkDeploymentConfig) o.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (params != null) {
            TimestampProgram prog = new TimestampProgram(params, sparkDeploymentConfig);
            sparkDeploymentConfig.setRunLocal(true);
            prog.startExecution();
        }
    }
}
