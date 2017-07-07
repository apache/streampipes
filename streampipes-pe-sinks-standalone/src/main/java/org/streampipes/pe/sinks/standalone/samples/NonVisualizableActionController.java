package org.streampipes.pe.sinks.standalone.samples;

import org.streampipes.model.impl.graph.SecInvocation;

/**
 * Created by riemer on 12.02.2017.
 */
public abstract class NonVisualizableActionController extends ActionController {

  @Override
  public boolean isVisualizable() {
    return false;
  }

  @Override
  public String getHtml(SecInvocation graph) {
    return null;
  }

}
