package de.fzi.cep.sepa.sources.samples.hella.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.sources.samples.hella.HellaVariables;

public class ScrapLineParser extends HellaParser {

	private static final List<String> columnNames = Arrays.asList("variable_type",
			"variable_timestamp",
			"machineId",
			"type",
			"scrap",
			"reasonText",
			"desgination",
			"finalArticle"
	);
	
	@Override
	public Map<String, Object> parseLine(String[] line) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(columnNames.get(0), HellaVariables.Scrap.tagNumber());
		result.put(columnNames.get(1), toTimestamp(line[0], line[1]));
		result.put(columnNames.get(2), line[2]);
		result.put(columnNames.get(3), "");
		result.put(columnNames.get(4), line[3]);
		result.put(columnNames.get(5), line[4]);
		result.put(columnNames.get(6), "");
		result.put(columnNames.get(7), line[5]);
		
		return result;
		
	}
	
}
