package org.apache.streampipes.connect.iiot;

import org.apache.streampipes.connect.iiot.adapters.influxdb.InfluxDbSetAdapter;
import org.apache.streampipes.connect.iiot.adapters.influxdb.InfluxDbStreamAdapter;
import org.apache.streampipes.connect.iiot.adapters.mysql.MySqlSetAdapter;
import org.apache.streampipes.connect.iiot.adapters.mysql.MySqlStreamAdapter;
import org.apache.streampipes.connect.iiot.adapters.netio.NetioMQTTAdapter;
import org.apache.streampipes.connect.iiot.adapters.netio.NetioRestAdapter;
import org.apache.streampipes.connect.iiot.adapters.opcua.OpcUaAdapter;
import org.apache.streampipes.connect.iiot.adapters.plc4x.modbus.Plc4xModbusAdapter;
import org.apache.streampipes.connect.iiot.adapters.plc4x.s7.Plc4xS7Adapter;
import org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataStreamAdapter;
import org.apache.streampipes.connect.container.worker.init.AdapterWorkerContainer;
import org.apache.streampipes.connect.iiot.protocol.set.FileProtocol;
import org.apache.streampipes.connect.iiot.protocol.set.HttpProtocol;
import org.apache.streampipes.connect.iiot.protocol.stream.*;
import org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.container.model.SpServiceDefinitionBuilder;

public class ConnectAdapterIiotInit extends AdapterWorkerContainer{
	public static void main(String[] args) {
		new ConnectAdapterIiotInit().init();
	}

	@Override
	public SpServiceDefinition provideServiceDefinition() {
		return SpServiceDefinitionBuilder.create("connect-worker-main",
						"StreamPipes Connect Worker Main",
						"",8000)
				.registerAdapter(new MySqlStreamAdapter())
				.registerAdapter(new MySqlSetAdapter())
				.registerAdapter(new MachineDataStreamAdapter())
				.registerAdapter(new RosBridgeAdapter())
				.registerAdapter(new OpcUaAdapter())
				.registerAdapter(new InfluxDbStreamAdapter())
				.registerAdapter(new InfluxDbSetAdapter())
                .registerAdapter(new NetioRestAdapter())
                .registerAdapter(new NetioMQTTAdapter())
				.registerAdapter(new Plc4xS7Adapter())
				.registerAdapter(new Plc4xModbusAdapter())
				.registerAdapter(new FileProtocol())
				.registerAdapter(new HttpProtocol())
				.registerAdapter(new FileStreamProtocol())
				.registerAdapter(new KafkaProtocol())
				.registerAdapter(new MqttProtocol())
				.registerAdapter(new HttpStreamProtocol())
				.registerAdapter(new PulsarProtocol())
				.registerAdapter(new HttpServerProtocol())
				.build();
	}
}
