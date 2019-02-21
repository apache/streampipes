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

package org.streampipes.connect.adapter.generic.protocol.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.generic.protocol.Protocol;

import java.io.InputStream;
import java.util.concurrent.*;

public abstract class PullProtocol extends Protocol {

    private ScheduledExecutorService scheduler;

    private Logger logger = LoggerFactory.getLogger(PullProtocol.class);

    private long interval;


    public PullProtocol() {
    }

    public PullProtocol(Parser parser, Format format, long interval) {
        super(parser, format);
        this.interval = interval;
    }

    @Override
    public void run(AdapterPipeline adapterPipeline) {
        final Runnable errorThread = () -> {
            executeProtocolLogic(adapterPipeline);
        };


        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(errorThread, 0, TimeUnit.MILLISECONDS);

    }


    private void executeProtocolLogic(AdapterPipeline adapterPipeline) {
         final Runnable task = () -> {

            format.reset();
            SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
            InputStream data = getDataFromEndpoint();

            if(data != null) {
                parser.parse(data, stk);
            } else {
                logger.warn("Could not receive data from Endpoint. Try again in " + interval + " seconds.");
            }
        };

        scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(task, 1, interval, TimeUnit.SECONDS);
        try {
            handle.get();
        } catch (ExecutionException e ) {
            logger.error("Error", e);
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
    }

    abstract InputStream getDataFromEndpoint();
}
