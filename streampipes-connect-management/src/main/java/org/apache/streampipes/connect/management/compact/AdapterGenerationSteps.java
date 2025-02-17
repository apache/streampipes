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

package org.apache.streampipes.connect.management.compact;

import org.apache.streampipes.connect.management.compact.generator.AdapterBasicsGenerator;
import org.apache.streampipes.connect.management.compact.generator.AdapterConfigGenerator;
import org.apache.streampipes.connect.management.compact.generator.AdapterEnrichmentRuleGenerator;
import org.apache.streampipes.connect.management.compact.generator.AdapterModelGenerator;
import org.apache.streampipes.connect.management.compact.generator.AdapterSchemaGenerator;
import org.apache.streampipes.connect.management.compact.generator.AdapterTransformationRuleGenerator;
import org.apache.streampipes.connect.management.management.GuessManagement;

import java.util.List;

public class AdapterGenerationSteps {

  public List<AdapterModelGenerator> getGenerators() {
    return List.of(
        new AdapterBasicsGenerator(),
        new AdapterConfigGenerator(),
        new AdapterSchemaGenerator(
            new SchemaMetadataEnricher(),
            new GuessManagement()
        ),
        new AdapterEnrichmentRuleGenerator(),
        new AdapterTransformationRuleGenerator()
    );
  }
}
