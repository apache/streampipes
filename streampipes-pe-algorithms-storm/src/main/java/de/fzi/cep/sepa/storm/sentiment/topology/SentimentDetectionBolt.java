package de.fzi.cep.sepa.storm.sentiment.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import backtype.storm.spout.Scheme;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.storm.utils.StormUtils;
import de.fzi.cep.sepa.storm.utils.Utils;
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
	private String sentimentField;
	private Scheme scheme;
	private List<String> newFields;
	private List<String> oldFields;

	public SentimentDetectionBolt(String id, EventStream eventStream) {
		this.id = id;
		this.scheme = StormUtils.getScheme(eventStream);
		newFields = scheme.getOutputFields().toList();
		newFields.add("sentiment");
		oldFields = scheme.getOutputFields().toList();

	}

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		collector = outputCollector;
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
		sentimentField = (String) map.get("sentiment.param1");
	}

	@Override
	public void execute(Tuple tuple) {

		String line = tuple.getStringByField(sentimentField);
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

		// first get the values from the previous event and append the value for the sentiment
		List<Object> values = new ArrayList<>();
		for (String s : oldFields) {
			values.add(tuple.getValueByField(s));
		}
		values.add(mainSentiment);
		

		collector.emit(Utils.SEPA_DATA_STREAM, new Values(values.toArray()));

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(Utils.SEPA_DATA_STREAM, new Fields(newFields));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
