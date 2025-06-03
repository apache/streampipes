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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;

// Interface can be simpler as constants are now in AbstractSwitchOperatorProcessor
public interface IStreamPipesSwitchProcessor extends IStreamPipesDataProcessor {
  // No constants needed here as they are now in the abstract base class for implementation details.
  // If you had methods specific to the "Switch" concept that don't belong to IStreamPipesDataProcessor,
  // they would go here. For now, it mostly serves as a marker interface and base for the abstract class.
}