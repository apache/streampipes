package de.fzi.cep.sepa.streampipes_flink_code_generation;

import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.twitter.bijection.GeneratedTupleCollectionInjections;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;

public class ControllerBuilder extends OldBuilder {

	public ControllerBuilder(SepaDescription sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	public Builder getEventStream(Builder b, EventStream eventStream, int n) {
		b = getEventProperties(b, eventStream.getEventSchema().getEventProperties(), n);
		b.addStatement(
				"$T stream$L = $T.createStream($S, $S, $S).schema($T.create().properties(eventProperties$L).build()).build()",
				JFC.EVENT_STREAM, n, JFC.STREAM_BUILDER, eventStream.getName(), eventStream.getDescription(),
				eventStream.getUri(), JFC.SCHEMA_BUILDER, n);

		return b;
	}

	public Builder getEventProperties(Builder b, List<EventProperty> eventProperties, int n) {
		b.addStatement("$T<$T> eventProperties$L = new $T<$T>()", JFC.LIST, JFC.EVENT_PROPERTY, n, JFC.ARRAY_LIST,
				JFC.EVENT_PROPERTY);

		for (int i = 0; i < eventProperties.size(); i++) {
			// TODO check for type
			b.addStatement("$T e$L = $T.createPropertyRestriction($S).build()", JFC.EVENT_PROPERTY, i,
					JFC.PRIMITIVE_PROPERTY_BUILDER, eventProperties.get(i).getDomainProperties().get(0));
			b.addStatement("eventProperties$L.add(e$L)", n, i);
		}

		return b;
	}

	public Builder getAppendOutputStrategy(Builder b, AppendOutputStrategy aos, int n) {
		b.addStatement("$T outputStrategy$L = new $T()", JFC.APPEND_OUTPUT_STRATEGY, n, JFC.APPEND_OUTPUT_STRATEGY);
		b.addStatement("$T<$T> appendProperties = new $T<$T>()", JFC.LIST, JFC.EVENT_PROPERTY, JFC.ARRAY_LIST,
				JFC.EVENT_PROPERTY);

		for (EventProperty ep : aos.getEventProperties()) {
			// TODO
			b.addStatement("appendProperties.add($T.stringEp($S, $S))", JFC.EP_PROPERTIES, ep.getRuntimeName(),
					ep.getDomainProperties().get(0).toString());
		}

		b.addStatement("outputStrategy$L.setEventProperties(appendProperties)", n);

		return b;
	}

	public Builder getOutputStrategies(Builder b, List<OutputStrategy> outputStrategies) {
		b.addStatement("$T<$T> strategies = new $T<$T>()", JFC.LIST, JFC.OUTPUT_STRATEGY, JFC.ARRAY_LIST,
				JFC.OUTPUT_STRATEGY);
		for (int i = 0; i < outputStrategies.size(); i++) {
			OutputStrategy outputStrategy = outputStrategies.get(i);
			if (outputStrategy instanceof AppendOutputStrategy) {
				b = getAppendOutputStrategy(b, (AppendOutputStrategy) outputStrategy, i);
			} else {
				// TODO add implementation for the other strategies
				try {
					throw new Exception("Not yet Implemented");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			b.addStatement("strategies.add(outputStrategy$L)", i);
		}

		return b;
	}

	public Builder getSupportedGrounding(Builder b, EventGrounding grounding) {
		return b.addStatement("desc.setSupportedGrounding($T.getSupportedGrounding())", JFC.STANDARD_TRANSPORT_FORMAT);
	}

	//	public String getDeclareModelCode() {
//		String result = "SepaDescription desc = new SepaDescription(\"" + sepa.getPathName() + "\", \"" + sepa.getName()
//				+ "\", \"" + sepa.getDescription() + "\");\n";
//		for (int i = 0; i < sepa.getEventStreams().size(); i++) {
//			// TODO
//			// result += getEventStream(sepa.getEventStreams().get(i), i) +
//			// "\n";
//			result += "desc.addEventStream(stream" + i + ");\n";
//		}
//
//		// result += "\n" + getOutputStrategies(sepa.getOutputStrategies());
//		result += "desc.setOutputStrategies(strategies);\n\n";
//
//		// result += getSupportedGrounding(sepa.getSupportedGrounding());
//
//		result += "\n\nreturn desc;\n";
//
//		return result;
//	}

	public Builder getDeclareModelCode(Builder b) {
		b.addStatement("$T desc = new $T($S, $S, $S)", JFC.SEPA_DESCRIPTION, JFC.SEPA_DESCRIPTION, sepa.getPathName(), sepa.getName(), sepa.getDescription());

		for (int i = 0; i < sepa.getEventStreams().size(); i++) {
			// TODO
			b = getEventStream(b, sepa.getEventStreams().get(i), i);
			b.addStatement("desc.addEventStream(stream$L)", i);
		}

		b = getOutputStrategies(b, sepa.getOutputStrategies());
		b.addStatement("desc.setOutputStrategies(strategies)");

		b = getSupportedGrounding(b, sepa.getSupportedGrounding());

		b.addStatement("return desc");

		return b;
	}

	@Override
	public JavaFile build() {
		Builder mB = MethodSpec.methodBuilder("declareModel").addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class).returns(SepaDescription.class);

		MethodSpec declareModel = getDeclareModelCode(mB).build();

		ClassName parameters = ClassName.get(packageName, name + "Parameters");

		MethodSpec getRuntime = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(SepaInvocation.class, "graph")
				.returns(ParameterizedTypeName.get(JFC.FLINK_SEPA_RUNTIME, parameters))
				.addCode("//TODO\nreturn null;\n").build();

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(JFC.ABSTRACT_FLINK_AGENT_DECLARER, parameters))
				.addMethod(declareModel).addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();
	}

	@Override
	public String toString() {
		return build().toString();
	}
}
