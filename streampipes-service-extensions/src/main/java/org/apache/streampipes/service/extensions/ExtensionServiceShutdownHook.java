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

package org.apache.streampipes.service.extensions;

/**
 * Cleanup that an extension service runs when it shuts down, in addition to stopping
 * the pipeline elements, adapters and functions it knows about. Register hooks by
 * overriding {@link StreamPipesExtensionsServiceBase#getAdditionalShutdownHooks()}.
 *
 * <p>Hooks run after the web server and the broker receiver are closed, so they must
 * not rely on requests from the core. Exceptions are logged and do not prevent the
 * remaining hooks from running.
 */
@FunctionalInterface
public interface ExtensionServiceShutdownHook {

  void onShutdown();
}
