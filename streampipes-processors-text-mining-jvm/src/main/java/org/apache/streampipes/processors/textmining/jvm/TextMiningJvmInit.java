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

package org.apache.streampipes.processors.textmining.jvm;

import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.textmining.jvm.config.TextMiningJvmConfig;
import org.apache.streampipes.processors.textmining.jvm.processor.chunker.ChunkerController;
import org.apache.streampipes.processors.textmining.jvm.processor.language.LanguageDetectionController;
import org.apache.streampipes.processors.textmining.jvm.processor.namefinder.NameFinderController;
import org.apache.streampipes.processors.textmining.jvm.processor.partofspeech.PartOfSpeechController;
import org.apache.streampipes.processors.textmining.jvm.processor.sentencedetection.SentenceDetectionController;
import org.apache.streampipes.processors.textmining.jvm.processor.tokenizer.TokenizerController;

public class TextMiningJvmInit extends StandaloneModelSubmitter {
    public static void main(String[] args) {
        DeclarersSingleton.getInstance()
                .add(new LanguageDetectionController())
                .add(new TokenizerController())
                .add(new PartOfSpeechController())
                .add(new ChunkerController())
                .add(new NameFinderController())
                .add(new SentenceDetectionController());

        DeclarersSingleton.getInstance().registerDataFormats(
                new JsonDataFormatFactory(),
                new CborDataFormatFactory(),
                new SmileDataFormatFactory(),
                new FstDataFormatFactory());

        DeclarersSingleton.getInstance().registerProtocols(
                new SpKafkaProtocolFactory(),
                new SpMqttProtocolFactory(),
                new SpJmsProtocolFactory());

        new TextMiningJvmInit().init(TextMiningJvmConfig.INSTANCE);
    }
}
