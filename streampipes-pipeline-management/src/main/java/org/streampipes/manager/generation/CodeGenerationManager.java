/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.generation;

import java.io.File;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.graph.DataProcessorDescription;

public class CodeGenerationManager {

	private DeploymentConfiguration config;
	private DataProcessorDescription element;
	
	public CodeGenerationManager(DeploymentConfiguration config, DataProcessorDescription element)
	{
		this.config = config;
		this.element = element;
	}

	public File getGeneratedFile()
	{
		// TODO check for config.getDeploymentType()
//		return new FlinkSepaCodeGenerator(config, element).createProject();
		return null;
	}
}
