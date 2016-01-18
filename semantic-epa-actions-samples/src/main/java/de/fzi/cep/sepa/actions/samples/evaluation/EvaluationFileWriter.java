package de.fzi.cep.sepa.actions.samples.evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;

public class EvaluationFileWriter implements Runnable, IMessageListener<byte[]> {

	EvaluationParameters params;
	PrintWriter stream;
	static long counter = 0;
	private JsonParser jsonParser;
	private KafkaConsumerGroup kafkaConsumerGroup;
	
	private boolean running;
	
	public EvaluationFileWriter(EvaluationParameters params)
	{
		this.params = params;
		jsonParser = new JsonParser();
		this.running = true;
		prepare();
	}
	
	private void prepare()
	{
		File file = new File(params.getPath());
		try {
			stream = new PrintWriter(new FileOutputStream(file), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
		
		if (counter % 10 == 0) System.out.println(counter + " Event processed."); 
		counter++;
	
		if (running)
		{
			
			long currentTimestamp = System.currentTimeMillis();
			StringBuilder output = new StringBuilder();
			JsonObject jsonObj = jsonParser.parse(new String(json)).getAsJsonObject();
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
		else
		{
			System.out.println("Stopping");
			stream.flush();
			stream.close();
			kafkaConsumerGroup.shutdown();
		}
		
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
		onEvent(null);
	}
	
}
