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

package org.streampipes.connect.adapter.preprocessing.elements;

import org.streampipes.connect.adapter.model.pipeline.AdapterPipelineElement;
import org.streampipes.connect.adapter.preprocessing.Util;


import java.util.List;
import java.util.Map;

public class SendToKafkaReplayAdapterSink implements AdapterPipelineElement {

    private SendToKafkaAdapterSink sendToKafkaAdapterSink;
    private long lastEventTimestamp;
    private List<String> timestampKeys;
    private boolean replaceTimestamp;


    public SendToKafkaReplayAdapterSink(SendToKafkaAdapterSink sendToKafkaAdapterSink,
                                        String timestampKey, boolean replaceTimestamp) {
        this.sendToKafkaAdapterSink = sendToKafkaAdapterSink;
        this.lastEventTimestamp = -1;
        this.timestampKeys = Util.toKeyArray(timestampKey);
        this.replaceTimestamp = replaceTimestamp;
    }

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        if ((event != null) && (lastEventTimestamp != -1)) {
            long actualEventTimestamp = getTimestampInEvent(event);
            try {
                Thread.sleep(actualEventTimestamp - lastEventTimestamp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (replaceTimestamp) {
                setTimestampInEvent(event, System.currentTimeMillis());
            }
            lastEventTimestamp = actualEventTimestamp;

        } else if (lastEventTimestamp == -1) {
            lastEventTimestamp = getTimestampInEvent(event);
            if (replaceTimestamp) {
                setTimestampInEvent(event, System.currentTimeMillis());
            }
        }
        return sendToKafkaAdapterSink.process(event);
    }

    private long getTimestampInEvent(Map<String, Object> event) {
        if (timestampKeys.size() == 1) {
            return (long) event.get(timestampKeys.get(0));
        }
        Map<String, Object> subEvent = event;
        for (int i = 0; i < timestampKeys.size() - 1; i++) {
            subEvent = (Map<String, Object>) subEvent.get(timestampKeys.get(i));
        }
        return (long) subEvent.get(timestampKeys.get(timestampKeys.size() - 1));

    }

    private void setTimestampInEvent(Map<String, Object> event, long timestamp) {
        if (timestampKeys.size() == 1) {
            event.put(timestampKeys.get(0), timestamp);
        } else {
            Map<String, Object> subEvent = event;
            for (int i = 0; i < timestampKeys.size() - 1; i++) {
                subEvent = (Map<String, Object>) subEvent.get(timestampKeys.get(i));
            }
            subEvent.put(timestampKeys.get(timestampKeys.size() - 1), timestamp);
        }
    }
}
