package de.fzi.cep.sepa.flink;

import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;

import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

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
	public boolean execute(DataStream<Map<String, Object>> convertedStream) {
		DataStreamSink<Map<String, Object>> sink = getSink(convertedStream);
				
		thread = new Thread(this);
		thread.start();
				
		return true;
	}

	public abstract DataStreamSink<Map<String, Object>> getSink(DataStream<Map<String, Object>>convertedStream);
	
}
