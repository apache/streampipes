package org.streampipes.pe.processors.esper.writer;

import com.espertech.esper.client.EventBean;
import com.google.gson.Gson;
import org.streampipes.pe.processors.esper.debs.c1.CellData;
import org.streampipes.pe.processors.esper.debs.c1.CellDataComparator;
import org.streampipes.pe.processors.esper.debs.c1.DebsOutput;
import org.streampipes.pe.processors.esper.debs.c1.DebsOutputParameters;
import org.streampipes.pe.processors.esper.debs.c1.OutputType;
import org.streampipes.pe.processors.esper.debs.c1.StatusEvent;
import org.streampipes.pe.processors.esper.debs.c1.TaxiDataInputProvider;
import org.streampipes.pe.processors.esper.debs.c2.CellDataBest;
import org.streampipes.pe.processors.esper.debs.c2.DebsOutputC2;
import org.streampipes.wrapper.esper.writer.Writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Challenge1FileWriter implements Writer {
	
	DebsOutputParameters params;
	FileOutputStream stream;
	BufferedWriter writer;
	
	//StringBuilder builderC1;
	//StringBuilder builderC2;
	PrintWriter writerc1;
	PrintWriter writerc2;
	PrintWriter statisticsWriter;
	PrintWriter statisticsWriterc1;
	PrintWriter statisticsWriterc2;
	
	File file;
	File file2;
	
	int counterC1 = 1;
	int counterC2 = 1;
	long c = 0;
	long unique = 0;
	long startTime;
	
	double delayC1 = 0;
	double delayC2 = 0;
	double overallDelay = 0;
	
//	long previousDateTimeSumC1 = 0;
	long previousDateTimeSumC2 = 0;
	
	private List<CellData> previousCellDataC1;
	private List<CellDataBest> previousCellDataC2;

	final OutputType outputType;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	BufferedWriter out;
	Gson gson = new Gson();
	
	static final double NANO_TO_MILLISECONDS = 1.0 / 1000000;
	
	public Challenge1FileWriter(DebsOutputParameters params, OutputType outputType)
	{
		this.params = params;
		this.outputType = outputType;
		prepare();
	}
	
	private void prepare()
	{
		file = new File("query1-" +System.currentTimeMillis() +".csv");
		file2 = new File("query2-" +System.currentTimeMillis() +".csv");
		File statistics = new File("statistics-" +System.currentTimeMillis() +".csv");
		File statisticsC1 = new File("statistics-c1-" +System.currentTimeMillis() +".csv");
		File statisticsC2 = new File("statistics-c2-" +System.currentTimeMillis() +".csv");
		//builderC1 = new StringBuilder();
		//builderC2 = new StringBuilder();
		
		
		try {
			writerc1 = new PrintWriter(new FileOutputStream(file), true);
			writerc2 = new PrintWriter(new FileOutputStream(file2), true);
			statisticsWriter = new PrintWriter(new FileOutputStream(statistics), true);
			statisticsWriter.write("nanoTime,counter,unique,counterC1,counterC2,totalDelay" +System.lineSeparator());
			statisticsWriterc1 = new PrintWriter(new FileOutputStream(statisticsC1), true);
			statisticsWriterc2 = new PrintWriter(new FileOutputStream(statisticsC2), true);
			statisticsWriterc1.write("nanoTime,counter,delay,totalDelayQuery,totalDelay" +System.lineSeparator());
			statisticsWriterc2.write("nanoTime,counter,delay,totalDelayQuery,totalDelay" +System.lineSeparator());
			out = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 512);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startTime = 0;
	}

	@Override
	public void onEvent(EventBean bean) {	
	
	
		Object object = bean.getUnderlying();
		
		if (object instanceof java.util.HashMap)
		{
			
			c++;
			Map<String, Object> map = (Map<String, Object>) object;
			
			if (map.containsKey(TaxiDataInputProvider.LIST))
			{
				Map<String, Object>[] list = (Map<String, Object>[]) map.get(TaxiDataInputProvider.LIST);
				
				//int generalCounter = (int) map.get("generalCounter");
				int generalCounter = 0;
				if (list != null && list.length > 0)
				{
					try {
						String output = makeDebsOutput(list, generalCounter);
						
						if (outputType == OutputType.PERSIST) 
							{
								//System.out.println(output);
								writerc1.write(output);
							}
						else if (outputType == OutputType.PRINT)
							{
								out.write(output);
							}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else 
				{
					// TODO: write null values
				}		
			}
			else if (map.containsKey(TaxiDataInputProvider.BEST_CELLS))
			{
				Map<String, Object>[] bestCells = (Map<String, Object>[]) map.get(TaxiDataInputProvider.BEST_CELLS);
				if (bestCells != null)
				{
					if (bestCells.length > 0)
					{
						try {
							String output = makeDebsOutputC2(bestCells);
							if (outputType == OutputType.PERSIST) writerc2.write(output);//builderC2.append(output);
							else if (outputType == OutputType.PRINT)
							{
								out.write(output);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			writeStatistics();
		}
		else if (object instanceof StatusEvent)
		{
			System.out.println("Status event received");
			StatusEvent statusEvent = (StatusEvent) object;
			if (!statusEvent.isStart())
			{
				System.out.println("Status Event received - Finished Processing");
				System.out.println("Performance Metrics: Query 1 + Query 2\n");
				System.out.println("StartTime: " +startTime);
				long endTime = statusEvent.getTimestamp();
				System.out.println("EndTime: " +endTime);
				
				long duration = endTime-startTime;
				String formattedDuration = String.format("%d min, %d sec", 
					    TimeUnit.NANOSECONDS.toMinutes(duration),
					    TimeUnit.NANOSECONDS.toSeconds(duration) - 
					    TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(duration))
					);
				
				double avgDelay = overallDelay / c;
				double avgDelayC1 = delayC1 / counterC1;
				double avgDelayC2 = delayC2 / counterC2;
			
				System.out.println("Total execution time (ns): " +duration);
				System.out.println("Total execution time (ms): " +TimeUnit.NANOSECONDS.toMillis(duration));
				System.out.println("Total execution time: " +formattedDuration);
				System.out.println("Total events produced: " +statusEvent.getEventCount());
				System.out.println("Total events received: " +c);
				System.out.println("Query 1 total results (written to file): " +counterC1);
				System.out.println("Query 2 total results (written to file): " +counterC2);
				System.out.println("Total unique events received: " +unique);
				System.out.println("Delay Query 1 (sum ms): " +delayC1);
				System.out.println("Delay Query 1 (avg ms): " +(avgDelayC1));
				System.out.println("Delay Query 2(sum ms): " +delayC2);
				System.out.println("Delay Query 2(avg ms): " +(avgDelayC2));
				System.out.println("Delay (sum ms): " +overallDelay);
				System.out.println("Delay (avg ms): " +(avgDelay));
				
				writerc1.close();
				writerc2.close();
				statisticsWriter.close();
				statisticsWriterc1.close();
				statisticsWriterc2.close();
				
			}
			else
			{
				System.out.println("Status Event received - Processing has started at time " +statusEvent.getTimestamp());
				startTime = statusEvent.getTimestamp();
			}
		}
	}
	
	

	private String makeDebsOutputC2(Map<String, Object>[] bestCells) {
		DebsOutputC2 output = prepareListDebsOutputC2(bestCells);
		StringBuilder sb = new StringBuilder();
		boolean newValue = isNew(output);
		if (newValue)
		{	
			counterC2++;
			sb.append(sdf.format(new Date(output.getPickup_time())) +", ");
			sb.append(sdf.format(new Date(output.getDropoff_time())) +", ");
			
			for(int i = 0; i < output.getCellData().size(); i++)
			{
				CellDataBest thisCell = output.getCellData().get(i);
				sb.append(thisCell.getCellId() +", ");
				sb.append(thisCell.getEmptyTaxis() +", ");
				sb.append(thisCell.getMedianProfit() +", ");
				sb.append(thisCell.getProfitability() +", ");
			}
			
			if (output.getCellData().size() <= 10)
			{
				int restToWrite = 10 - output.getCellData().size();
				for(int i = 0; i < restToWrite; i++)
				{
					sb.append("NaN, NaN, NaN, NaN, ");
				}
			}
			double thisDelay = output.getDelay();
			//sb.append(TimeUnit.NANOSECONDS.toMillis(thisDelay) +";");
			sb.append(thisDelay +";");
			//sb.append(c +", ");
			//sb.append(counterC2);
			sb.append(System.lineSeparator());
			unique++;
			overallDelay = overallDelay + thisDelay;
			delayC2 = delayC2 + thisDelay;
			
			writeStatisticsPerQuery(statisticsWriterc2, System.nanoTime(), counterC2, thisDelay, delayC2, overallDelay);
		}
		return sb.toString();
	}

	private DebsOutputC2 prepareListDebsOutputC2(Map<String, Object>[] item) {
		List<CellDataBest> cellDataset = new ArrayList<>();
		DebsOutputC2 debsOutput = new DebsOutputC2();
		
		for(int i = 0; i < item.length; i++)
		{
			
			Map<String, Object> obj = item[i];
			String cellId = obj.get(TaxiDataInputProvider.CELLX) +TaxiDataInputProvider.DOT +obj.get(TaxiDataInputProvider.CELLY);
			final long emptyTaxis = (long) obj.get(TaxiDataInputProvider.EMPTY_TAXIS); 
			final double profitability = (double) obj.get(TaxiDataInputProvider.PROFITABILITY); 
			final double medianProfit = (double) obj.get(TaxiDataInputProvider.MEDIAN_PROFIT); 
			final long readTime = (long) obj.get(TaxiDataInputProvider.READ_DATETIME);
			final long pickupTime = (long) obj.get(TaxiDataInputProvider.PICKUP_DATETIME);
			final long dropoffTime = (long) obj.get(TaxiDataInputProvider.DROPOFF_DATETIME);
			
			cellDataset.add(new CellDataBest(cellId, emptyTaxis, medianProfit, profitability, readTime, pickupTime, dropoffTime));
		}
		
		CellDataBest recentData = findMaxBest(cellDataset);
		long currentTime = System.nanoTime();
		debsOutput.setDelay((currentTime - recentData.getReadDatetime())  / 1000000.0);
		debsOutput.setDropoff_time(recentData.getDropoff_datetime());
		debsOutput.setPickup_time(recentData.getPickup_datetime());
	
		Collections.sort(cellDataset, Collections.reverseOrder());
		debsOutput.setCellData(cellDataset);
	
		return debsOutput;
	}

	private String makeDebsOutput(Map<String, Object>[] list, long counter) throws IOException
	{
		
		DebsOutput output = prepareListDebsOutput(list);
		StringBuilder sb = new StringBuilder();
		boolean newValue = isNew(output);
		if (newValue)
		{	
			counterC1 ++;
			sb.append(sdf.format(new Date(output.getPickup_time())) +", ");
			sb.append(sdf.format(new Date(output.getDropoff_time())) +", ");
			
			for(int i = 0; i < output.getCellData().size(); i++)
			{
				CellData thisCell = output.getCellData().get(i);
				sb.append(thisCell.getStartCellId() +", ");
				sb.append(thisCell.getEndCellId() +", ");
				//sb.append(thisCell.getCount() +", ");
			}
			if (output.getCellData().size() <= 10)
			{
				int restToWrite = 10 - output.getCellData().size();
				for(int i = 0; i < restToWrite; i++)
				{
					sb.append("NaN, NaN, NaN, NaN, ");
				}
			}
			double thisDelay = output.getDelay();
			//sb.append(TimeUnit.NANOSECONDS.toMillis(thisDelay) +", ");
			sb.append(thisDelay +", ");
			//sb.append(c +", ");
			//sb.append(counter);
			sb.append(System.lineSeparator());
			unique++;
			overallDelay = overallDelay + thisDelay;
			delayC1 = delayC1 + thisDelay;
			
			writeStatisticsPerQuery(statisticsWriterc1, System.nanoTime(), counterC1, thisDelay, delayC1, overallDelay);
		}
		
		return sb.toString();
	}	

	private DebsOutput prepareListDebsOutput(Map<String, Object>[] item)
	{
		List<CellData> cellDataset = new ArrayList<>();
		DebsOutput debsOutput = new DebsOutput();
		
		
		for(int i = 0; i < item.length; i++)
		{
			Map<String, Object> obj = item[i];
			
			String startCellId = obj.get("cellXPickup") +TaxiDataInputProvider.DOT +obj.get("cellYPickup");
			String endCellId = obj.get("cellXDropoff") +TaxiDataInputProvider.DOT +obj.get("cellYDropoff");
			
			//String startCellId = extractCellId(TaxiDataInputProvider.CELLOPTIONS, TaxiDataInputProvider.CELLX, TaxiDataInputProvider.CELLY, obj);
			//String endCellId = extractCellId(TaxiDataInputProvider.CELLOPTIONS_1, TaxiDataInputProvider.CELLX, TaxiDataInputProvider.CELLY, obj);
			long count = (long) obj.get(TaxiDataInputProvider.COUNT_VALUE); 
			long readTime = (long) obj.get(TaxiDataInputProvider.READ_DATETIME);
			long pickupTime = (long) obj.get(TaxiDataInputProvider.PICKUP_DATETIME);
			long dropoffTime = (long) obj.get(TaxiDataInputProvider.DROPOFF_DATETIME);
			
			
			cellDataset.add(new CellData(startCellId, endCellId, count, readTime, pickupTime, dropoffTime));
		}
		
		CellData recentData = findMax(cellDataset);
		long currentTime = System.nanoTime();
		debsOutput.setDelay((currentTime - recentData.getReadDatetime()) / 1000000.0);
		debsOutput.setDropoff_time(recentData.getDropoff_datetime());
		debsOutput.setPickup_time(recentData.getPickup_datetime());
	
		Collections.sort(cellDataset, new CellDataComparator());
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

	private CellDataBest findMaxBest(List<CellDataBest> cellDataset) {
		long max = 0;
		CellDataBest result = null;
		for(CellDataBest cellData : cellDataset)
		{
			if (cellData.getReadDatetime() > max) 
				{
					result = cellData;
					max = cellData.getReadDatetime();
				}
		}
		return result;
	}
	
	private boolean isNew(DebsOutput output) {
		boolean newValue = false;
		if (previousCellDataC1 == null) 
			newValue = true;
		else if (output.getCellData().size() != previousCellDataC1.size()) 
				newValue = true;
		else {
			for(int i = 0; i < output.getCellData().size(); i++)
			{
				if (!
					(
						(output.getCellData().get(i).getStartCellId().equals(previousCellDataC1.get(i).getStartCellId()))
						&& 
						(output.getCellData().get(i).getEndCellId().equals(previousCellDataC1.get(i).getEndCellId()))
					)
					) 
					newValue = true;;
			}
		}
		this.previousCellDataC1 = output.getCellData();
		return newValue;
	}
	
	private boolean isNew(DebsOutputC2 output) {
		boolean newValue = false;
		if (previousCellDataC2 == null) 
			newValue = true;
		else if (output.getCellData().size() != previousCellDataC2.size()) 
				newValue = true;
		else {
			for(int i = 0; i < output.getCellData().size(); i++)
			{
				if (!
						(output.getCellData().get(i).getCellId().equals(previousCellDataC2.get(i).getCellId()))
					) 
					newValue = true;;
			}
		}
		this.previousCellDataC2 = output.getCellData();
		return newValue;
	}
	
	private void writeStatistics() {
		StringBuilder builder = new StringBuilder();
		builder.append(System.nanoTime() +", ");
		builder.append(c +", ");
		builder.append(unique +", ");
		builder.append(counterC1 +", ");
		builder.append(counterC2 +", ");
		builder.append(overallDelay);
		builder.append(System.lineSeparator());
		statisticsWriter.write(builder.toString());
	}
	
	private void writeStatisticsPerQuery(PrintWriter writer,
			long nanoTime, int counterC22, double thisDelay, double delayC22,
			double overallDelay2) {
		StringBuilder builder = new StringBuilder();
		builder.append(nanoTime +", ");
		builder.append(counterC22 +", ");
		builder.append(thisDelay +", ");
		builder.append(delayC22  +", ");
		builder.append(overallDelay2);
		builder.append(System.lineSeparator());
		writer.write(builder.toString());
		
	}
}

