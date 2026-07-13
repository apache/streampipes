package org.apache.streampipes.extensions.management.init;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.EventCollector;

public record RunningAdapterInstance(StreamPipesAdapter adapter,
                                     EventCollector eventCollector) {
}

