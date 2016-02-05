package de.fzi.cep.sepa.actions.samples.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.samples.barchart.BarChartController;
import de.fzi.cep.sepa.actions.samples.charts.LineChartController;
import de.fzi.cep.sepa.actions.samples.couchdb.CouchDbController;
import de.fzi.cep.sepa.actions.samples.debs.DebsOutputController;
import de.fzi.cep.sepa.actions.samples.evaluation.EvaluationController;
import de.fzi.cep.sepa.actions.samples.file.FileController;
import de.fzi.cep.sepa.actions.samples.gauge.GaugeController;
import de.fzi.cep.sepa.actions.samples.heatmap.HeatmapController;
import de.fzi.cep.sepa.actions.samples.jms.JMSConsumer;
import de.fzi.cep.sepa.actions.samples.kafka.KafkaController;
import de.fzi.cep.sepa.actions.samples.maparea.MapAreaController;
import de.fzi.cep.sepa.actions.samples.maps.MapsController;
import de.fzi.cep.sepa.actions.samples.notification.NotificationController;
import de.fzi.cep.sepa.actions.samples.number.NumberController;
import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseTopologyController;
import de.fzi.cep.sepa.actions.samples.proasense.kpi.ProaSenseKpiController;
import de.fzi.cep.sepa.actions.samples.route.RouteController;
import de.fzi.cep.sepa.actions.samples.table.MultiRowTableController;
import de.fzi.cep.sepa.actions.samples.table.TableViewController;
import de.fzi.cep.sepa.actions.samples.verticalbar.VerticalBarController;
import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;

public class Init implements Runnable {

	public static void main(String[] args) throws Exception
	{
		new Init().declare();
	}
	
	public void declare() {
		
		List<SemanticEventConsumerDeclarer> consumers = new ArrayList<>();
		
		consumers.add(new JMSConsumer());
		consumers.add(new LineChartController());
		consumers.add(new MapsController());
		consumers.add(new TableViewController());
		consumers.add(new FileController());
		consumers.add(new MultiRowTableController());
		consumers.add(new DebsOutputController());
		consumers.add(new HeatmapController());
		consumers.add(new ProaSenseTopologyController());
		consumers.add(new GaugeController());
		consumers.add(new RouteController());
		consumers.add(new BarChartController());
		consumers.add(new MapAreaController());
		consumers.add(new de.fzi.cep.sepa.actions.samples.maparealist.MapAreaController());
		consumers.add(new ProaSenseKpiController());
		consumers.add(new NotificationController());
		consumers.add(new KafkaController());
		consumers.add(new EvaluationController());
		consumers.add(new CouchDbController());
                consumers.add(new NumberController());
                consumers.add(new VerticalBarController());
		//consumers.add(new HistogramController());
		
		try {
			ModelSubmitter.submitConsumer(consumers);
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
