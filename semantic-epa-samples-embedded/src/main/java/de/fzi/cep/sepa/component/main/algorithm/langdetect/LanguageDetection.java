package de.fzi.cep.sepa.component.main.algorithm.langdetect;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

import java.util.HashMap;
import java.util.Map;

public class LanguageDetection implements EPEngine<LanguageDetectionParameters>{

	private OutputCollector collector;
	private Map<String, String> mappingPropertyNames;
	private String outputPropertyName;
	
	
	public LanguageDetection() {
		 try {
			DetectorFactory.loadProfile("C:\\workspace\\semantic-epa-parent\\semantic-epa-algorithms-samples\\src\\main\\resources\\profiles");
			
			mappingPropertyNames = new HashMap<>();
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void bind(EngineParameters<LanguageDetectionParameters> parameters,
			OutputCollector collector, SepaInvocation graph) {
		mappingPropertyNames.put(parameters.getStaticProperty().getInputStreamParams().get(0).getInName(), parameters.getStaticProperty().getMappingPropertyName());
		this.outputPropertyName = "language";
		this.collector = collector;
		
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		String mappingPropertyName = mappingPropertyNames.get(sourceInfo);
		String fieldValue = (String) event.get(mappingPropertyName);
		event.put(outputPropertyName, detectLanguage(fieldValue));
		collector.send(event);
	}
	
	private String detectLanguage(String text)
	{
		try {
			Detector detector = DetectorFactory.create();
			detector.append(text);
			return detector.detect();
		} catch (LangDetectException e) {
			return "unknown";
		}
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

}
