package org.streampipes.pe.sinks.standalone.samples.file;

import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class FileController extends StandaloneEventSinkDeclarer<FileParameters> {

  private FileWriter fileWriter;

  private static final String PATH_KEY = "path";

  @Override
  public DataSinkDescription declareModel() {
    DataSinkDescription desc = DataSinkBuilder
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
  public ConfiguredEventSink<FileParameters> onInvocation(DataSinkInvocation graph) {
    String path = getExtractor(graph).singleValueParameter(PATH_KEY, String.class);

    FileParameters params = new FileParameters(graph, path);

    return new ConfiguredEventSink<>(params, FileWriter::new);
  }

}
