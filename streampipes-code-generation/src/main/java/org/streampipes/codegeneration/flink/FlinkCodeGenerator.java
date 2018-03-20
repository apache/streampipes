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

package org.streampipes.codegeneration.flink;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.codegeneration.api.ImplementationCodeGenerator;
import org.streampipes.codegeneration.flink.sepa.FlinkSepaControllerGenerator;
import org.streampipes.codegeneration.utils.DirectoryBuilder;
import org.streampipes.codegeneration.utils.JFC;

import java.io.File;

public abstract class FlinkCodeGenerator extends ImplementationCodeGenerator {
	protected String packageName;
	protected String name;
	protected String version;
	protected String port;
	
	protected String src;
	protected String webInf;

	public FlinkCodeGenerator(DeploymentConfiguration config, ConsumableStreamPipesEntity element) {
		super(config, element);
		packageName = config.getGroupId() + "." + config.getArtifactId();
		name = config.getClassNamePrefix();
		version = "0.40.3-SNAPSHOT";
		port = Integer.toString(config.getPort());
		
		src = getTempDir() + "src" + File.separator + "main" + File.separator + "java" + File.separator;
		webInf = getTempDir() + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "WEB-INF" + File.separator;

	}

	@Override
	protected void createDirectoryStructure() {
		String r = getTempDir(); 
		String dirs[] = {r + "target/", src, r + "src/api/resources/", r + "src/test/", webInf};

		boolean success = DirectoryBuilder.createDirectories(dirs);

		if (!success) {
			try {
				throw new Exception("Couldn't create folder structure");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getDeclareModel() {
		return new FlinkSepaControllerGenerator(element, name, packageName).getDeclareModelCode(JFC.SEPA_DESCRIPTION).build().toString();
	}

}
