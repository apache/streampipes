#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.pe.processor.${packageName};

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class ${classNamePrefix}Parameters extends EventProcessorBindingParams {

  private String exampleText;

  public ${classNamePrefix}Parameters(DataProcessorInvocation graph, String exampleText) {
    super(graph);
    this.exampleText = exampleText;
  }

  public String getExampleText() {
    return exampleText;
  }

}
