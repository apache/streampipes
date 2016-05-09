package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import java.io.File;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.streampipes.codegeneration.CodeGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.DirectoryBuilder;

public abstract class FlinkCodeGenerator extends CodeGenerator {
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
		version = "0.0.1-SNAPSHOT";
		port = Integer.toString(config.getPort());
		
		src = getTempDir() + "src" + File.separator + "main" + File.separator + "java" + File.separator;
		webInf = getTempDir() + "WebContent" + File.separator + "WEB-INF" + File.separator;

	}

	@Override
	protected void createDirectoryStructure() {
		String r = getTempDir(); 
		String dirs[] = {r + "target/", src, r + "src/main/resources/", r + "src/test/", webInf};

		boolean success = DirectoryBuilder.createDirectories(dirs);

		if (!success) {
			try {
				throw new Exception("Couldn't create folder structure");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
