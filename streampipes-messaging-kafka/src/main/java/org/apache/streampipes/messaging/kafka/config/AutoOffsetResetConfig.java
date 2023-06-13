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

package org.apache.streampipes.messaging.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class AutoOffsetResetConfig implements KafkaConfigAppender {

    private String autoOffsetResetConfig;

    @Override
    public void appendConfig(Properties props) {
        if (autoOffsetResetConfig != null) {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig);
        }
    }

    public AutoOffsetResetConfig(String autoOffsetResetConfig) {
        this.autoOffsetResetConfig = autoOffsetResetConfig;
    }

    public AutoOffsetResetConfig() {

    }


    public String getAutoOffsetResetConfig() {
        return autoOffsetResetConfig;
    }

    public void setAutoOffsetResetConfig(String autoOffsetResetConfig) {
        this.autoOffsetResetConfig = autoOffsetResetConfig;
    }
}
