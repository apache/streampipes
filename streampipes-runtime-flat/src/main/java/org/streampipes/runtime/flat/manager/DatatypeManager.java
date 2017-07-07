package org.streampipes.runtime.flat.manager;

import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.runtime.flat.datatype.DatatypeDefinition;
import org.streampipes.runtime.flat.datatype.json.JsonDatatypeDefinition;
import org.streampipes.runtime.flat.datatype.json.ThriftDatatypeDefinition;

public class DatatypeManager {

	public static DatatypeDefinition findDatatypeDefinition(TransportFormat format) {
		
		if (format.getRdfType().stream().anyMatch(type -> type.toString().equals(MessageFormat.Thrift))) 
			return new ThriftDatatypeDefinition();
		else return new JsonDatatypeDefinition();
		 
	}
}
