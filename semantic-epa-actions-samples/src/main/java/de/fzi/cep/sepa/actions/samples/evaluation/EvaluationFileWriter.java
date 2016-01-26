package de.fzi.cep.sepa.actions.samples.evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;

public class EvaluationFileWriter implements Runnable, IMessageListener<byte[]> {

	EvaluationParameters params;
	PrintWriter stream;
	private int counter = 0;
	private JsonParser jsonParser;
	private KafkaConsumerGroup kafkaConsumerGroup;
	private List<ReceivedEvent> input;

	
	private boolean running;
	
	public EvaluationFileWriter(EvaluationParameters params)
	{
		this.params = params;
		jsonParser = new JsonParser();
		this.running = true;
		this.input = new ArrayList<>();
		prepare();
	}
	
	private void prepare()
	{
		File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() +getFilename());
		try {
			stream = new PrintWriter(new FileOutputStream(file), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private String getFilename() {
		Calendar calendar = Calendar.getInstance();
		return "evaluation-" 
				+ClientConfiguration.INSTANCE.getSimulationMaxEvents() 
				+"-" 
				+ClientConfiguration.INSTANCE.getSimulationDelayMs()
				+"-"
				+calendar.get(Calendar.YEAR)
				+"-"
				+calendar.get(Calendar.MONTH)+1
				+"-"
				+calendar.get(Calendar.DAY_OF_MONTH)
				+"-"
				+calendar.get(Calendar.HOUR_OF_DAY)
				+"-"
				+calendar.get(Calendar.MINUTE)
				+".csv";
	}
	
	@Override
	public void run() {
		System.out.println("Starting Kafka Consumer");
		System.out.println(params.getTopic());
		kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), "EvaluationConsumer",
				new String[] {params.getTopic()}, this);
		kafkaConsumerGroup.run(1);
		
	}

	@Override
	public void onEvent(byte[] json) {	
		
		if (!running)
		{
			System.out.println("Stopping");
			kafkaConsumerGroup.shutdown();
			process();
		} else
			input.add(new ReceivedEvent(json, System.currentTimeMillis()));
		
		if (counter % 10000 == 0 || !running) 
		{
			System.out.println(counter + " Event processed."); 
		}
		counter++;
	}
	
	private void process() {
		
		for(ReceivedEvent event : input) {
			long currentTimestamp = event.getTimestamp();
			StringBuilder output = new StringBuilder();
			output.append(counter);
			output.append(",");
			JsonObject jsonObj = jsonParser.parse(new String(event.getByteMsg())).getAsJsonObject();
			for(Entry<String, JsonElement> element : jsonObj.entrySet())
			{
				output.append(element.getValue());
				output.append(",");
			}
			output.append(currentTimestamp);
			output.append(",");
			output.append(currentTimestamp - jsonObj.get(params.getTimestampProperty()).getAsLong());
			output.append(System.lineSeparator());
			
			stream.write(output.toString());
		}
		
		stream.flush();
		stream.close();
	}
	
	

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
		onEvent(null);
	}
	
	public static void main(String[] args) {
		System.out.println(new EvaluationFileWriter(null).getFilename());
	}
	
}
