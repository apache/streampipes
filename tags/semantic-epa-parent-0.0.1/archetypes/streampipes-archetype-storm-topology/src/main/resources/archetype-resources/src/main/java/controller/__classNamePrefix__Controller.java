package ${package}.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.storm.config.StormConfig;
import de.fzi.cep.sepa.storm.utils.Parameters;
import de.fzi.cep.sepa.storm.utils.Utils;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class ${classNamePrefix}Controller extends EpDeclarer<Parameters>{
	private static String STORM_LOCATION = "FILL_IN STORM_LOCATION";
	private static String JAR_LOCATION = "FILL_IN JAR_LOCATION";
	private static String MAIN_CLASS = "${package}.topology.Main";

	private static String ID;
	private static String REV;


	@Override
	public SepaDescription declareModel() {

		SepaDescription desc = new SepaDescription("FILL_IN PATH_NAME", "FILL_IN NAME OF PROJECT",
				"FILL_IN DESCRIPTION");

		desc.setIconUrl(StormConfig.iconBaseUrl + "FILL_IN ROUTE TO ICON");
		try {

			////////////////////////////////////////////////////////////////////////////
			// Add description of the SEPA 
			////////////////////////////////////////////////////////////////////////////

		} catch (Exception e) {
			e.printStackTrace();
		}
		return desc;
	}


	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		org.lightcouch.Response res = Utils.storeSepaInvocation(invocationGraph);
		ID = res.getId();
		REV = res.getRev();

		Utils.executeCommand(STORM_LOCATION + " jar " + JAR_LOCATION + " " + MAIN_CLASS +" "+ ID +" -c nimbus.host=" + Main.NIMBUS_HOST + " -c nimbus.thift.port=" + Main.NIMBUS_THRIFT_PORT);

		return new Response(ID, true);
	}

	@Override 
	public Response detachRuntime() {
		Utils.executeCommand(STORM_LOCATION + " kill " + Main.TOPOLOGY_NAME +" -c nimbus.host=" + Main.NIMBUS_HOST + " -c nimbus.thift.port=" + Main.NIMBUS_THRIFT_PORT);
		Utils.removeSepaInvocation(ID, REV);

		return new Response(ID, true);
	}
}
