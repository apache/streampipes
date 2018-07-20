/*
Copyright 2018 FZI Forschungszentrum Informatik

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

package org.streampipes.connect.firstconnector.protocol.stream;

import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.pipeline.AdapterPipeline;
import org.streampipes.connect.firstconnector.protocol.Protocol;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class PullProtocoll extends Protocol {

    private ScheduledExecutorService scheduler;

    private long interval;


    public PullProtocoll() {
    }

    public PullProtocoll(Parser parser, Format format, long interval) {
        super(parser, format);
        this.interval = interval;
    }

    @Override
    public void run(AdapterPipeline adapterPipeline) {
        final Runnable task = () -> {
            SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
            InputStream data = getDataFromEndpoint();

            parser.parse(data, stk);
        };

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(task, 1, interval, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
    }

    abstract InputStream getDataFromEndpoint();
}
