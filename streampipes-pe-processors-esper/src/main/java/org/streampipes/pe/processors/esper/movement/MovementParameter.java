package org.streampipes.pe.processors.esper.movement;

import java.util.List;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MovementParameter extends EventProcessorBindingParams {

	private final String positionCrs; // coordinate reference system id e.g. EPSG:4326 for lat,long

	private final String timestampProperty;
	private final String xProperty;
	private final String yProperty;

	private final long maxInterval;
	
	private final List<String> partitionProperties;

	public MovementParameter(DataProcessorInvocation graph,
		List<String> partitionProperties, String positionCrs, String timestampProperty, String xProperty, String yProperty,
		long maxInterval) {
		super(graph);
		this.positionCrs = positionCrs;
		this.partitionProperties = partitionProperties;
		this.timestampProperty = timestampProperty;
		this.xProperty = xProperty;
		this.yProperty = yProperty;
		this.maxInterval = maxInterval;
	}

	
	public String getPositionCRS() {
		return positionCrs;
	}

	public String getTimestampProperty() {
		return timestampProperty;
	}

	public String getXProperty() {
		return xProperty;
	}

	public String getYProperty() {
		return yProperty;
	}

	public long getMaxInterval() {
		return maxInterval;
	}


	public List<String> getPartitionProperties() {
		return partitionProperties;
	}
	
	
}
