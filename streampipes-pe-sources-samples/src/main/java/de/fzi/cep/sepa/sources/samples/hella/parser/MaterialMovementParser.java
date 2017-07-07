package de.fzi.cep.sepa.sources.samples.hella.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.sources.samples.adapter.LineParser;
import de.fzi.cep.sepa.sources.samples.hella.HellaVariables;

public class MaterialMovementParser implements LineParser {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final List<String> columnNames = Arrays.asList("variable_type",
			"variable_timestamp",
			"location",
			"event",
			"shuttle",
			"rightPiece",
			"leftPiece"
	);
	
	@Override
	public Map<String, Object> parseLine(String[] line) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(columnNames.get(0), HellaVariables.MontracMovement.tagNumber());
		result.put(columnNames.get(1), toTimestamp(line[0]));
		result.put(columnNames.get(2), line[1]);
		result.put(columnNames.get(3), line[2]);
		result.put(columnNames.get(4), Integer.parseInt(line[3]));
		result.put(columnNames.get(5), Boolean.parseBoolean(line[4]));
		result.put(columnNames.get(6), Boolean.parseBoolean(line[5]));

		return result;
	}
	

	
	private long toTimestamp(String date)
	{
		try {
			return sdf.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

}
