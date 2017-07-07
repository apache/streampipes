package org.streampipes.pe.sinks.standalone.samples.proasense.pandda;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.Optional;

/**
 * Created by riemer on 12.02.2017.
 */
public class PanddaPublisher implements EventListener<byte[]> {

  private static final String PanddaOutputTopic = "si.ijs.internal.oa_output";

  private EventProducer kafkaProducer;
  private PanddaParameters panddaParameters;
  private TSerializer serializer;

  public PanddaPublisher(String kafkaHost, Integer kafkaPort, PanddaParameters panddaParameters) {
    this.kafkaProducer = new StreamPipesKafkaProducer(buildUrl(kafkaHost, kafkaPort), PanddaOutputTopic);
    this.panddaParameters = panddaParameters;
    this.serializer = new TSerializer(new TBinaryProtocol.Factory());
  }

  public void closePublisher() {
    kafkaProducer.closeProducer();
  }

  private String buildUrl(String kafkaHost, Integer kafkaPort) {
    return kafkaHost +":" +kafkaPort;
  }

  @Override
  public void onEvent(byte[] event) {
    String inputJson = new String(event);
    this.kafkaProducer.publish(buildPredictedEvent(inputJson));
  }

  private String buildPredictedEvent(String inputJson) {
    JsonObject json = new JsonObject();

    json.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
    json.add("eventName", new JsonPrimitive("prediction"));
    json.add("params", buildParams());
    json.add("pdfType", new JsonPrimitive("exponential"));
    json.add("eventProperties", buildEventProperties());
    json.add("timestamps", new JsonArray());

    System.out.println(json.toString());
    return json.toString();
  }

  private JsonArray buildParams() {
    JsonArray jsonArray = new JsonArray();
    jsonArray.add(new JsonPrimitive(0.0045));

    return jsonArray;
  }

  private JsonObject buildEventProperties() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("timeUnit", new JsonPrimitive("hour"));
    jsonObject.add("coeff", new JsonPrimitive(0.023453107960687173));
    jsonObject.add("std", new JsonPrimitive(0.0031882800000000004));
    jsonObject.add("zScore", new JsonPrimitive(6.519465442623263));

    return jsonObject;
  }
  private Optional<byte[]> serialize(TBase tbase)
  {
    try {
      return Optional.of(serializer.serialize(tbase));
    } catch (TException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public static void main(String[] args) {
    PanddaPublisher publisher = new PanddaPublisher("ipe-koi15.fzi.de", 9092, null);
    publisher.onEvent(new String("abc").getBytes());
  }
}
