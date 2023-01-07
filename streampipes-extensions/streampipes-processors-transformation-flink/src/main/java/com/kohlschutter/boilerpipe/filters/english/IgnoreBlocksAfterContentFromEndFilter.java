/**
 * boilerpipe
 *
 * Copyright (c) 2009, 2014 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
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
import com.kohlschutter.boilerpipe.labels.DefaultLabels;

import java.util.List;
import java.util.ListIterator;

/**
 * Marks all blocks as "non-content" that occur after blocks that have been marked
 * {@link DefaultLabels#INDICATES_END_OF_TEXT}, and after any content block. This filter can be used
 * in conjunction with an upstream {@link TerminatingBlocksFinder}.
 * 
 * @see TerminatingBlocksFinder
 */
public final class IgnoreBlocksAfterContentFromEndFilter extends HeuristicFilterBase implements
    BoilerpipeFilter {
  public static final IgnoreBlocksAfterContentFromEndFilter INSTANCE =
      new IgnoreBlocksAfterContentFromEndFilter();

  private IgnoreBlocksAfterContentFromEndFilter() {
  }

  public boolean process(TextDocument doc) throws BoilerpipeProcessingException {
    boolean changes = false;

    int words = 0;

    List<TextBlock> blocks = doc.getTextBlocks();
    if (!blocks.isEmpty()) {
      ListIterator<TextBlock> it = blocks.listIterator(blocks.size());

      TextBlock tb;

      while (it.hasPrevious()) {
        tb = it.previous();
        if (tb.hasLabel(DefaultLabels.INDICATES_END_OF_TEXT)) {
          tb.addLabel(DefaultLabels.STRICTLY_NOT_CONTENT);
          tb.removeLabel(DefaultLabels.MIGHT_BE_CONTENT);
          tb.setIsContent(false);
          changes = true;
        } else if (tb.isContent()) {
          words += tb.getNumWords();
          if (words > 200) {
            break;
          }
        }

      }
    }

    return changes;
  }
}
