package de.fzi.cep.sepa.actions.samples.proasense.pandda;

import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.PDFType;
import eu.proasense.internal.PredictedEvent;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by riemer on 12.02.2017.
 */
public class PanddaPublisher implements EventListener<byte[]> {

  private static final String PanddaOutputTopic = "";

  private EventProducer kafkaProducer;
  private PanddaParameters panddaParameters;
  private TSerializer serializer;

  public PanddaPublisher(String kafkaHost, Integer kafkaPort, PanddaParameters panddaParameters) {
    this.kafkaProducer = new StreamPipesKafkaProducer(buildUrl(kafkaHost, kafkaPort));
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
    PredictedEvent predictedEvent = buildPredictedEvent(inputJson);

    Optional<byte[]> serializedEvent = serialize(predictedEvent);
    if (serializedEvent.isPresent()) {
      this.kafkaProducer.publish(serializedEvent.get());
    }
  }

  private PredictedEvent buildPredictedEvent(String inputJson) {
    PredictedEvent predictedEvent = new PredictedEvent();
    predictedEvent.eventName = "<please put the stream id here>";
    predictedEvent.timestamp =  new Date().getTime() / 1000;
    predictedEvent.pdfType = PDFType.EXPONENTIAL;
    predictedEvent.eventProperties = new HashMap<String,ComplexValue>();

    predictedEvent.params = new ArrayList<Double>();
    predictedEvent.timestamps = new ArrayList<Long>();

    double lambda = 1 / 222.2;
    predictedEvent.params.add(lambda);
    predictedEvent.timestamps.add(System.currentTimeMillis());

    return predictedEvent;
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
}
