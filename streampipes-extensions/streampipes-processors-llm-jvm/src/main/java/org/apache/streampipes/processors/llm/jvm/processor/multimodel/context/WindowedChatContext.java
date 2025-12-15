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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Retains the <em>N most‑recent</em> (user, AI) turns (FIFO).
 * System prompt is always prepended.
 */
public class WindowedChatContext implements ChatContext {

  private final ChatMessage systemPrompt;
  private final int capacity; // number of turns to keep
  private final Deque<Turn> turns;

  public WindowedChatContext(ChatMessage systemPrompt, int capacity) {
    this.systemPrompt = Objects.requireNonNull(systemPrompt, "systemPrompt");
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be > 0");
    }
    this.capacity = capacity;
    this.turns = new ArrayDeque<>(capacity);
  }

  @Override
  public List<ChatMessage> buildRequest(UserMessage userInput) {
    List<ChatMessage> msgs = new ArrayList<>(1 + capacity * 2 + 1);
    msgs.add(systemPrompt);
    for (Turn t : turns) {
      // Add the user message
      msgs.add(t.user());
      // Then add the AI response
      msgs.add(t.ai());
    }
    msgs.add(userInput);
    return msgs;
  }

  @Override
  public void recordTurn(UserMessage userMessage, AiMessage aiMessage) {
    if (turns.size() == capacity) {
      turns.pollFirst(); // drop oldest
    }
    turns.offerLast(new Turn(userMessage, aiMessage));
  }
}
