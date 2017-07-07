package de.fzi.cep.sepa.sources.samples.adapter;

import java.util.Map;

public interface LineParser {

	Map<String, Object> parseLine(String[] line);
}
