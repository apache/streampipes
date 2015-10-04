package de.fzi.cep.sepa.sources.samples.csv;

import java.util.Map;

public interface LineParser {

	public Map<String, Object> parseLine(String[] line);
}
