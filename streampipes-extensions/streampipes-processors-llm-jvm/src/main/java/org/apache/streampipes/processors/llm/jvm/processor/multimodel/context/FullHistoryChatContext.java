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
package org.apache.streampipes.processors.llm.jvm.processor.multimodel.context;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stores <em>every</em> turn from the first user input onward.
 * <p>
 * No pruning is performed – callers must ensure that the resulting prompt
 * stays below the target model’s context window.
 */
public class FullHistoryChatContext implements ChatContext {

  private final ChatMessage systemPrompt;
  private final List<Turn> turns;

  public FullHistoryChatContext(ChatMessage systemPrompt) {
    this.systemPrompt = Objects.requireNonNull(systemPrompt, "systemPrompt");
    this.turns = new CopyOnWriteArrayList<>();
  }

  @Override
  public List<ChatMessage> buildRequest(UserMessage userInput) {
    List<ChatMessage> out = new ArrayList<>(1 + turns.size() * 2 + 1);
    out.add(systemPrompt);
    for (Turn t : turns) {
      out.add(t.user());
      out.add(t.ai());
    }
    out.add(userInput);
    return Collections.unmodifiableList(out);
  }

  @Override
  public void recordTurn(UserMessage userMessage, AiMessage aiMessage) {
    turns.add(new Turn(userMessage, aiMessage));
  }
}
