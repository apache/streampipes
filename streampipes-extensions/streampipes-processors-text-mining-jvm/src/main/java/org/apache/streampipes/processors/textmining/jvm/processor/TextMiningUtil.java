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

package org.apache.streampipes.processors.textmining.jvm.processor;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.List;

public class TextMiningUtil {
  /*
   * Given an array of spans and an array of tokens, it extracts and merges the tokens
   * specified in the spans and adds them to a list. This list is returned
   */
  public static List<String> extractSpans(Span[] spans, String[] tokens) throws SpRuntimeException {
    List<String> list = new ArrayList<>();
    for (Span span : spans) {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = span.getStart(); i < span.getEnd(); i++) {
        if (i >= tokens.length) {
          throw new SpRuntimeException("token list does not fit spans (token list lenght: " + tokens.length
              + ", span: [" + span.getStart() + ", " + span.getEnd() + "))");
        }
        stringBuilder.append(tokens[i]).append(' ');
      }
      // Removing the last space
      stringBuilder.setLength(Math.max(stringBuilder.length() - 1, 0));
      list.add(stringBuilder.toString());
    }
    return list;
  }
}
