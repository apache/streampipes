package de.fzi.cep.sepa.esper.config;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.esper.debs.c1.DebsOutputParameters;
import de.fzi.cep.sepa.esper.debs.c1.OutputType;
import de.fzi.cep.sepa.esper.writer.Challenge1FileWriter;
import de.fzi.cep.sepa.esper.writer.SEPAWriter;
import de.fzi.cep.sepa.esper.writer.TestDrillingWriter;
import de.fzi.cep.sepa.esper.writer.Writer;
import de.fzi.cep.sepa.runtime.OutputCollector;

public class EsperConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = Configuration.getInstance().ESPER_BASE_URL;
		iconBaseUrl = Configuration.getInstance().getHostname() +"8080" +"/semantic-epa-backend/img";
	}
	
	public static <T> Writer getDefaultWriter(OutputCollector collector, T params)
	{
		return new SEPAWriter(collector);
//		return new TestDrillingWriter();
		//return new Challenge1FileWriter(new DebsOutputParameters("c:\\users\\riemer\\desktop\\debs22"), OutputType.PERSIST);
	}
}
