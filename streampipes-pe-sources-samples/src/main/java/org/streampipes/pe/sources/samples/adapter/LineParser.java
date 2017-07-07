package org.streampipes.pe.sources.samples.adapter;

import java.util.Map;

public interface LineParser {

	Map<String, Object> parseLine(String[] line);
}
