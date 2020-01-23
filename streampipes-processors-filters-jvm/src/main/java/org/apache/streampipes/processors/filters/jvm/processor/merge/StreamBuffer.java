/*
Copyright 2020 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.streampipes.processors.filters.jvm.processor.merge;

import org.apache.streampipes.model.runtime.Event;

import java.util.LinkedList;
import java.util.Queue;

public class StreamBuffer {
    private String timestampProperty;
    private Long timeInterval;
    private Queue<Event> buffer;

    public StreamBuffer(String timestampProperty, long timeInterval) {
        buffer = new LinkedList<>();
        this.timestampProperty = timestampProperty;
        this.timeInterval = timeInterval;
    }

    public void add(Event event) {
        buffer.add(event);
    }

    public Event getMatchingEvent(long eventTimestamp) {
        Event result = null;

        Long timestampOfHead = this.getHeadTimestamp();

        // TODO remove all events that are too old

        if (checkTimestamp(eventTimestamp, timestampOfHead)) {
            // search for last event in timewindow
            do {
                result = buffer.remove();

                timestampOfHead = this.getHeadTimestamp();

            } while (checkTimestamp(eventTimestamp, timestampOfHead));

        }

        return result;
    }

    /**
     * Check if timestamp of head is within time window
     * @param eventTimestamp
     * @param timestampOfHead
     * @return
     */
    private boolean checkTimestamp(long eventTimestamp, Long timestampOfHead) {
        if (timestampOfHead == null) {
            return false;
        } else {
            return eventTimestamp < timestampOfHead - timeInterval  || eventTimestamp > timestampOfHead + timeInterval;
        }
    }

    private Long getHeadTimestamp() {
        Event head = this.buffer.peek();
        if (head != null) {
            return head.getFieldBySelector(timestampProperty).getAsPrimitive().getAsLong();
        } else {
            return null;
        }
    }

}
