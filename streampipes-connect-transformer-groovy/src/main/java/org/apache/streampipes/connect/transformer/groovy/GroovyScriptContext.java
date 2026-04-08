package org.apache.streampipes.connect.transformer.groovy;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.connect.transformer.api.Context;

public record GroovyScriptContext(IStreamPipesClient client) implements Context {
}
