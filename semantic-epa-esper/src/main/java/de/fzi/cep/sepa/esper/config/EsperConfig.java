package de.fzi.cep.sepa.esper.config;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.esper.SEPAWriter;
import de.fzi.cep.sepa.esper.Writer;
import de.fzi.cep.sepa.esper.debs.c1.Challenge1FileWriter;
import de.fzi.cep.sepa.esper.debs.c1.DebsOutputParameters;
import de.fzi.cep.sepa.esper.debs.c1.OutputType;
import de.fzi.cep.sepa.runtime.OutputCollector;

public class EsperConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = Configuration.ESPER_BASE_URL;
		iconBaseUrl = Configuration.WEBAPP_BASE_URL +"/semantic-epa-backend/img";
	}
	
	public static <T> Writer getDefaultWriter(OutputCollector collector, T params)
	{
		return new SEPAWriter(collector);
		//return new Challenge1FileWriter(new DebsOutputParameters("c:\\users\\riemer\\desktop"), OutputType.PRINT);
	}
}
