package de.fzi.cep.sepa.algorithm.languagedetection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public class LanguageDetection implements EPEngine<LanguageDetectionParameters>{

	private static final String PROFILE_FOLDER = "./profiles";
	
	private OutputCollector collector;
	private Map<String, String> mappingPropertyNames;
	private String outputPropertyName;
	
	
	public LanguageDetection() {
		 try {
			DetectorFactory.loadProfile(getProfiles());
			mappingPropertyNames = new HashMap<>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getProfiles() throws IOException {
		
		List<String> languages = new ArrayList<>();
		
		ClassLoader loader = LanguageDetection.class.getClassLoader();
        InputStream in = loader.getResourceAsStream(PROFILE_FOLDER);
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = rdr.readLine()) != null) {
        	URL fileUrl = Resources.getResource("./profiles/" +line);
        	languages.add(Resources.toString(fileUrl, Charsets.UTF_8));
        }
        rdr.close();
        
        return languages;
	}
	
	
	@Override
	public void bind(EngineParameters<LanguageDetectionParameters> parameters,
			OutputCollector collector, SepaInvocation graph) {
		mappingPropertyNames.put(parameters
				.getStaticProperty()
				.getInputStreamParams().get(0)
				.getInName(), 
				parameters
				.getStaticProperty()
				.getMappingPropertyName());
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
