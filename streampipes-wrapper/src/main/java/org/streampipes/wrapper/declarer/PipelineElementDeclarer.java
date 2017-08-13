package org.streampipes.wrapper.declarer;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.Response;
import org.streampipes.sdk.extractor.AbstractParameterExtractor;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.runtime.EventProcessorRuntime;
import org.streampipes.wrapper.runtime.PipelineElement;

import java.util.function.Supplier;

public abstract class PipelineElementDeclarer<B extends BindingParams, EPR extends EventProcessorRuntime, I
        extends InvocableSEPAElement, EX extends AbstractParameterExtractor<I>, PE extends
        PipelineElement<B>> {

  protected EPR epRuntime;
  protected String elementId;

  public Response detachRuntime(String pipelineId) {
    try {
      epRuntime.discardRuntime();
      return new Response(elementId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(elementId, false, e.getMessage());
    }
  }

  protected abstract EX getExtractor(I graph);

  public abstract void invokeEPRuntime(B bindingParameters, Supplier<PE> supplier) throws Exception;

  protected Response submit(B staticParams, Supplier<PE> engine) {
    try {
      invokeEPRuntime(staticParams, engine);
      return new Response(staticParams.getGraph().getElementId(), true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(staticParams.getGraph().getElementId(), false, e.getMessage());
    }
  }

}
