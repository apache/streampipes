package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.EPEngine;
import org.streampipes.wrapper.declarer.EpDeclarer;
import org.streampipes.wrapper.params.BindingParameters;
import org.streampipes.wrapper.params.EngineParameters;
import org.streampipes.wrapper.standalone.FlatEPRuntime;
import org.streampipes.wrapper.standalone.param.FlatRuntimeParameters;

import java.util.function.Supplier;

public abstract class FlatEpDeclarer<B extends BindingParameters> extends EpDeclarer<B, FlatEPRuntime> {

  @Override
  public void preDetach() throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public FlatEPRuntime prepareRuntime(B bindingParameters,
                                      Supplier<EPEngine<B>> supplier, EngineParameters<B> engineParams) {

    SepaInvocation sepa = bindingParameters.getGraph();


    FlatRuntimeParameters<B> runtimeParams = new FlatRuntimeParameters<>(sepa.getUri(), supplier, engineParams);
    FlatEPRuntime epRuntime = new FlatEPRuntime(runtimeParams);

    return epRuntime;
  }


}
