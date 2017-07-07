package de.fzi.cep.sepa.actions.samples.debs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.fzi.cep.sepa.actions.messaging.jms.ActiveMQConsumer;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;

public class FileWriter implements Runnable, IMessageListener<String> {

	DebsParameters params;
	FileOutputStream stream;
	BufferedWriter writer;
	StringBuilder builder;
	JsonParser parser;
	File file;
	long previousDateTimeSum = 0;
	static int c = 1;
	static int counter = 1;
	
	public FileWriter(DebsParameters params)
	{
		this.params = params;
		prepare();
	}
	
	private void prepare()
	{
		file = new File(params.getPath());
		parser = new JsonParser();
		builder = new StringBuilder();
		
	}
	
	@Override
	public void run() {
		ActiveMQConsumer consumer = new ActiveMQConsumer(params.getUrl(), params.getTopic());
		consumer.setListener(this);
	}

	@Override
	public void onEvent(String json) {
		counter ++;
		
		try {
		if (counter % 100 == 0) 
			{
				FileUtils.writeStringToFile(file, builder.toString(), true);
				builder = new StringBuilder();
			}
		
		JsonElement jsonElement = parser.parse(json);
		JsonElement list = jsonElement.getAsJsonObject().get("list");
		long counter = jsonElement.getAsJsonObject().get("generalCounter").getAsLong();
		if (list.isJsonArray())
			builder.append(makeDebsOutput(list, counter));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String makeDebsOutput(JsonElement jsonElement, long counter) throws IOException
	{
		JsonArray arr = jsonElement.getAsJsonArray();
		
		DebsOutput output = prepareListDebsOutput(arr);
		StringBuilder sb = new StringBuilder();
		boolean newValue = isNew(output);
		if (newValue)
		{	
			
			sb.append("pickup_datetime=" +output.getPickup_time() +", ");
			sb.append("dropoff_datetime=" +output.getDropoff_time() +", ");
			
			for(int i = 0; i < output.getCellData().size(); i++)
			{
				CellData thisCell = output.getCellData().get(i);
				sb.append("start_cell_id_" +(i+1) +"=" +thisCell.getStartCellId() +", ");
				sb.append("end_cell_id_" +(i+1) +"=" +thisCell.getEndCellId() +", ");
				sb.append("count_value_id_" +(i+1) +"=" +thisCell.getCount() +", ");
			}
			sb.append("delay=" +output.getDelay() +", ");
			sb.append("order=" +c);
			sb.append(", generalCounter=" +counter);
			c++;
			sb.append(System.lineSeparator() +System.lineSeparator());
		
		}
		return sb.toString();
	}
	
	private boolean isNew(DebsOutput output) {
		long dateTimeSum = 0;
		for(CellData cd : output.getCellData())
			{
				dateTimeSum += cd.getReadDatetime();
			}
		if (dateTimeSum == previousDateTimeSum)
			return false;
		else 
			{
				previousDateTimeSum = dateTimeSum;
				return true;
			}
	}

	private DebsOutput prepareListDebsOutput(JsonArray jsonArr)
	{
		List<CellData> cellDataset = new ArrayList<>();
		DebsOutput debsOutput = new DebsOutput();
		long currentTime = System.currentTimeMillis();
		
		for(int i = 0; i < jsonArr.size(); i++)
		{
			JsonElement obj = jsonArr.get(i);
			
			String startCellId = extractCellId("cellOptions", "cellX", "cellY", obj);
			String endCellId = extractCellId("cellOptions1", "cellX", "cellY", obj);
			int count = obj.getAsJsonObject().get("countValue").getAsInt(); 
			long readTime = obj.getAsJsonObject().get("read_datetime").getAsLong();
			long pickupTime = obj.getAsJsonObject().get("pickup_datetime").getAsLong();
			long dropoffTime = obj.getAsJsonObject().get("dropoff_datetime").getAsLong();
			
			
			cellDataset.add(new CellData(startCellId, endCellId, count, readTime, pickupTime, dropoffTime));
		}
		
		CellData recentData = findMax(cellDataset);
		
		debsOutput.setDelay(currentTime - recentData.getReadDatetime());
		debsOutput.setDropoff_time(recentData.getDropoff_datetime());
		debsOutput.setPickup_time(recentData.getPickup_datetime());
	
		Collections.sort(cellDataset, Collections.reverseOrder());
		debsOutput.setCellData(cellDataset);
	
		return debsOutput;
	}
	
	private CellData findMax(List<CellData> cellDataset) {
		long max = 0;
		CellData result = null;
		for(CellData cellData : cellDataset)
		{
			if (cellData.getReadDatetime() > max) 
				{
					result = cellData;
					max = cellData.getReadDatetime();
				}
		}
		return result;
	}

	private String extractCellId(String objectName, String fieldXName, String fieldYName, JsonElement obj)
	{
		return obj.getAsJsonObject().get(objectName).getAsJsonObject().get(fieldXName).getAsString() 
		+"."
		+ obj.getAsJsonObject().get(objectName).getAsJsonObject().get(fieldYName).getAsString();
	}
	
	
	private Map<String, String> prepareDebsOutput(JsonArray jsonArr)
	{
		Map<String, String> result = new LinkedHashMap<String, String>();
		for(int i = 0; i < jsonArr.size(); i++)
		{
			JsonElement obj = jsonArr.get(i);
			if (i == 0) 
				{
					long readTime = obj.getAsJsonObject().get("read_datetime").getAsLong();
					long currentTime = System.currentTimeMillis();
					result.put("newDelay", String.valueOf(currentTime-readTime));
				}
			//result.put("pickup_datetime", obj.getAsJsonObject().get("pickup_datetime").getAsString());
			//result.put("dropoff_datetime", obj.getAsJsonObject().get("dropoff_datetime").getAsString());
			result.put("start_cell_id_" +(i+1), obj.getAsJsonObject().get("cellOptions").getAsJsonObject().get("cellX").getAsString() 
					+"."
					+ obj.getAsJsonObject().get("cellOptions").getAsJsonObject().get("cellY").getAsString()
					);
			result.put("end_cell_id_" +(i+1), obj.getAsJsonObject().get("cellOptions1").getAsJsonObject().get("cellX").getAsString() 
					+"."
					+ obj.getAsJsonObject().get("cellOptions1").getAsJsonObject().get("cellY") 
					);
			result.put("countValue_route_" +(i+1), obj.getAsJsonObject().get("countValue").getAsString()  
					);
		}
		
		return result;
	}

}

