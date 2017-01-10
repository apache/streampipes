package de.fzi.cep.sepa.actions.samples.main;

import de.fzi.cep.sepa.actions.alarm.AlarmLightController;
import de.fzi.cep.sepa.actions.dashboard.DashboardController;
import de.fzi.cep.sepa.actions.samples.couchdb.CouchDbController;
import de.fzi.cep.sepa.actions.samples.debs.DebsOutputController;
import de.fzi.cep.sepa.actions.samples.evaluation.EvaluationController;
import de.fzi.cep.sepa.actions.samples.jms.JMSConsumer;
import de.fzi.cep.sepa.actions.samples.kafka.KafkaController;
import de.fzi.cep.sepa.actions.samples.notification.NotificationController;
import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseTopologyController;
import de.fzi.cep.sepa.actions.samples.proasense.kpi.ProaSenseKpiController;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;

public class ActionsInit extends StandaloneModelSubmitter {

    public static void main(String[] args) throws Exception
	{
        DeclarersSingleton.getInstance()

		.add(new JMSConsumer())
		//.add(new MapsController())
				
		//.add(new FileController())
		//.add(new MultiRowTableController())
		.add(new DebsOutputController())
		//.add(new HeatmapController())
		.add(new ProaSenseTopologyController())
		//.add(new GaugeController())
		//.add(new RouteController())
		//.add(new BarChartController())
		//.add(new MapAreaController())
		//.add(new de.fzi.cep.sepa.actions.samples.maparealist.MapAreaController())
		.add(new ProaSenseKpiController())
		.add(new NotificationController())
		.add(new KafkaController())
		.add(new EvaluationController())
		.add(new CouchDbController())
        //.add(new NumberController())
        //.add(new VerticalBarController())
		.add(new DashboardController())
        .add(new AlarmLightController());
		//.add(new HistogramController())

        DeclarersSingleton.getInstance().setPort(8091);

        new ActionsInit().init();

	}


}
