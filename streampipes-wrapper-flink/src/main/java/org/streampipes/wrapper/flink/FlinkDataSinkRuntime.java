package org.streampipes.wrapper.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

import java.util.Map;


public abstract class FlinkDataSinkRuntime<B extends EventSinkBindingParams> extends FlinkRuntime<B, DataSinkInvocation>{

	private static final long serialVersionUID = 1L;

	public FlinkDataSinkRuntime(B params)
	{
		super(params);
	}

	public FlinkDataSinkRuntime(B params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}

	@Override
	public void appendExecutionConfig(DataStream<Map<String, Object>>... convertedStream) {
		getSink(convertedStream);

	}

	public abstract void getSink(DataStream<Map<String, Object>>... convertedStream1);

}
