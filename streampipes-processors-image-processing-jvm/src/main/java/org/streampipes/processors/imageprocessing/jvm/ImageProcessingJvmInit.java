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

package org.streampipes.processors.imageprocessing.jvm;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.streampipes.processors.imageprocessing.jvm.config.ImageProcessingJvmConfig;
import org.streampipes.processors.imageprocessing.jvm.processor.genericclassification.GenericImageClassificationController;
import org.streampipes.processors.imageprocessing.jvm.processor.imagecropper.ImageCropperController;
import org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.ImageEnrichmentController;
import org.streampipes.processors.imageprocessing.jvm.processor.imagerectification.ImageRectificationController;
import org.streampipes.processors.imageprocessing.jvm.processor.qrreader.QrCodeReaderController;

public class ImageProcessingJvmInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton
            .getInstance()
            .add(new ImageEnrichmentController())
            .add(new ImageCropperController())
            .add(new ImageRectificationController())
            .add(new QrCodeReaderController())
            .add(new GenericImageClassificationController());

    DeclarersSingleton.getInstance().registerDataFormat(new JsonDataFormatFactory());
    DeclarersSingleton.getInstance().registerProtocol(new SpKafkaProtocolFactory());

    new ImageProcessingJvmInit().init(ImageProcessingJvmConfig.INSTANCE);
  }
}
