package org.streampipes.wrapper.standalone.manager;

import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.wrapper.standalone.datatype.DatatypeDefinition;
import org.streampipes.wrapper.standalone.datatype.json.JsonDatatypeDefinition;

public class DatatypeManager {

	public static DatatypeDefinition findDatatypeDefinition(TransportFormat format) {
		
		if (format.getRdfType().stream().anyMatch(type -> type.toString().equals(MessageFormat.Thrift)))
			// TODO remove
			return new JsonDatatypeDefinition();
		else return new JsonDatatypeDefinition();
		 
	}
}
