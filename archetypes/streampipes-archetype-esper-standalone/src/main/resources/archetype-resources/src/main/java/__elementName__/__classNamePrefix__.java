package ${package}.${elementName};

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import ${package}.esper.EsperEventEngine;


public class ${classNamePrefix} extends EsperEventEngine<${classNamePrefix}Parameters>{
	
	private static final Logger logger = LoggerFactory.getLogger(${classNamePrefix}.class.getSimpleName());

	protected List<String> statements(final ${classNamePrefix}Parameters params) {
		
		List<String> statements = new ArrayList<String>();
		
		return statements;
		
	}
}

