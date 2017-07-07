package de.fzi.cep.sepa.runtime.flat.manager;

import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.datatype.json.JsonDatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.datatype.json.ThriftDatatypeDefinition;

public class DatatypeManager {

	public static DatatypeDefinition findDatatypeDefinition(TransportFormat format) {
		
		if (format.getRdfType().stream().anyMatch(type -> type.toString().equals(MessageFormat.Thrift))) 
			return new ThriftDatatypeDefinition();
		else return new JsonDatatypeDefinition();
		 
	}
}
