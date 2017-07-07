package de.fzi.cep.sepa.tutorial;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Formats;
import de.fzi.cep.sepa.sdk.helpers.Protocols;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;

public class VehiclePositionStream extends AbstractAlreadyExistingStream {

  @Override
  public EventStream declareModel(SepDescription sep) {
      return DataStreamBuilder.create("vehicle-position", "Vehicle Position", "An event stream " +
              "that produces current vehicle positions")
              .property(EpProperties.timestampProperty("timestamp"))
              .property(EpProperties.stringEp("plateNumber", "http://my.company/plateNumber"))
              .property(EpProperties.doubleEp("latitude", Geo.lat))
              .property(EpProperties.doubleEp("longitude", Geo.lng))
              .format(Formats.jsonFormat())
              .protocol(Protocols.kafka("ipe-koi15.fzi.de", 9092, "my.company.vehicle" +
                      ".position"))
              .build();

  }
}
