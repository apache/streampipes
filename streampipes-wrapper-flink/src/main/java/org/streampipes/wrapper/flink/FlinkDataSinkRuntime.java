package org.streampipes.wrapper.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.model.graph.DataSinkInvocation;

import java.util.Map;


public abstract class FlinkDataSinkRuntime extends FlinkRuntime<DataSinkInvocation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FlinkDataSinkRuntime(DataSinkInvocation graph) {
		super(graph);
	}
	
	public FlinkDataSinkRuntime(DataSinkInvocation graph, FlinkDeploymentConfig config) {
		super(graph, config);
	}

	@Override
	public boolean execute(DataStream<Map<String, Object>>... convertedStream) {
		getSink(convertedStream);
				
		thread = new Thread(this);
		thread.start();
				
		return true;
	}

//	public abstract DataStreamSink<Map<String, Object>> getSink(DataStream<Map<String, Object>>... convertedStream1);
	public abstract void getSink(DataStream<Map<String, Object>>... convertedStream1);

}
