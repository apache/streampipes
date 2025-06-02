<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

## Multi-Model Prompt Processor

![icon](icon.png)

---

### Description

Choose between **OpenAI**, **Anthropic** or **Ollama / Llama** models and keep a *stateless*, *windowed* or *full*
conversation when streaming events through StreamPipes.

---

### Configuration

### Configuration overview
| Setting               | Purpose                                                                      |
|-----------------------|------------------------------------------------------------------------------|
| **Model Provider**    | `"OpenAI"`, `"Anthropic"` or `"Ollama"`                                      |
| **Model Name**        | Any model string supported by the chosen provider (lists below)              |
| **Temperature**       | *0–1* randomness control (higher->more creative, lower-> more deterministic) |
| **System Prompt**     | Sets the assistant’s role / rules                                            |
| **Input Field**       | Event field treated as the user message                                      |
| **History Mode**      | `"Stateless"`, `"Windowed"`, or `"Full"`                                     |
| **Window Size**       | N recent turns to keep when **Windowed**                                     |
| **OpenAI API Key**    | Api Key for OpenAI                                                           |
| **Anthropic API Key** | Api Key for Anthropic                                                        |
| **Ollama Base URL**   | Base URL of hosted Ollama instance                                           |
| **Output Field**      | Name of the new field carrying the LLM reply                                 |

---

### Ready‑to‑use model names

#### Anthropic
- `claude-3-7-sonnet-20250219`
- `claude-3-5-sonnet-20241022`
- `claude-3-5-haiku-20241022`
- `claude-3-5-sonnet-20240620`
- `claude-3-opus-20240229`
- `claude-3-sonnet-20240229`
- `claude-3-haiku-20240307`
- `claude-2.1`
- `claude-2.0`

#### OpenAI
- `gpt-3.5-turbo`
- `gpt-3.5-turbo-1106`
- `gpt-3.5-turbo-0125`
- `gpt-3.5-turbo-16k`
- `gpt-4`
- `gpt-4-0613`
- `gpt-4-turbo-preview`
- `gpt-4-1106-preview`
- `gpt-4-0125-preview`
- `gpt-4-turbo`
- `gpt-4-turbo-2024-04-09`
- `gpt-4-32k`
- `gpt-4-32k-0613`
- `gpt-4o`
- `gpt-4o-2024-05-13`
- `gpt-4o-2024-08-06`
- `gpt-4o-2024-11-20`
- `gpt-4o-mini`
- `gpt-4o-mini-2024-07-18`
- `o1`
- `o1-2024-12-17`
- `o1-mini`
- `o1-mini-2024-09-12`
- `o1-preview`
- `o1-preview-2024-09-12`
- `o3-mini`
- `o3-mini-2025-01-31`

#### Llama suggestions (Ollama)
- `llama3`
- `llama3:8b`
- `llama2`
- `mistral‑7b‑instruct`

*(Add any custom model present in your Ollama instance.)*

---

### Output

Each event is forwarded with an extra field (`llmResponse` by default) containing the model’s answer.  
Conversation state is kept **in‑memory** and reset when the pipeline stops/restarts.
