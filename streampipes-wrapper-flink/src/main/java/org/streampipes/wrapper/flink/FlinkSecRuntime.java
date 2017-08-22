package org.streampipes.wrapper.flink;

import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import org.streampipes.model.impl.graph.SecInvocation;

public abstract class FlinkSecRuntime extends FlinkRuntime<SecInvocation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FlinkSecRuntime(SecInvocation graph) {
		super(graph);
	}
	
	public FlinkSecRuntime(SecInvocation graph, FlinkDeploymentConfig config) {
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
