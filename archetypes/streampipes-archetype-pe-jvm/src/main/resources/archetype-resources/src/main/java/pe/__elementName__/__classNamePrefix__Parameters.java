#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.pe.${elementName};

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class ${classNamePrefix}Parameters extends EventSinkBindingParams {

  private String exampleText;

  public ${classNamePrefix}Parameters(DataSinkInvocation graph, String exampleText) {
    super(graph);
    this.exampleText = exampleText;
  }

  public String getExampleText() {
    return exampleText;
  }

}
