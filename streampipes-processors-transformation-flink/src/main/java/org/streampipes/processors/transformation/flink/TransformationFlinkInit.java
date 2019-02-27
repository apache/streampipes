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

package org.streampipes.processors.transformation.flink;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.processors.transformation.flink.config.TransformationFlinkConfig;
import org.streampipes.processors.transformation.flink.processor.boilerplate.BoilerplateController;
import org.streampipes.processors.transformation.flink.processor.converter.FieldConverterController;
import org.streampipes.processors.transformation.flink.processor.hasher.FieldHasherController;
import org.streampipes.processors.transformation.flink.processor.mapper.FieldMapperController;
import org.streampipes.processors.transformation.flink.processor.rename.FieldRenamerController;

public class TransformationFlinkInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            .add(new FieldConverterController())
            .add(new FieldHasherController())
            .add(new FieldMapperController())
            //.add(new MeasurementUnitConverterController())
            .add(new FieldRenamerController())
            .add(new BoilerplateController());

    new TransformationFlinkInit().init(TransformationFlinkConfig.INSTANCE);
  }

}
