package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import java.io.File;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.streampipes.codegeneration.api.ImplementationCodeGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaControllerGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.DirectoryBuilder;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public abstract class FlinkCodeGenerator extends ImplementationCodeGenerator {
	protected String packageName;
	protected String name;
	protected String version;
	protected String port;
	
	protected String src;
	protected String webInf;

	public FlinkCodeGenerator(DeploymentConfiguration config, ConsumableSEPAElement element) {
		super(config, element);
		packageName = config.getGroupId() + "." + config.getArtifactId();
		name = config.getClassNamePrefix();
		version = "0.0.2-SNAPSHOT";
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
