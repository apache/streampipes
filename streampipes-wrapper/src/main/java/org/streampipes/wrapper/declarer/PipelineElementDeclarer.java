package org.streampipes.wrapper.declarer;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.sdk.extractor.AbstractParameterExtractor;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.runtime.PipelineElement;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

import java.util.function.Supplier;

public abstract class PipelineElementDeclarer<B extends BindingParams, EPR extends
        PipelineElementRuntime, I
        extends InvocableSEPAElement, EX extends AbstractParameterExtractor<I>, PE extends
        PipelineElement<B>> {

  protected EPR epRuntime;
  protected String elementId;

  public void invokeEPRuntime(B bindingParameters, Supplier<PE> supplier) throws Exception {

    elementId = bindingParameters.getGraph().getElementId();

    epRuntime = prepareRuntime(bindingParameters, supplier);
    epRuntime.bindRuntime();
  }

  public Response detachRuntime(String pipelineId) {
    try {
      epRuntime.discardRuntime();
      return new Response(elementId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(elementId, false, e.getMessage());
    }
  }

  protected Response submit(B staticParams, Supplier<PE> engine) {
    try {
      invokeEPRuntime(staticParams, engine);
      return new Response(staticParams.getGraph().getElementId(), true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(staticParams.getGraph().getElementId(), false, e.getMessage());
    }
  }

  protected abstract EX getExtractor(I graph);

  public abstract EPR prepareRuntime(B bindingParameters, Supplier<PE> supplier);




}
