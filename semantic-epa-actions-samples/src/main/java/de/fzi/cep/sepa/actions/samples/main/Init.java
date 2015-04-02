package de.fzi.cep.sepa.actions.samples.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.samples.charts.ChartConsumer;
import de.fzi.cep.sepa.actions.samples.debs.DebsOutputController;
import de.fzi.cep.sepa.actions.samples.file.FileController;
import de.fzi.cep.sepa.actions.samples.heatmap.HeatmapController;
import de.fzi.cep.sepa.actions.samples.jms.JMSConsumer;
import de.fzi.cep.sepa.actions.samples.maps.MapsController;
import de.fzi.cep.sepa.actions.samples.table.MultiRowTableController;
import de.fzi.cep.sepa.actions.samples.table.TableViewController;
import de.fzi.cep.sepa.commons.Configuration;
import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.SemanticEventConsumerDeclarer;

public class Init implements Runnable {

	public static void main(String[] args) throws Exception
	{
		new Init().declare();
	}
	
	public void declare() {
		
		List<SemanticEventConsumerDeclarer> consumers = new ArrayList<>();
		
		consumers.add(new JMSConsumer());
		consumers.add(new ChartConsumer());
		consumers.add(new MapsController());
		consumers.add(new TableViewController());
		consumers.add(new FileController());
		consumers.add(new MultiRowTableController());
		consumers.add(new DebsOutputController());
		consumers.add(new HeatmapController());
		
		try {
			ModelSubmitter.submitConsumer(consumers, Configuration.ACTION_BASE_URL, 8091);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		declare();
	}
}
