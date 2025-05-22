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
package org.apache.streampipes.processors.llm.jvm.processor.multimodel;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.llm.jvm.processor.multimodel.context.ChatContext;
import org.apache.streampipes.processors.llm.jvm.processor.multimodel.context.FullHistoryChatContext;
import org.apache.streampipes.processors.llm.jvm.processor.multimodel.context.StatelessChatContext;
import org.apache.streampipes.processors.llm.jvm.processor.multimodel.context.WindowedChatContext;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processor that calls an LLM (OpenAI, Anthropic, or Ollama) and appends the
 * model’s answer as a new event field. History behaviour (stateless, N‑window,
 * or full conversation) is configurable per pipeline instance.
 */
public class MultiModelPromptProcessor extends StreamPipesDataProcessor {

  // UI config IDs
  public static final String MODEL_PROVIDER_ID = "modelProvider";
  public static final String MODEL_NAME_ID = "modelName";
  public static final String SYSTEM_PROMPT_ID = "systemPrompt";
  public static final String ANTHROPIC_KEY_ID = "anthropicKey";
  public static final String OPENAPI_KEY_ID = "openApiKey";
  public static final String OLLAMA_URL_ID = "ollamaUrl";
  public static final String MAPPING_INPUT_ID = "inputField";
  public static final String HISTORY_MODE_ID = "historyMode";
  public static final String WINDOW_SIZE_ID = "windowSize";
  public static final String TEMPERATURE = "temperature";
  public static final String OUTPUT_FIELD_ID = "llmResponse";
  public static final String STATELESS = "Stateless";
  public static final String WINDOWED = "Windowed";
  public static final String FULL = "Full";
  private static final Logger LOG = LoggerFactory.getLogger(MultiModelPromptProcessor.class);
  // Model provider constants
  private static final String PROVIDER_OPENAI = "OpenAI";
  private static final String PROVIDER_ANTHROPIC = "Anthropic";
  private static final String PROVIDER_OLLAMA = "Ollama";
  // Runtime state
  private ChatLanguageModel chatModel;
  private ChatContext chatContext;
  private Set<String> inputFieldSelectors;

