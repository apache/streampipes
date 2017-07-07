//package de.fzi.cep.sepa.sources.samples.proveit;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import org.apache.http.client.ClientProtocolException;
//
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//
//import EventStreamDeclarer;
//import de.fzi.cep.sepa.sources.samples.util.Utils;
//import de.fzi.proveit.senslet.model.Senslet;
//
//public class ProveITStreamGenerator {
//
//	public static final String PROVEIT_URL = "http://kalmar29.fzi.de:8080/ProveIT/api/senslets/templates";
//	
//	public List<EventStreamDeclarer> generateStreams() {
//		List<EventStreamDeclarer> streams = new ArrayList<>();
//		
//		List<Senslet> senslets = retrieveSenslets();
//		int i = 0;
//		for(Senslet senslet : senslets)
//		{ i++;
//			if (i <= 9) streams.add(new ProveITEventStream(senslet));
//		}
//		
//		return streams;
//	}
//	
//	private List<Senslet> retrieveSenslets()
//	{
//		List<Senslet> senslets = new ArrayList<>();
//		try {
//			senslets = de.fzi.proveit.senslet.util.Utils.getGson().fromJson(Utils.fetchJson(PROVEIT_URL), new TypeToken<List<Senslet>>(){}.getType());
//			System.out.println(senslets.size());
//		} catch (JsonSyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return senslets;
//		
//	}
//	
//	public static void main(String[] args)
//	{
//		new ProveITStreamGenerator().retrieveSenslets();
//	}
//
//}
