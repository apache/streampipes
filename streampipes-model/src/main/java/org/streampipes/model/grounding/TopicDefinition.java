package org.streampipes.model.grounding;

import org.streampipes.model.base.UnnamedStreamPipesEntity;

public abstract class TopicDefinition extends UnnamedStreamPipesEntity {

  public TopicDefinition() {
    super();
  }

  public TopicDefinition(UnnamedStreamPipesEntity other, String topicName) {
    super(other);
  }

  public TopicDefinition(String elementName, String topicName) {
    super(elementName);
  }

}
