package de.fzi.cep.sepa.commons;

import java.io.OutputStream;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;

public class Configuration {

	public static final String SERVER_URL = "http://localhost";
	
	public static final String TCP_SERVER_URL = "tcp://localhost";
	
	public static final int ACTION_PORT = 8091;
	
	public static final int ESPER_PORT = 8090;
	
	public static final int SOURCES_PORT = 8089;
	
	public static final int WEBAPP_PORT = 8080;
	
	public static final String ESPER_BASE_URL = SERVER_URL + ":" + ESPER_PORT;
	
	public static final String ACTION_BASE_URL = SERVER_URL + ":" + ACTION_PORT;
	
	public static final String SOURCES_BASE_URL = SERVER_URL + ":" + SOURCES_PORT;
	
	public static final String WEBAPP_BASE_URL = SERVER_URL + ":" + WEBAPP_PORT;
	
	public static final String SESAME_URI = "http://localhost:8080/openrdf-sesame";
	
	public static final String SESAME_REPOSITORY_ID = "test-6";
	
	public static final String CONTEXT_PATH = "/semantic-epa-backend";
	
	//public static final String EMPIRE_CONFIG_LOCATION = "/home/riemer/empire.config.properties";

	public static final String EMPIRE_CONFIG_LOCATION = "c:\\workspace\\semantic-epa-parent\\semantic-epa-backend\\semantic-epa-storage\\src\\main\\resources\\empire.config.properties";

	//public static final String EMPIRE_CONFIG_LOCATION = "/home/robin/FZI/CEP/semantic-epa-parent/semantic-epa-backend/semantic-epa-storage/src/main/resources/empire.config.properties";

	
	//public static final String EMPIRE_CONFIG_LOCATION = "e:\\Workspace Eclipse Luna\\semantic-epa-parent\\semantic-epa-backend\\semantic-epa-storage\\src\\main\\resources\\empire.config.properties";

	public static final RDFFormat RDF_FORMAT = RDFFormat.JSONLD;
	
	public static RDFWriter getRioWriter(OutputStream stream) throws RDFHandlerException
	{
		RDFWriter writer = Rio.createWriter(RDF_FORMAT, stream);
		
		writer.handleNamespace("sepa", "http://sepa.event-processing.org/sepa#");
		writer.handleNamespace("empire", "urn:clarkparsia.com:empire:");
		writer.handleNamespace("fzi", "urn:fzi.de:sepa:");
		
		writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
		writer.getWriterConfig().set(JSONLDSettings.OPTIMIZE, true);
		
		return writer;
	}
}
