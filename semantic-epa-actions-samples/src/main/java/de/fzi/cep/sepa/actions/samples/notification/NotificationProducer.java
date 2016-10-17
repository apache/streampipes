package de.fzi.cep.sepa.actions.samples.notification;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationProducer implements EventListener<byte[]> {

	StreamPipesKafkaProducer producer;
	private TSerializer serializer;
	private String title;
	private String content;
    Pattern pattern;
	
	public NotificationProducer(SecInvocation sec)
	{
		producer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), "de.fzi.cep.sepa.notifications");
		this.title = SepaUtils.getFreeTextStaticPropertyValue(sec, "title");
		this.content = SepaUtils.getFreeTextStaticPropertyValue(sec, "content");
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
        this.pattern = Pattern.compile("#[^#]*#");
	}
	
	@Override
	public void onEvent(byte[] json) {
		RecommendationEvent event = new RecommendationEvent();
		event.setAction(replacePlaceholders(content, new String(json)));
		event.setActor("Me");
		event.setEventName(title);
		event.setRecommendationId("Notification");
		event.setEventProperties(new HashMap<>());
		event.setTimestamp(new Date().getTime());
		
		try {
			producer.publish(serializer.serialize(event));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public String replacePlaceholders(String content, String json) {
        List<String> placeholders = getPlaceholders(content);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        for(String placeholder : placeholders) {
            String replacedValue = getPropertyValue(jsonObject, placeholder);
            content = content.replaceAll(placeholder, replacedValue);
        }

        return content;
    }

    private String getPropertyValue(JsonObject jsonObject, String placeholder) {
        String jsonKey = placeholder.replaceAll("#", "");
        return String.valueOf(jsonObject.get(jsonKey).getAsString());
    }

    private List<String> getPlaceholders(String content) {
        List<String> results = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            results.add(matcher.group());
        }
        return results;
    }

    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("eventId", "abc");
        jsonObject.addProperty("timestamp", 123456645);

        String content = "This is a new event with id #eventId# and timestamp #timestamp#";
        String json = new Gson().toJson(jsonObject);
        System.out.println(json);
        System.out.println(new NotificationProducer(null).replacePlaceholders(content, json));
    }

}
