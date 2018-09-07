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

package org.streampipes.connect.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;

public abstract class Adapter {
    Logger logger = LoggerFactory.getLogger(Adapter.class);

    @Deprecated
    protected String kafkaUrl;

    @Deprecated
    protected String topic;


    private boolean debug;


    public Adapter(boolean debug) {
        this.debug = debug;
    }

    public Adapter() {
        this(false);
    }

    @Deprecated
    public Adapter(String kafkaUrl, String topic, boolean debug) {
        this.kafkaUrl = kafkaUrl;
        this.topic = topic;
        this.debug = debug;
    }

    @Deprecated
    public Adapter(String kafkaUrl, String topic) {
        this(kafkaUrl, topic, false);
    }

    public abstract AdapterDescription declareModel();

    // Decide which adapter to call
    public abstract void startAdapter() throws AdapterException;

    public abstract void stopAdapter() throws AdapterException;

    public abstract Adapter getInstance(AdapterDescription adapterDescription);

    public abstract GuessSchema getSchema(AdapterDescription adapterDescription) throws AdapterException;

    public abstract String getId();

    public boolean isDebug() {
        return debug;
    }
}
