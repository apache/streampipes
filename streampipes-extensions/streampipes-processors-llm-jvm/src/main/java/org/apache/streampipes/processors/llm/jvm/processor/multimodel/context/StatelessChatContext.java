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
import java.util.Objects;

/**
 * Keeps <strong>no</strong> conversational history.
 * Request = <code>[systemPrompt, userInput]</code>
 * <p>
 * Ideal for deterministic prompts such as classification, routing, or
 * single‑step calculations.
 */
public class StatelessChatContext implements ChatContext {

  private final ChatMessage systemPrompt;

  public StatelessChatContext(ChatMessage systemPrompt) {
    this.systemPrompt = Objects.requireNonNull(systemPrompt, "systemPrompt");
  }

  @Override
  public List<ChatMessage> buildRequest(UserMessage userInput) {
    return List.of(systemPrompt, userInput);
  }

  @Override
  public void recordTurn(UserMessage userMessage, AiMessage aiMessage) {
    /* intentionally empty – stateless */
  }
}
