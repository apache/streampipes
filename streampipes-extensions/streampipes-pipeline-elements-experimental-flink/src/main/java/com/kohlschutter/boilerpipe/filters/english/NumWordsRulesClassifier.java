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
package com.kohlschutter.boilerpipe.filters.english;

import com.kohlschutter.boilerpipe.BoilerpipeFilter;
import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextBlock;
import com.kohlschutter.boilerpipe.document.TextDocument;

import java.util.List;
import java.util.ListIterator;

/**
 * Classifies {@link TextBlock}s as content/not-content through rules that have been determined
 * using the C4.8 machine learning algorithm, as described in the paper
 * "Boilerplate Detection using Shallow Text Features" (WSDM 2010), particularly using number of
 * words per block and link density per block.
 */
public class NumWordsRulesClassifier implements BoilerpipeFilter {
  public static final NumWordsRulesClassifier INSTANCE = new NumWordsRulesClassifier();

  /**
   * Returns the singleton instance for RulebasedBoilerpipeClassifier.
   */
  public static NumWordsRulesClassifier getInstance() {
    return INSTANCE;
  }

  public boolean process(TextDocument doc) throws BoilerpipeProcessingException {
    List<TextBlock> textBlocks = doc.getTextBlocks();
    boolean hasChanges = false;

    ListIterator<TextBlock> it = textBlocks.listIterator();
    if (!it.hasNext()) {
      return false;
    }
    TextBlock prevBlock = TextBlock.EMPTY_START;
    TextBlock currentBlock = it.next();
    TextBlock nextBlock = it.hasNext() ? it.next() : TextBlock.EMPTY_START;

    hasChanges = classify(prevBlock, currentBlock, nextBlock) | hasChanges;

    if (nextBlock != TextBlock.EMPTY_START) {
      while (it.hasNext()) {
        prevBlock = currentBlock;
        currentBlock = nextBlock;
        nextBlock = it.next();
        hasChanges = classify(prevBlock, currentBlock, nextBlock) | hasChanges;
      }
      prevBlock = currentBlock;
      currentBlock = nextBlock;
      nextBlock = TextBlock.EMPTY_START;
      hasChanges = classify(prevBlock, currentBlock, nextBlock) | hasChanges;
    }

    return hasChanges;
  }

  protected boolean classify(final TextBlock prev, final TextBlock curr, final TextBlock next) {
    final boolean isContent;

    if (curr.getLinkDensity() <= 0.333333) {
      if (prev.getLinkDensity() <= 0.555556) {
        if (curr.getNumWords() <= 16) {
          if (next.getNumWords() <= 15) {
            if (prev.getNumWords() <= 4) {
              isContent = false;
            } else {
              isContent = true;
            }
          } else {
            isContent = true;
          }
        } else {
          isContent = true;
        }
      } else {
        if (curr.getNumWords() <= 40) {
          if (next.getNumWords() <= 17) {
            isContent = false;
          } else {
            isContent = true;
          }
        } else {
          isContent = true;
        }
      }
    } else {
      isContent = false;
    }

    return curr.setIsContent(isContent);
  }

}
