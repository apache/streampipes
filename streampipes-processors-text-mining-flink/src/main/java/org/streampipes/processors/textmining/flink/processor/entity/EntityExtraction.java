/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.textmining.flink.processor.entity;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class EntityExtraction implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private String fieldName;
  private EntityExtractionModel entityExtractionModel;
  NameFinderME nameFinder;

  public EntityExtraction(String fieldName, EntityExtractionModel entityExtractionModel) throws IOException {
    this.fieldName = fieldName;
    this.entityExtractionModel = entityExtractionModel;
    this.loadModel();
  }

  private void loadModel() throws IOException {
    try (InputStream modelIn = new FileInputStream(entityExtractionModel.getFilename())){
      TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
      this.nameFinder = new NameFinderME(model);
    }
  }

  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
    String value = String.valueOf(in.get(fieldName));
    Span[] entities = nameFinder.find(new String[] {value});

    // TODO

    out.collect(in);

  }
}