  /* Simple null / blank guard */
  private static void requireNonBlank(String value, String message) throws SpRuntimeException {
    if (value == null || value.isBlank()) {
      throw new SpRuntimeException(message);
    }
  }

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
                    "org.apache.streampipes.processors.llm.jvm.multimodel",
                    0
            )
            .category(DataProcessorType.TRANSFORM)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)

            // Input mapping
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(),
                            Labels.withId(MAPPING_INPUT_ID),
                            PropertyScope.NONE)
                    .build())

            // LLM selection
            .requiredAlternatives(Labels.withId(MODEL_PROVIDER_ID),
                    Alternatives.from(Labels.withId(PROVIDER_OPENAI),
                            StaticProperties.secretValue(Labels.withId(OPENAPI_KEY_ID))),
                    Alternatives.from(Labels.withId(PROVIDER_ANTHROPIC),
                            StaticProperties.secretValue(Labels.withId(ANTHROPIC_KEY_ID))),
                    Alternatives.from(Labels.withId(PROVIDER_OLLAMA),
                            StaticProperties.stringFreeTextProperty(Labels.withId(OLLAMA_URL_ID))))
            .requiredTextParameter(Labels.withId(MODEL_NAME_ID))
            .requiredFloatParameter(Labels.withId(TEMPERATURE), 0.1F, 0.1F, 0.9F, 0.1F)

            // Prompt & history behaviour
            .requiredCodeblock(Labels.withId(SYSTEM_PROMPT_ID), CodeLanguage.None)
            .requiredSingleValueSelection(
                    Labels.withId(HISTORY_MODE_ID),
                    Options.from(STATELESS, WINDOWED, FULL))
            .requiredIntegerParameter(
                    Labels.withId(WINDOW_SIZE_ID), 0, 100, 1)

            // Output mapping
            .outputStrategy(OutputStrategies.append(
                    EpProperties.stringEp(Labels.empty(), OUTPUT_FIELD_ID, SO.TEXT)))
            .build();
  }

  @Override
  public void onInvocation(ProcessorParams params,
                           SpOutputCollector collector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    var extractor = params.extractor();
    String provider = extractor.selectedAlternativeInternalId(MODEL_PROVIDER_ID);
    String modelName = extractor.singleValueParameter(MODEL_NAME_ID, String.class);
    String systemPromptRaw = extractor.codeblockValue(SYSTEM_PROMPT_ID);
    String anthropicKey = (PROVIDER_ANTHROPIC.equalsIgnoreCase(provider)
            ? extractor.secretValue(ANTHROPIC_KEY_ID) : null);
    String openApiKey = (PROVIDER_OPENAI.equalsIgnoreCase(provider)
            ? extractor.secretValue(OPENAPI_KEY_ID) : null);
    String ollamaUrl = (PROVIDER_OLLAMA.equalsIgnoreCase(provider)
            ? extractor.singleValueParameter(OLLAMA_URL_ID, String.class) : null);
    Double temperature = extractor.singleValueParameter(TEMPERATURE, Double.class);
    this.inputFieldSelectors = new HashSet<>(extractor.mappingPropertyValues(MAPPING_INPUT_ID));

    // Build model
    this.chatModel = buildChatModel(provider, modelName, openApiKey, anthropicKey, ollamaUrl, temperature);

    // Build history strategy
    String mode = extractor.selectedSingleValue(HISTORY_MODE_ID, String.class);

    SystemMessage systemMsg = SystemMessage.from(systemPromptRaw);

    switch (mode) {
      case STATELESS -> this.chatContext = new StatelessChatContext(systemMsg);

      case WINDOWED -> {
        int windowSize = extractor.singleValueParameter(WINDOW_SIZE_ID, Integer.class);
        this.chatContext = new WindowedChatContext(systemMsg, windowSize);
      }

      case FULL -> this.chatContext = new FullHistoryChatContext(systemMsg);

      default -> throw new SpRuntimeException("Unknown history mode: " + mode);
    }

    LOG.info("MultiModelPromptProcessor initialised – provider={}, model={}, mode={}",
            provider, modelName, mode);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {

    // Build user message
    String payload = event.getFields().entrySet().stream()
            .filter(e -> inputFieldSelectors.contains(e.getKey()))
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + '=' + e.getValue().getAsPrimitive().getAsString())
            .collect(Collectors.joining(", "));
    UserMessage userMsg = UserMessage.from(payload);

    // Build request & call LLM
    List<ChatMessage> request = chatContext.buildRequest(userMsg);
    ChatResponse resp = chatModel.chat(ChatRequest.builder()
            .messages(request)
            .build());
    AiMessage aiMsg = resp.aiMessage();

    // Update history & emit event
    chatContext.recordTurn(userMsg, aiMsg);

    event.addField(OUTPUT_FIELD_ID, aiMsg.text());
    collector.collect(event);
  }

  @Override
  public void onDetach() {
    LOG.info("MultiModelPromptProcessor detached – history cleared");
  }

  /**
   * Instantiates the correct {@link ChatLanguageModel} based on the selected
   * provider. Validation errors are surfaced as {@link SpRuntimeException}.
   */
  private ChatLanguageModel buildChatModel(String provider,
                                           String modelName,
                                           String openApiKey,
                                           String anthropicKey,
                                           String ollamaUrl,
                                           Double temperature) throws SpRuntimeException {

    Objects.requireNonNull(modelName, "modelName");

    return switch (provider) {
      case PROVIDER_OPENAI -> {
        requireNonBlank(openApiKey, "API key is required for OpenAI");
        yield OpenAiChatModel.builder()
                .apiKey(openApiKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
      }
      case PROVIDER_ANTHROPIC -> {
        requireNonBlank(anthropicKey, "API key is required for Anthropic");
        yield AnthropicChatModel.builder()
                .apiKey(anthropicKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
      }
      case PROVIDER_OLLAMA -> {
        requireNonBlank(ollamaUrl, "Base URL is required for Ollama");
        yield OllamaChatModel.builder()
                .baseUrl(ollamaUrl)
                .modelName(modelName)
                .temperature(temperature)
                .build();
      }
      default -> throw new SpRuntimeException("Unknown model provider: " + provider);
    };
  }
}
