package de.fzi.cep.sepa.storm.sentiment.topology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import de.fzi.cep.sepa.storm.sentiment.controller.SentimentDetectionParameters;
import de.fzi.cep.sepa.storm.topology.SepaSpout;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentDetectionBolt extends BaseRichBolt {

	private static final long serialVersionUID = -3911542682275246545L;

	private String id;
	private StanfordCoreNLP pipeline;
	private OutputCollector collector;
	

	public SentimentDetectionBolt(String id) {
		this.id = id;
	}

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
//		TODO fix prepare
//		super.prepare(map, topologyContext, outputCollector);
		collector = outputCollector;
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}

	@Override
	public void execute(Tuple tuple) {
//		Map<String, Object> event = (Map<String, Object>) tuple.getValueByField("payload");
		 
//		if (parameters != null) {
//			String line = (String) event.get("sentiment");
			String line = (String) tuple.getValue(0);

			int mainSentiment = 0;
			if (line != null && line.length() > 0) {
				int longest = 0;
				Annotation annotation = pipeline.process(line);
				for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
					Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
					int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
					String partText = sentence.toString();
					if (partText.length() > longest) {
						mainSentiment = sentiment;
						longest = partText.length();
					}
				}
			}

			Map<String, Object> event = new HashMap<>();

			event.put("sentiment", mainSentiment);

//			emit(new Values(event));
//		}
		
		collector.emit(SepaSpout.SEPA_DATA_STREAM, new Values(event));
//		collector.ack(tuple);

	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(SepaSpout.SEPA_DATA_STREAM, new Fields("payload"));
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
