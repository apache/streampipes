package de.fzi.cep.sepa.flink.samples.batchstream;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by philippzehnder on 16.02.17.
 */
public class FirstBatchThenStreamProgram extends FlinkSepaRuntime<FirstBatchThenStreamParameters> implements Serializable {

    public FirstBatchThenStreamProgram(FirstBatchThenStreamParameters params) {
        super(params);
    }

    public FirstBatchThenStreamProgram(FirstBatchThenStreamParameters params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}

    @Override
    protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
        DataStream<Map<String, Object>> batch = messageStream[0];
        DataStream<Map<String, Object>> stream = messageStream[1];

        return null;
    }

}
