package de.fzi.cep.sepa.storm.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.lightcouch.Response;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.impl.SepaInvocationStorageImpl;

public class Utils {

	public static String NIMBUS_HOST = "ipe-koi05.fzi.de";
	public static int NIMBUS_THRIFT_PORT = 6627;
	public static final String SEPA_DATA_STREAM = "SEPA_DATA_STREAM";




	public static String getZookeeperUrl(EventStream eventStream) {
		KafkaTransportProtocol tp = (KafkaTransportProtocol) eventStream.getEventGrounding().getTransportProtocol();
		return tp.getZookeeperHost() + ":" + tp.getZookeeperPort();

	}

	
	public static EventProperty getEventPropertyById(URI id, EventStream eventStream) {
		
		for (EventProperty p : eventStream.getEventSchema().getEventProperties()) {
			if (p.getElementName() != null && p.getElementName().equals(id.toString())) {
				return p;
			}
		}
		
		return null;
	}

	public static SepaInvocation getSepaInvocation(String id) {
		SepaInvocationStorageImpl invocationStorage = new SepaInvocationStorageImpl();
		return invocationStorage.getSepaInvovation(id);

	}
	
	public static Response storeSepaInvocation(SepaInvocation invocation) {
		SepaInvocationStorageImpl impl = new SepaInvocationStorageImpl();
		return impl.storeSepaInvocation(invocation);

	}
	
	public static boolean removeSepaInvocation(String id, String rev) {
		SepaInvocationStorageImpl impl = new SepaInvocationStorageImpl();
		return impl.removeSepaInvovation(id, rev);
		
	}
	
	public static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
            
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
}
