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

import java.util.List;

/**
 * Strategy interface that decides *how much* conversational state (if any) is
 * carried from one LLM turn to the next.
 * <p>
 * Typical call‑flow per event:
 * <ol>
 *   <li>{@code List<ChatMessage> req = context.buildRequest(userMsg);}</li>
 *   <li>Send {@code req} to the model and obtain {@link AiMessage}</li>
 *   <li>{@code context.recordTurn(userMsg, aiMsg);} — lets the implementation
 *       keep or discard the turn as it sees fit</li>
 * </ol>
 */
public interface ChatContext {

  /**
   * Builds the list of messages that will be sent to the LLM **for the current
   * input**. Implementations <em>must</em> prepend their fixed system prompt
   * and append the supplied {@code userInput}.
   *
   * @param userInput user message for the current turn
   * @return immutable message list in correct order
   */
  List<ChatMessage> buildRequest(UserMessage userInput);

  /**
   * Gives the strategy a chance to store (or ignore) the freshly completed
   * turn.
   *
   * @param userMessage the user part of the turn
   * @param aiMessage   the LLM’s answer
   */
  void recordTurn(UserMessage userMessage, AiMessage aiMessage);
}

/* -------- Internal helper ------------------------------------------------ */

record Turn(UserMessage user, AiMessage ai) {
}
