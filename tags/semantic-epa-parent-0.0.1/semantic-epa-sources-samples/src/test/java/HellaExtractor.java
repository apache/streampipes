import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Optional;

import de.fzi.cep.sepa.sources.samples.util.Utils;


public class HellaExtractor {

	public static final String directory = "E:\\hella-data\\hella-1week\\week-dummy-scrap\\Montrac_2015-09-17";
	public static final String fileType = "evt";
	public static final String filenamePrefix = "20150917-";

			
	public static final long endPrefix = 235959;
	
	public static void main(String[] args) throws FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(directory +File.separator +filenamePrefix +".csv")), true);
		
		
		for(int i = 0; i < endPrefix; i++)
		{
			for(int j = 1; j < 5; j++)
			{
				Optional<BufferedReader> readerOpt = Utils.getReader(buildFile(i, j));
				if (readerOpt.isPresent())
				{
					try {
						BufferedReader br = readerOpt.get();			
						String line;
						
						while ((line = br.readLine()) != null) {
							line = line.replace("LeftPiece=", "");
							line = line.replace("RightPiece=", "");
							writer.write(line +System.lineSeparator());
						}
					} catch (Exception e)
					{
						//e.printStackTrace();
					}
				}
				else
				{
					System.out.println("skipping: " +i +", " +j);
				}
			}
		}
		writer.close();
	}
	
	public static File buildFile(int i, int j)
	{
		final String fileFormat = String.format("%06d", i);
		return new File(directory + File.separator +filenamePrefix +fileFormat +"-" +j +"." +fileType);
	}
}
