package de.fzi.cep.sepa.esper.movement;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class MovementParameter extends BindingParameters {

	private final String positionCrs; // coordinate reference system id e.g. EPSG:4326 for lat,long

	private final String timestampProperty;
	private final String xProperty;
	private final String yProperty;

	private final long maxInterval;

	public MovementParameter(String inName, String outName, String positionCrs, List<String> allProperties,
		List<String> partitionProperties, String timestampProperty, String xProperty, String yProperty,
		long maxInterval) {
		super(inName, outName, allProperties, partitionProperties);
		this.inName = inName;
		this.outName = outName;
		this.positionCrs = positionCrs;
		this.allProperties = allProperties;
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
}
