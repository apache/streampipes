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

import org.streampipes.codegeneration.utils.Utils;

public class XmlGenerator {
	private String name;
	private String packageName;
	private String version;

	public XmlGenerator(String name, String packageName, String version) {
		this.name = name;
		this.packageName = packageName;
		this.version = version;
	}

	public String getPomFile(boolean standalone) {
		String pom = Utils.readResourceFile("pom");
		pom = pom.replaceAll("####name####", name.toLowerCase());
		pom = pom.replaceAll("####version####", version);
		return pom;
	}
	
	public String getWebXmlFile() {
		String webXml = Utils.readResourceFile("web");
		webXml = webXml.replace("####name####", packageName + ".Init");
		return webXml;
	}
}
