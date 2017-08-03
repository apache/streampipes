package org.streampipes.wrapper.esper.config;

import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.wrapper.esper.writer.SEPAWriter;
import org.streampipes.wrapper.esper.writer.Writer;
import org.streampipes.wrapper.OutputCollector;

public class EsperConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = ClientConfiguration.INSTANCE.getEsperUrl();
		iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";
	}

	public static final String getIconUrl(String pictureName) {
		return iconBaseUrl +"/" +pictureName +".png";
	}
	
	public static <T> Writer getDefaultWriter(OutputCollector collector, T params)
	{
		return new SEPAWriter(collector);
//		return new TestDrillingWriter();
		//return new Challenge1FileWriter(new DebsOutputParameters("c:\\users\\riemer\\desktop\\debs22"), OutputType.PERSIST);
	}
}
