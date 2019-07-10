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

package org.streampipes.connect.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.util.PollingSettings;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;

import java.util.concurrent.*;

public abstract class PullAdapter extends SpecificDataStreamAdapter {

    protected static Logger logger = LoggerFactory.getLogger(PullAdapter.class);
    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService errorThreadscheduler;


    public PullAdapter() {
        super();
    }

    public PullAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);
    }

    protected abstract void pullData();

    protected abstract PollingSettings getPollingInterval();

    @Override
    public void startAdapter() throws AdapterException {

        final Runnable errorThread = () -> {
            executeAdpaterLogic();
        };

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(errorThread, 0, TimeUnit.MILLISECONDS);

    }

    private void executeAdpaterLogic() {
        final Runnable task = () -> {

            pullData();

        };

        scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(task, 1,
                getPollingInterval().getValue(), getPollingInterval().getTimeUnit());

        try {
            handle.get();
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error", e);
        }
    }

    @Override
    public void stopAdapter() throws AdapterException {
        scheduler.shutdownNow();
    }
}
