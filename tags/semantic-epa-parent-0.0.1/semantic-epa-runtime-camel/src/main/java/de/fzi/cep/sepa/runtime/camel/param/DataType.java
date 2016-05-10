package de.fzi.cep.sepa.runtime.camel.param;

import java.util.Map;

import org.apache.camel.impl.GzipDataFormat;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.dataformat.SerializationDataFormat;
import org.apache.camel.model.dataformat.XStreamDataFormat;
import org.apache.camel.spi.DataFormat;

import de.fzi.cep.sepa.runtime.camel.util.ThriftDataFormatDefinition;

public enum DataType {
	JAVA(new SerializationDataFormat()), // only maps (String -> Object)
	XML(new XStreamDataFormat()), // XStream marshaled! only maps
	JSON(jsonToMapFormat()),
	GZIP_JAVA(new GzipDataFormat(), new SerializationDataFormat()),
	GZIP_XML(new GzipDataFormat(), new XStreamDataFormat()),
	GZIP_JSON(new GzipDataFormat(), jsonToMapFormat()),
	THRIFT(new ThriftDataFormatDefinition());
	// maybe encryption

	private final DataFormat additionalFormat;

	private final DataFormatDefinition serialization;

	private DataType(DataFormatDefinition serialization) {
		this.additionalFormat = null;
		this.serialization = serialization;
	}

	private DataType(DataFormat additional, DataFormatDefinition serialization) { // unmarshaling order!
		this.additionalFormat = additional;
		this.serialization = serialization;
	}

	private static DataFormatDefinition jsonToMapFormat() {
		JsonDataFormat format = new JsonDataFormat(JsonLibrary.Jackson);
		format.setUnmarshalType(Map.class); 
		return format;
	}

	public RouteDefinition marshal(RouteDefinition def) {
		def.marshal(serialization);
		if (additionalFormat != null)
			def.marshal(additionalFormat);
		return def;
	}

	public RouteDefinition unmarshal(RouteDefinition def) {
		if (additionalFormat != null)
			def.unmarshal(additionalFormat);
		def.unmarshal(serialization);
		return def;
	}
}
