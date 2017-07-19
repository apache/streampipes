package org.streampipes.sdk.tutorial;

import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sources.AbstractAlreadyExistingStream;

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
