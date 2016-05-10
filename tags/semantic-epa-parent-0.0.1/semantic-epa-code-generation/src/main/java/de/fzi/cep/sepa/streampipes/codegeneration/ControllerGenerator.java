package de.fzi.cep.sepa.streampipes.codegeneration;

import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public abstract class ControllerGenerator extends Generator {
	public ControllerGenerator(ConsumableSEPAElement element, String name, String packageName) {
		super(element, name, packageName);
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

	public Builder getDeclareModelCode(ClassName desc) {
		Builder b = MethodSpec.methodBuilder("declareModel").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
				.returns(desc);

		b.addStatement("$T desc = new $T($S, $S, $S)", desc, desc, element.getUri(),
				element.getName(), element.getDescription());

		for (int i = 0; i < element.getEventStreams().size(); i++) {
			// TODO
			b = getEventStream(b, element.getEventStreams().get(i), i);
			b.addStatement("desc.addEventStream(stream$L)", i);
		}

		//TODO find better solution
		if (element instanceof SepaDescription) {
			b = getOutputStrategies(b, ((SepaDescription) element).getOutputStrategies());
			b.addStatement("desc.setOutputStrategies(strategies)");
		}

		b = getSupportedGrounding(b, element.getSupportedGrounding());

		b.addStatement("return desc");

		return b;
	}
}