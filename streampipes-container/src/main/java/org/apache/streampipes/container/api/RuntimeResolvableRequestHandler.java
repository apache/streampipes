package org.apache.streampipes.container.api;

import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.SelectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import java.util.List;

public class RuntimeResolvableRequestHandler {

  // for backwards compatibility
  public RuntimeOptionsResponse handleRuntimeResponse(ResolvesContainerProvidedOptions resolvesOptions,
                                                      RuntimeOptionsRequest req) {
    List<Option> availableOptions =
            resolvesOptions.resolveOptions(req.getRequestId(),
                    makeExtractor(req));

    SelectionStaticProperty sp = getConfiguredProperty(req);
    sp.setOptions(availableOptions);

    return new RuntimeOptionsResponse(req, sp);
  }

  public RuntimeOptionsResponse handleRuntimeResponse(SupportsRuntimeConfig declarer,
                                                      RuntimeOptionsRequest req) {
    StaticProperty result = declarer.resolveConfiguration(
            req.getRequestId(),
            makeExtractor(req));

    return new RuntimeOptionsResponse(req, result);
  }

  private SelectionStaticProperty getConfiguredProperty(RuntimeOptionsRequest req) {
    return req.getStaticProperties()
            .stream()
            .filter(p -> p.getInternalName().equals(req.getRequestId()))
            .map(p -> (SelectionStaticProperty) p)
            .findFirst()
            .get();
  }

  private StaticPropertyExtractor makeExtractor(RuntimeOptionsRequest req) {
    return StaticPropertyExtractor.from(req.getStaticProperties(),
            req.getInputStreams(),
            req.getAppId());
  }
}
