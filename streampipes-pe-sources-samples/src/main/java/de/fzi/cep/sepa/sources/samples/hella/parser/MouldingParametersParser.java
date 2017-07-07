package de.fzi.cep.sepa.sources.samples.hella.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.sources.samples.hella.HellaVariables;

public class MouldingParametersParser extends HellaParser {

	private static final List<String> columnNames = Arrays.asList("variable_type",
			"variable_timestamp",
			"machineId",
			"movementDifferential",
			"meltCushion",
			"dosingTime",
			"injectionTime",
			"cycleTime",
			"cavityPressure"
	);
			
	@Override
	public Map<String, Object> parseLine(String[] line) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(columnNames.get(0), HellaVariables.IMM.tagNumber());
		result.put(columnNames.get(1), toTimestamp(line[0], line[1]));
		result.put(columnNames.get(2), "m1");
		result.put(columnNames.get(3), Double.parseDouble(line[6]));
		result.put(columnNames.get(4), Double.parseDouble(line[5]));
		result.put(columnNames.get(5), Double.parseDouble(line[4]));
		result.put(columnNames.get(6), Double.parseDouble(line[3]));
		result.put(columnNames.get(7), Double.parseDouble(line[2]));
		result.put(columnNames.get(8), Double.parseDouble(line[8]));
		
		return result;
		
	}

}
