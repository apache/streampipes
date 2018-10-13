package de.fzi.cep.sepa.storm.utils;

import backtype.storm.spout.Scheme;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import scala.NotImplementedError;

import java.net.URI;

public class StormUtils {
  public static Scheme getScheme(EventStream eventStream) {
    if (isJson(eventStream)) {
      return new JsonScheme(eventStream);
    } else {
      throw new NotImplementedError();
    }

  }


  public static boolean isJson(EventStream eventStream) {
    return eventStream
            .getEventGrounding()
            .getTransportFormats()
            .get(0)
            .getRdfType()
            .contains(URI.create(MessageFormat.Json));
  }
}
