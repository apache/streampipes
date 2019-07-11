/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.processors.textmining.jvm;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.streampipes.dataformat.fst.FstDataFormatFactory;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.streampipes.processors.textmining.jvm.config.TextMiningJvmConfig;
import org.streampipes.processors.textmining.jvm.processor.chunker.ChunkerController;
import org.streampipes.processors.textmining.jvm.processor.language.LanguageDetectionController;
import org.streampipes.processors.textmining.jvm.processor.namefinder.NameFinderController;
import org.streampipes.processors.textmining.jvm.processor.partofspeech.PartOfSpeechController;
import org.streampipes.processors.textmining.jvm.processor.sentence.SentenceDetectionController;
import org.streampipes.processors.textmining.jvm.processor.tokenizer.TokenizerController;

public class TextMiningJvmInit extends StandaloneModelSubmitter {
    public static void main(String[] args) {
        DeclarersSingleton
                .getInstance()
                .add(new LanguageDetectionController())
                .add(new TokenizerController())
                .add(new PartOfSpeechController())
                .add(new ChunkerController())
                .add(new NameFinderController())
                .add(new SentenceDetectionController());

        DeclarersSingleton.getInstance().registerDataFormats(new JsonDataFormatFactory(),
                new CborDataFormatFactory(),
                new SmileDataFormatFactory(),
                new FstDataFormatFactory());

        DeclarersSingleton.getInstance().registerProtocols(new SpKafkaProtocolFactory(),
                new SpJmsProtocolFactory());

        new TextMiningJvmInit().init(TextMiningJvmConfig.INSTANCE);
    }
}
