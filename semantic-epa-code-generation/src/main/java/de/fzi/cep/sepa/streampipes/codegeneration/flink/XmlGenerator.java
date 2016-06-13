package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

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
