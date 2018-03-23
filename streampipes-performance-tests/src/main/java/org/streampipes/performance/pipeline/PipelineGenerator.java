/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.performance.pipeline;

import org.streampipes.commons.exceptions.NoMatchingJsonSchemaException;
import org.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.streampipes.commons.exceptions.RemoteServerNotAccessibleException;
import org.streampipes.manager.matching.PipelineVerificationHandler;
import org.streampipes.model.SpDataSequence;
import org.streampipes.model.client.exception.InvalidConnectionException;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineModificationMessage;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.performance.model.PerformanceTestSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipelineGenerator {

  private PerformanceTestSettings settings;

//  private static final String backendUrl = "http://backend:8030";
//  private static final String sourcesUrl = "http://pe-examples-sources:8090";
//  private static final String flinkUrl = "http://pe-flink:8094";

  private static final String backendUrl = "http://ipe-koi06.fzi.de:8030";
  private static final String sourcesUrl = "http://pe-examples-sources:8090";
  private static final String flinkUrl = "http://pe-flink:8094";

  private DataSourceDescription dataSourceDescription;
  private DataProcessorDescription timestampEnrichmentDescription;
  private DataSinkDescription elasticSearchDescription;

  public PipelineGenerator(PerformanceTestSettings settings) {
    this.settings = settings;
    this.dataSourceDescription = fetchDataSourceDescription();
    this.timestampEnrichmentDescription = fetchTimestampEnrichmentDescription();
    this.elasticSearchDescription = fetchElasticSearchDecription();
  }

  private DataSinkDescription fetchElasticSearchDecription() {

    return null;
  }

  private DataProcessorDescription fetchTimestampEnrichmentDescription() {

    return null;
  }

  private DataSourceDescription fetchDataSourceDescription() {

    return null;
  }

  public Pipeline buildPipeline() throws NoSepaInPipelineException, InvalidConnectionException, RemoteServerNotAccessibleException, NoMatchingJsonSchemaException {
    Pipeline pipeline = new Pipeline();
    pipeline.setStreams(buildStreams());
    pipeline.setActions(new ArrayList<>());
    pipeline.setSepas(new ArrayList<>());

    String lastElementId = "stream0";
    for (Integer i = 0; i < settings.getNumberOfTimestampEnrichmentEpas(); i++) {
      DataProcessorInvocation invocation = new DataProcessorInvocation(timestampEnrichmentDescription);
      invocation.setDOM("epa" + i);
      invocation.setConnectedTo(Arrays.asList(lastElementId));
      lastElementId = "epa" + i;
      pipeline.getSepas().add(invocation);
      PipelineModificationMessage message = verifyPipeline(pipeline);
      //pipeline.getSepas().get(i).setStaticProperties(modifyTimestampEnricher(message.getPipelineModifications().get
      //       (0)));
    }

    DataSinkInvocation dataSinkInvocation = new DataSinkInvocation(elasticSearchDescription);
    dataSinkInvocation.setConnectedTo(Arrays.asList(lastElementId));
    dataSinkInvocation.setDOM("action0");
    pipeline.getActions().add(dataSinkInvocation);

    PipelineModificationMessage message = verifyPipeline(pipeline);
    //message.getPipelineModifications().get(0)
    return null;

  }

  private List<SpDataSequence> buildStreams() {
    //Optional<SpDataStream> streamOpt = dataSourceDescription.getSpDataStreams().
    return null;
  }

  private PipelineModificationMessage verifyPipeline(Pipeline pipeline) throws NoSepaInPipelineException, InvalidConnectionException, NoMatchingJsonSchemaException, RemoteServerNotAccessibleException {
    return new PipelineVerificationHandler(pipeline).validateConnection()
            .computeMappingProperties().getPipelineModificationMessage();
  }

}
