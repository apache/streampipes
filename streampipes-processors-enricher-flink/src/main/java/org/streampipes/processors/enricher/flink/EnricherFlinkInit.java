/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.processors.enricher.flink;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.streampipes.dataformat.fst.FstDataFormatFactory;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.streampipes.processors.enricher.flink.config.EnricherFlinkConfig;
import org.streampipes.processors.enricher.flink.processor.math.mathop.MathOpController;
import org.streampipes.processors.enricher.flink.processor.math.staticmathop.StaticMathOpController;
import org.streampipes.processors.enricher.flink.processor.timestamp.TimestampController;
import org.streampipes.processors.enricher.flink.processor.trigonometry.TrigonometryController;
import org.streampipes.processors.enricher.flink.processor.urldereferencing.UrlDereferencingController;

public class EnricherFlinkInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            .add(new TimestampController())
            .add(new MathOpController())
            .add(new StaticMathOpController())
            .add(new UrlDereferencingController())
            .add(new TrigonometryController());

    DeclarersSingleton.getInstance().registerDataFormats(new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory());

    new EnricherFlinkInit().init(EnricherFlinkConfig.INSTANCE);
  }

}
