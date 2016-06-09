package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.*;
import com.squareup.javapoet.MethodSpec.Builder;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.streampipes.codegeneration.Generator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public class InitGenerator extends Generator {

	public InitGenerator(ConsumableSEPAElement sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	@Override
	public JavaFile build() {
		List<ClassName> controllers = new ArrayList<ClassName>();
		controllers.add(ClassName.get("", name + "Controller"));

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Init").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.CONTAINER_MODEL_SUBMITTER).addMethod(getInit(controllers))
				.build();
		return JavaFile.builder(packageName, controllerClass).build();
	}

	private MethodSpec getInit(List<ClassName> controllers) {
		Builder b = MethodSpec.methodBuilder("init").addAnnotation(Override.class).returns(TypeName.VOID)
				.addModifiers(Modifier.PUBLIC);
		b.addStatement("$T.getInstance().setRoute($S)", JFC.DECLARERS_SINGLETON, name.toLowerCase());
		for (ClassName cn : controllers) {
			b.addStatement("$T.getInstance().add(new $S())", JFC.DECLARERS_SINGLETON, cn);
		}

		return b.build();
	}

}
