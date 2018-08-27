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
package org.streampipes.processors.imageprocessing.jvm.processor.genericclassification;

import boofcv.abst.scene.ImageClassifier;
import boofcv.factory.scene.ClassifierAndSource;
import boofcv.factory.scene.FactoryImageClassifier;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import deepboof.io.DeepBoofDataBaseOps;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.imageprocessing.jvm.processor.commons.PlainImageTransformer;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GenericImageClassification extends StandaloneEventProcessorEngine<GenericImageClassificationParameters> {

  private GenericImageClassificationParameters params;
  private ClassifierAndSource cs;

  private ImageClassifier<Planar<GrayF32>> classifier;
  private List<String> categories;

  public GenericImageClassification(GenericImageClassificationParameters params) {
    super(params);
  }

  @Override
  public void onInvocation(GenericImageClassificationParameters genericImageClassificationParameters, DataProcessorInvocation dataProcessorInvocation) {
    this.params = genericImageClassificationParameters;
    //this.cs = FactoryImageClassifier.vgg_cifar10();  // Test set 89.9% for 10 categories
		ClassifierAndSource cs = FactoryImageClassifier.nin_imagenet(); // Test set 62.6% for 1000 categories

    File path = DeepBoofDataBaseOps.downloadModel(cs.getSource(), new File("download_data"));

    this.classifier = cs.getClassifier();
    try {
      this.classifier.loadModel(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.categories = classifier.getCategories();
  }

  @Override
  public void onEvent(Map<String, Object> in, String s, SpOutputCollector out) {
    PlainImageTransformer<GenericImageClassificationParameters> imageTransformer = new PlainImageTransformer<>(in,
            params);


    Optional<BufferedImage> imageOpt = imageTransformer.getImage(params.getImagePropertyName());
    if (imageOpt.isPresent()) {
      BufferedImage buffered = imageOpt.get();
      Planar<GrayF32> image = new Planar<>(GrayF32.class, buffered.getWidth(), buffered.getHeight(), 3);
      ConvertBufferedImage.convertFromPlanar(buffered, image, true, GrayF32.class);

      classifier.classify(image);
      List<ImageClassifier.Score> scores = classifier.getAllResults();
      scores.sort(new Comparator<ImageClassifier.Score>() {
        @Override
        public int compare(ImageClassifier.Score o1, ImageClassifier.Score o2) {
          return (o1.score - o2.score) >= 0 ? -1 : 1;
        }
      });
      //Collections.reverse(scores);

      if (scores.size() > 0) {
        System.out.println(scores.get(0).score +":" +categories.get(scores.get(0).category));
        //scores.forEach(score -> System.out.println(score.category +":" +categories.get(score.category) +":" +score));
        Map<String, Object> outMap = new HashMap<>();
        outMap.put("score", scores.get(0).score);
        outMap.put("category", categories.get(scores.get(0).category));
        out.onEvent(outMap);
      }
    }
  }

  @Override
  public void onDetach() {

  }
}
