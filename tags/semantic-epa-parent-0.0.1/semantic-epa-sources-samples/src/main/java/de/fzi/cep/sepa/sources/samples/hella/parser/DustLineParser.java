package de.fzi.cep.sepa.sources.samples.hella.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.sources.samples.csv.LineParser;
import de.fzi.cep.sepa.sources.samples.hella.HellaVariables;

public class DustLineParser implements LineParser {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final List<String> columnNames = Arrays.asList(
			"variable_type",
			"variable_timestamp",
			"id",
			"bin0",
			"bin1",
			"bin2",
			"bin3",
			"bin4",
			"bin5",
			"bin6",
			"bin7",
			"bin8",
			"bin9",
			"bin10",
			"bin11",
			"bin11",
			"bin12",
			"bin13",
			"bin14",
			"bin15"
			
	);
	
	@Override
	public Map<String, Object> parseLine(String[] line) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(columnNames.get(0), HellaVariables.Dust.tagNumber());
		result.put(columnNames.get(1), toTimestamp(line[0]));
		result.put(columnNames.get(2), line[1]);
		
		for(int i = 0; i <= 15; i++) {
			result.put(columnNames.get(i+3), Integer.parseInt(line[i+2]));
		}
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
