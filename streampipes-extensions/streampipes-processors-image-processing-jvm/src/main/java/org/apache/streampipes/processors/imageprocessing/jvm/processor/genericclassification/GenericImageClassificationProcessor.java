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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.genericclassification;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.ImagePropertyConstants;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.PlainImageTransformer;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import boofcv.abst.scene.ImageClassifier;
import boofcv.factory.scene.ClassifierAndSource;
import boofcv.factory.scene.FactoryImageClassifier;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import deepboof.io.DeepBoofDataBaseOps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GenericImageClassificationProcessor extends StreamPipesDataProcessor {

  private String imagePropertyName;

  private ClassifierAndSource cs;

  private ImageClassifier<Planar<GrayF32>> classifier;

  private List<String> categories;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processor.imageclassification.jvm.generic-image-classification")
        .category(DataProcessorType.IMAGE_PROCESSING)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements
                .domainPropertyReq("https://image.com"), Labels.withId(
                ImagePropertyConstants.IMAGE_MAPPING.getProperty()), PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(
            EpProperties.doubleEp(Labels.empty(), "score", "https://schema.org/score"),
            EpProperties.stringEp(Labels.empty(), "category", "https://schema.org/category")

        ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    ProcessingElementParameterExtractor extractor = parameters.extractor();

    imagePropertyName = extractor.mappingPropertyValue(ImagePropertyConstants.IMAGE_MAPPING.getProperty());
    // this.cs = FactoryImageClassifier.vgg_cifar10();  // Test set 89.9% for 10 categories
    ClassifierAndSource cs = FactoryImageClassifier.nin_imagenet(); // Test set 62.6% for 1000 categories

    File path = DeepBoofDataBaseOps.downloadModel(cs.getSource(), new File("download_data"));

    this.classifier = cs.getClassifier();
    try {
      this.classifier.loadModel(path);
    } catch (IOException e) {
      throw new SpRuntimeException(e.getMessage());
    }
    this.categories = classifier.getCategories();
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    PlainImageTransformer imageTransformer = new PlainImageTransformer(in);

    Optional<BufferedImage> imageOpt = imageTransformer.getImage(imagePropertyName);
    if (imageOpt.isPresent()) {
      BufferedImage buffered = imageOpt.get();
      Planar<GrayF32> image = new Planar<>(GrayF32.class, buffered.getWidth(), buffered.getHeight(), 3);
      ConvertBufferedImage.convertFromPlanar(buffered, image, true, GrayF32.class);

      classifier.classify(image);
      List<ImageClassifier.Score> scores = classifier.getAllResults();
      scores.sort((o1, o2) -> Double.compare(o2.score, o1.score));

      if (!scores.isEmpty()) {
        System.out.println(scores.get(0).score + ":" + categories.get(scores.get(0).category));
        //scores.forEach(score -> System.out.println(score.category +":" +categories.get(score.category) +":" +score));
        in.addField("score", scores.get(0).score);
        in.addField("category", categories.get(scores.get(0).category));
        out.collect(in);
      }
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
