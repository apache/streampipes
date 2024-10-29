/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.manager.template.instances;

import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.model.template.CompactPipelineTemplate;
import org.apache.streampipes.model.template.PipelinePlaceholderConfig;
import org.apache.streampipes.model.template.PipelinePlaceholders;

import java.util.List;
import java.util.Map;

public class PersistDataLakePipelineTemplate implements DefaultPipelineTemplateProvider {

  public static final String DATA_LAKE_SINK_REF = "lake";
  public static final String DATA_LAKE_SINK_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  public static final String DATA_LAKE_CONNECTOR_ID = "stream1";
  public static final String DATA_LAKE_TEMPLATE_ID = "sp-internal-persist";

  public static final String DATA_LAKE_MEASUREMENT_FIELD = "db_measurement";
  public static final String DATA_LAKE_TIMESTAMP_FIELD = "timestamp_mapping";
  public static final String DATA_LAKE_DIMENSIONS_FIELD = "dimensions_selection";

  @Override
  public CompactPipelineTemplate getTemplate() {
    var template = new CompactPipelineTemplate();
    template.setElementId(DATA_LAKE_TEMPLATE_ID);
    template.setName("Persist Data");
    template.setDescription("Use this template to persist an input data stream to the time-series storage");
    template.setPipeline(List.of(
            new CompactPipelineElement(
                "sink",
                DATA_LAKE_SINK_REF,
                DATA_LAKE_SINK_ID,
                List.of(DATA_LAKE_CONNECTOR_ID),
                List.of(
                    Map.of("schema_update", "Update schema"),
                    Map.of("ignore_duplicates", false)
                ),
                null
            )
        )
    );
    template.setPlaceholders(new PipelinePlaceholders(
        List.of("stream1"),
        List.of(
            new PipelinePlaceholderConfig(DATA_LAKE_SINK_REF, DATA_LAKE_MEASUREMENT_FIELD),
            new PipelinePlaceholderConfig(DATA_LAKE_SINK_REF, DATA_LAKE_TIMESTAMP_FIELD),
            new PipelinePlaceholderConfig(DATA_LAKE_SINK_REF, DATA_LAKE_DIMENSIONS_FIELD)
        )
    ));

    return template;
  }
}
