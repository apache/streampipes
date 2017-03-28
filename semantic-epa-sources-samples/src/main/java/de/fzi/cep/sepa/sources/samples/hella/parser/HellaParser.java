package de.fzi.cep.sepa.sources.samples.hella.parser;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.fzi.cep.sepa.sources.samples.adapter.LineParser;

public abstract class HellaParser implements LineParser {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy H:mm:ss");
	
	protected long toTimestamp(String date, String time)
	{
		try {
			return sdf.parse(date + " " +time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
