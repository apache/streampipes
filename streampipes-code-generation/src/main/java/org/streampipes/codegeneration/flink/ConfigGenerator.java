package org.streampipes.codegeneration.flink;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.streampipes.model.ConsumableSEPAElement;
import org.streampipes.codegeneration.Generator;
import org.streampipes.codegeneration.utils.JFC;

import javax.lang.model.element.Modifier;

public class ConfigGenerator extends Generator {

	public ConfigGenerator(ConsumableSEPAElement sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	@Override
	public JavaFile build() {
		FieldSpec jar = FieldSpec.builder(JFC.STRING, "JAR_FILE")
			    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
			    .initializer("$S" , "./" + name + "-0.40.3-SNAPSHOT.jar")
			    .build();

		FieldSpec flinkHost = FieldSpec.builder(JFC.STRING, "FLINK_HOST")
			    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
			    .initializer("$T.INSTANCE.getFlinkHost()", JFC.CLIENT_CONFIGURATION)
			    .build();

		FieldSpec flinkPort = FieldSpec.builder(int.class, "FLINK_PORT")
			    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
			    .initializer("$T.INSTANCE.getFlinkPort()", JFC.CLIENT_CONFIGURATION)
			    .build();

		TypeSpec parameterClass = TypeSpec.classBuilder("Config").addModifiers(Modifier.PUBLIC)
				.addField(jar).addField(flinkHost).addField(flinkPort).build();

		return JavaFile.builder(packageName, parameterClass).build();
	}

}
