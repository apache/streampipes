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

package org.streampipes.codegeneration.flink.sec;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.codegeneration.flink.ConfigGenerator;
import org.streampipes.codegeneration.flink.FlinkCodeGenerator;
import org.streampipes.codegeneration.flink.XmlGenerator;
import org.streampipes.codegeneration.utils.Utils;

public class FlinkSecCodeGenerator extends FlinkCodeGenerator {

	public FlinkSecCodeGenerator(DeploymentConfiguration config, DataSinkDescription element) {
		super(config, element);
	}

	@Override
	protected void create() {
		createDirectoryStructure();

		// source files
		Utils.writeToFile(new FlinkSecControllerGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new FlinkSecProgramGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new ConfigGenerator(element, name, packageName).build(), src);

		// xml files
		XmlGenerator xmlGenerator = new XmlGenerator(name, packageName, version);
		Utils.writeToFile(xmlGenerator.getPomFile(true), getTempDir() + "pom.xml");
		Utils.writeToFile(xmlGenerator.getWebXmlFile(), webInf + "web.xml");

	}

}
