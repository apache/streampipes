/**
 * boilerpipe
 * <p>
 * Copyright (c) 2009, 2014 Christian Kohlschütter
 * <p>
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kohlschutter.boilerpipe.extractors;

import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.filters.english.NumWordsRulesClassifier;

/**
 * A quite generic full-text extractor solely based upon the number of words per block (the current,
 * the previous and the next block).
 */
public class NumWordsRulesExtractor extends ExtractorBase {
  public static final NumWordsRulesExtractor INSTANCE = new NumWordsRulesExtractor();

  /**
   * Returns the singleton instance for {@link NumWordsRulesExtractor}.
   */
  public static NumWordsRulesExtractor getInstance() {
    return INSTANCE;
  }

  public boolean process(TextDocument doc) throws BoilerpipeProcessingException {

    return NumWordsRulesClassifier.INSTANCE.process(doc);
  }

}
