package org.streampipes.wrapper.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

import java.util.Map;


public abstract class FlinkDataSinkRuntime<B extends EventSinkBindingParams> extends FlinkRuntime<DataSinkInvocation>{

	private static final long serialVersionUID = 1L;

	protected B params;

	public FlinkDataSinkRuntime(B params)
	{
		super(params.getGraph());
		this.params = params;
	}

	public FlinkDataSinkRuntime(B params, FlinkDeploymentConfig config)
	{
		super(params.getGraph(), config);
		this.params = params;
	}

	@Override
	public boolean execute(DataStream<Map<String, Object>>... convertedStream) {
		getSink(convertedStream);
				
		thread = new Thread(this);
		thread.start();
				
		return true;
	}

	public abstract void getSink(DataStream<Map<String, Object>>... convertedStream1);

}
