#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.pe.notification;

import ${groupId}.model.graph.DataSinkInvocation;
import ${groupId}.wrapper.params.binding.EventSinkBindingParams;

public class NotificationParameters extends EventSinkBindingParams {

  private String title;
  private String content;

  public NotificationParameters(DataSinkInvocation graph, String title, String content) {
    super(graph);
    this.title = title;
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }
}
