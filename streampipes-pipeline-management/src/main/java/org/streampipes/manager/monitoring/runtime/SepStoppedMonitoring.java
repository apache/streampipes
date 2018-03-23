/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.monitoring.runtime;

import org.json.JSONObject;
import org.streampipes.commons.exceptions.NoMatchingFormatException;
import org.streampipes.commons.exceptions.NoMatchingProtocolException;
import org.streampipes.commons.exceptions.NoMatchingSchemaException;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.manager.operations.Operations;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.kafka.SpKafkaConsumer;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.storage.couchdb.impl.PipelineStorageImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *  
 */
public class SepStoppedMonitoring implements EpRuntimeMonitoring<DataSourceDescription>, Runnable {

	private Map<String, List<PipelineObserver>> streamToObserver;
	private Map<String, Pipeline> streamToStoppedMonitoringPipeline;
	private SpKafkaConsumer kafkaConsumerGroup;

	@Override
	public boolean register(PipelineObserver observer) {

		try {
			Pipeline pipeline = new PipelineStorageImpl().getPipeline(observer.getPipelineId());

			List<SpDataStream> allStreams = new ArrayList<>();
			pipeline.getStreams().forEach((s) -> allStreams.add(s));

			for (SpDataStream s : allStreams) {
				if (streamToObserver.get(s.getElementId()) == null) {
					List<PipelineObserver> po = new ArrayList<>();
					po.add(observer);

					// TODO fix this s.getSourceId() is always null
					String streamId = s.getElementId();
					String sourceId = streamId.substring(0, streamId.lastIndexOf("/"));

					streamToObserver.put(streamId, po);
					Pipeline p = new SepStoppedMonitoringPipelineBuilder(sourceId, streamId).buildPipeline();
					Operations.startPipeline(p, false, false, false);
					streamToStoppedMonitoringPipeline.put(streamId, p);

				} else {
					streamToObserver.get(s.getElementId()).add(observer);
				}
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NoMatchingFormatException e) {
			e.printStackTrace();
		} catch (NoMatchingSchemaException e) {
			e.printStackTrace();
		} catch (NoMatchingProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean remove(PipelineObserver observer) {

		Pipeline pipeline = new PipelineStorageImpl().getPipeline(observer.getPipelineId());
		List<SpDataStream> streams = pipeline.getStreams();

		for (SpDataStream sc : streams) {
			String streamId = sc.getElementId();
			List<PipelineObserver> po = streamToObserver.get(streamId);
			if (po.size() == 1) {
				streamToObserver.remove(streamId);
				
				Operations.stopPipeline(streamToStoppedMonitoringPipeline.get(streamId), false, false, false);
				streamToStoppedMonitoringPipeline.remove(streamId);
			} else {
				po.remove(observer);
			}

		}

		return false;
	}

	private class KafkaCallback implements InternalEventProcessor<byte[]> {

		@Override
		public void onEvent(byte[] payload) {
			String str = new String(payload, StandardCharsets.UTF_8);
			JSONObject jo = new JSONObject(str);

			List<PipelineObserver> listPos = streamToObserver.get(jo.get("topic"));
			for (PipelineObserver po : listPos) {
				po.update();
			}
			
			
		}

	}

	@Override
	public void run() {
		streamToObserver = new HashMap<>();
		streamToStoppedMonitoringPipeline = new HashMap<>();

		String topic = "internal.streamepipes.sec.stopped";

		kafkaConsumerGroup = new SpKafkaConsumer(BackendConfig.INSTANCE.getKafkaUrl(), topic,
				new KafkaCallback());
		Thread thread = new Thread(kafkaConsumerGroup);
		thread.start();

	}

	public static void main(String[] args) throws IOException {
		SepStoppedMonitoring monitoring = new SepStoppedMonitoring();
		monitoring.run();

		// Montrac 01
		String id = "baaaf5b2-5412-4ac1-a7eb-04aeaf0e12b8";
		PipelineObserver observer1 = new PipelineObserver(id);
		
		// Montrac 02
		id = "ef915142-2a08-4166-8bea-8d946ae31cd6";
		PipelineObserver observer2 = new PipelineObserver(id);

		// Random Number Stream
		id = "b3c0b6ad-05df-4670-a078-83775eeb550b";
		PipelineObserver observer3 = new PipelineObserver(id);
				
		monitoring.register(observer1);
		monitoring.register(observer2);
		monitoring.register(observer3);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		monitoring.remove(observer1);
		monitoring.remove(observer2);
		monitoring.remove(observer3);
		System.out.println("laalal");
	}
}
