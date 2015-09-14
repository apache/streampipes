package de.fzi.cep.sepa.esper.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.esper.writer.SEPAWriter;
import de.fzi.cep.sepa.esper.writer.Writer;
import de.fzi.cep.sepa.runtime.OutputCollector;

public class EsperConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = ClientConfiguration.INSTANCE.getEsperUrl();
		iconBaseUrl = ClientConfiguration.INSTANCE.getWebappUrl() +"/semantic-epa-backend/img";
	}
	
	public static <T> Writer getDefaultWriter(OutputCollector collector, T params)
	{
		return new SEPAWriter(collector);
//		return new TestDrillingWriter();
		//return new Challenge1FileWriter(new DebsOutputParameters("c:\\users\\riemer\\desktop\\debs22"), OutputType.PERSIST);
	}
}
