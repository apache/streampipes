package de.fzi.cep.sepa.storm.sentiment.topology;

import java.util.Map;
import java.util.Properties;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Values;
import de.fzi.cep.sepa.storm.sentiment.controller.SentimentDetectionParameters;
import de.fzi.cep.sepa.storm.topology.FunctionalSepaBolt;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
//import edu.stanford.nlp.trees.Tree;
//import edu.stanford.nlp.util.CoreMap;

public class SentimentDetectionBolt extends FunctionalSepaBolt<SentimentDetectionParameters> {

	private static final long serialVersionUID = -3911542682275246545L;

//	private StanfordCoreNLP pipeline;

	public SentimentDetectionBolt(String id) {
		super(id);
	}

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//		pipeline = new StanfordCoreNLP(props);
	}

	@Override
	protected void performEventAction(Map<String, Object> event, SentimentDetectionParameters parameters,
			String configurationId) {
		if (parameters != null) {
			String line = (String) event.get(parameters.getSentimentMapsTo());

			int mainSentiment = 0;
			if (line != null && line.length() > 0) {
				int longest = 0;
//				Annotation annotation = pipeline.process(line);
//				for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
//					Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
//					int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//					String partText = sentence.toString();
//					if (partText.length() > longest) {
//						mainSentiment = sentiment;
//						longest = partText.length();
//					}
//				}
			}

			event.put("sentiment", mainSentiment);

			emit(new Values(event));
		}
	}

}
