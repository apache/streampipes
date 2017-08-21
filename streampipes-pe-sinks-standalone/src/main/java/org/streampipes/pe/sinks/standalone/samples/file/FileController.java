package org.streampipes.pe.sinks.standalone.samples.file;

import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class FileController extends StandaloneEventSinkDeclarer<FileParameters> {

  private FileWriter fileWriter;

  private static final String PATH_KEY = "path";

  @Override
  public SecDescription declareModel() {
    SecDescription desc = DataSinkBuilder
            .create("file", "File Output", "Writes the data in a csv file on the host system. The file name must be provided.")
            .iconUrl(ActionConfig.getIconUrl("file_icon"))
            .requiredStaticProperty(new FreeTextStaticProperty(PATH_KEY, "Path", "The path to the file " +
                    "including the file name"))
            .setStream1()
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();

    return desc;
  }


  @Override
  public ConfiguredEventSink<FileParameters, EventSink<FileParameters>> onInvocation(SecInvocation graph) {
    String path = getExtractor(graph).singleValueParameter(PATH_KEY, String.class);

    FileParameters params = new FileParameters(graph, path);

    return new ConfiguredEventSink<>(params, FileWriter::new);
  }

}
