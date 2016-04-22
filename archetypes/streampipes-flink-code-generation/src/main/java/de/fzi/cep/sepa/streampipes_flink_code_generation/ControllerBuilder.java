package de.fzi.cep.sepa.streampipes_flink_code_generation;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.flink.api.java.io.TypeSerializerOutputFormat;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class ControllerBuilder extends Builder {

	public ControllerBuilder(SepaDescription sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	/**
	 * 
	 * @param eventStream
	 * @param n
	 *            The number of the stream
	 * @return
	 */
	public String getEventStream(EventStream eventStream, int n) {
		String result = "EventStream stream" + n + " = StreamBuilder.createStream(";		
		
		result += "\"" + eventStream.getName() + "\", ";
		result += "\"" + eventStream.getDescription() + "\", ";
		result += "\"" + eventStream.getUri() + "\")\n";

		result += ".schema(SchemaBuilder.create().properties(eventProperties).build()).build();";

		return result;
	}

	
	public String getAppendOutputStrategy(AppendOutputStrategy aos) {
		String result = "AppendOutputStrategy outputStrategy = new AppendOutputStrategy();\n";
		
		for (EventProperty ep : aos.getEventProperties()) {
			// TODO 
			
		}
		
		return result;
	}
	
	public String getOutputStrategies(List<OutputStrategy> outputStrategies) {
		String result = "List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();\n";
		for (int i = 0; i < outputStrategies.size(); i++) {
			OutputStrategy outputStrategy = outputStrategies.get(i);
			if (outputStrategy instanceof AppendOutputStrategy) {
				result += getAppendOutputStrategy((AppendOutputStrategy) outputStrategy);
			} else {
				// TODO add implementation for the other strategies 
				try {
					throw new Exception("Not yet Implemented");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result += "strategies.add(outputStrategy);\n";
		}
		
		return result;
	}
	
	public String getSupportedGrounding(EventGrounding grounding) {
		return "desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());";
	}

	public String getDeclareModelCode() {
		String result = "SepaDescription desc = new SepaDescription(\"" + sepa.getPathName() + "\", \"" + sepa.getName()
				+ "\", \"" + sepa.getDescription() + "\");\n";
		for (int i = 0; i < sepa.getEventStreams().size(); i++) {
			result += getEventStream(sepa.getEventStreams().get(i), i) +"\n";
			result += "desc.addEventStream(stream" + i + ");\n";
		}

		result += "\n" + getOutputStrategies(sepa.getOutputStrategies());
		result += "desc.setOutputStrategies(strategies);\n\n";
		
		result += getSupportedGrounding(sepa.getSupportedGrounding());
		
		result += "\n\nreturn desc;\n";
		
		return result;
	}

	@Override
	public JavaFile build() {
		MethodSpec declareModel = MethodSpec.methodBuilder("declareModel").addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class).returns(SepaDescription.class).addCode(getDeclareModelCode()).build();

		ClassName parameters = ClassName.get(packageName, name + "Parameters");

		MethodSpec getRuntime = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(SepaInvocation.class, "graph")
				.returns(ParameterizedTypeName.get(JFC.FLINK_SEPA_RUNTIME, parameters))
				.addCode("//TODO\nreturn null;\n").build();


		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(JFC.ABSTRACT_FLINK_AGENT_DECLARER, parameters)).addMethod(declareModel)
				.addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();
	}

	@Override
	public String toString() {
		return build().toString();
	}
}
