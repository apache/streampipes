/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.connect.api.EmitBinaryEvent;
import org.apache.streampipes.connect.api.IAdapterPipeline;
import org.apache.streampipes.connect.api.IFormat;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;

import java.util.Date;
import java.util.Map;

public class SendToPipeline implements EmitBinaryEvent {

    private IFormat format;

    private SpKafkaProducer producer;
    private ObjectMapper objectMapper;

    // ====== For performance tests
    private int delmeCounter;
    private long startTime;
    // =============================


    private IAdapterPipeline adapterPipeline;

    @Deprecated
    // TODO remove
    public SendToPipeline(IFormat format, String brokerUrl, String topic) {
        this.format = format;

        producer = new SpKafkaProducer(brokerUrl, topic);
        objectMapper = new ObjectMapper();

        // ====== For performance tests
        this.delmeCounter = 0;
        this.startTime = new Date().getTime();
        // =============================
    }

    public SendToPipeline(IFormat format, IAdapterPipeline adapterPipeline) {
       this.format = format;
       this.adapterPipeline = adapterPipeline;

        this.delmeCounter = 0;
        this.startTime = new Date().getTime();
    }

    @Override
    public Boolean emit(byte[] event) {

        // ====== For performance tests
        this.delmeCounter = this.delmeCounter + 1;
        if (this.delmeCounter % 200000 == 0) {
            System.out.println("Processed: " + this.delmeCounter);
            long diff = new Date().getTime() - this.startTime;
            System.out.println("Time: " + diff);
        }
        // =============================

        Map<String, Object> result = format.parse(event);

        if (result != null) {
            adapterPipeline.process(result);
        }
        return true;
    }
}
