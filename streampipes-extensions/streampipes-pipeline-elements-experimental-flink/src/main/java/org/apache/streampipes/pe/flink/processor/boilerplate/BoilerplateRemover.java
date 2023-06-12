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

package org.apache.streampipes.pe.flink.processor.boilerplate;

import org.apache.streampipes.model.runtime.Event;

import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.extractors.ExtractorBase;
import com.kohlschutter.boilerpipe.sax.BoilerpipeSAXInput;
import com.kohlschutter.boilerpipe.sax.HTMLDocument;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.nio.charset.Charset;

public class BoilerplateRemover implements FlatMapFunction<Event, Event> {

  private String htmlProperty;
  private OutputMode outputMode;

  private ExtractorBase extractor;
  private HTMLHighlighter htmlHighlighter;

  public BoilerplateRemover(String htmlProperty, ExtractorMode extractorMode, OutputMode outputMode) {
    this.htmlProperty = htmlProperty;
    this.outputMode = outputMode;
    this.htmlHighlighter = null;
    setExtractor(extractorMode);
  }

  @Override
  public void flatMap(Event event, Collector<Event> collector) throws Exception {
    String value = event.getFieldBySelector(htmlProperty).getAsPrimitive().getAsString();

    HTMLDocument htmlDoc = new HTMLDocument(value.getBytes(), Charset.defaultCharset());
    TextDocument textDoc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
    extractor.process(textDoc);

    String result = "";
    switch (outputMode) {
      case PLAIN_TEXT:
        result = textDoc.getContent();
        break;
      case HIGHLIGHTED_HTML:
        result = getHTMLHighligther(false).process(textDoc, htmlDoc.toInputSource());
        break;
      case HTML:
        result = getHTMLHighligther(true).process(textDoc, htmlDoc.toInputSource());
    }

    event.updateFieldBySelector(htmlProperty, result);

    collector.collect(event);
  }

  private void setExtractor(ExtractorMode extractorMode) {
    switch (extractorMode) {
      case ARTICLE:
        extractor = CommonExtractors.ARTICLE_EXTRACTOR;
        break;
      case DEFAULT:
        extractor = CommonExtractors.DEFAULT_EXTRACTOR;
        break;
      case LARGEST_CONTENT:
        extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;
        break;
      case CANOLA:
        extractor = CommonExtractors.CANOLA_EXTRACTOR;
        break;
      case KEEP_EVERYTHING:
        extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;
    }
  }

  private HTMLHighlighter getHTMLHighligther(boolean extractHTML) {
    if (htmlHighlighter == null) {
      if (extractHTML) {
        htmlHighlighter = HTMLHighlighter.newExtractingInstance();
      } else {
        htmlHighlighter = HTMLHighlighter.newHighlightingInstance();
      }
    }
    return htmlHighlighter;
  }

}
