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
package org.streampipes.manager.template;

import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateInvocation;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.management.StorageDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class TestTemplateGenerator {

  public static void main(String[] args) {
    List<PipelineTemplateDescription> descriptions = new PipelineTemplateGenerator().makeExampleTemplates();

    if (descriptions.size() > 0) {
      PipelineTemplateInvocation invocation = new PipelineTemplateInvocationGenerator(getSource(), descriptions.get(0)).generateInvocation();
      //Pipeline pipeline = new PipelineGenerator("http://localhost:8089/sep/source_random/random-data-set", descriptions.get(0), "test").makePipeline();

      Operations.handlePipelineTemplateInvocation("riemer@fzi.de", invocation);
      try {
        System.out.println(Utils.asString(new JsonLdTransformer().toJsonLd(invocation)));
      } catch (IllegalAccessException | InvocationTargetException | InvalidRdfException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

  }

  private static SpDataStream getSource() {
    return StorageDispatcher.INSTANCE.getTripleStore().getStorageAPI().getEventStreamById("http://localhost:8089/sep/source_random/random-data-set");
  }
}
