package de.fzi.cep.sepa.streampipes_flink_code_generation;

import com.squareup.javapoet.JavaFile;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public class XmlGenerator {
	private String name;
	private String packageName;

	public XmlGenerator(String name, String packageName) {
		this.name = name;
		this.packageName = packageName;
	}

	public String getPomFile() {
		String pom = Utils.readResourceFile("pom");
		pom = pom.replaceAll("####name####", name.toLowerCase());
		return pom;
	}
	
	public String getWebXmlFile() {
		String webXml = Utils.readResourceFile("web");
		webXml = webXml.replace("####name####", packageName + ".Init");
		return webXml;
	}
}
