package org.streampipes.dataformat;

public abstract class SpDataFormatFactory {

  public abstract String getTransportFormatRdfUri();

  public abstract SpDataFormatDefinition createInstance();
}
