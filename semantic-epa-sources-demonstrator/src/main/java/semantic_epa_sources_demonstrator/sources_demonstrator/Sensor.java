package semantic_epa_sources_demonstrator.sources_demonstrator;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;


public abstract class Sensor {

	private String url;
	private String topic;
	
	public Sensor(String url, String topic) {
		this.url = url;
		this.topic = topic;
	}
	
	public abstract void start();

	
	public String requestData() {

		try {
			return Request.Get(url)
			.connectTimeout(1000)
			.execute().returnContent().asString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Error 404. Not Found";
	}
	
	public void sendToBroker(String s) {
		ProaSenseInternalProducer producer = new ProaSenseInternalProducer(Main.BROKER, topic);		
		
		producer.send(s.getBytes());
	}
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	
}
