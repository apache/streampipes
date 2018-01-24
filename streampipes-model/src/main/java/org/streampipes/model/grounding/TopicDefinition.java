package org.streampipes.model.grounding;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.TOPIC_DEFINITION)
@Entity
@MappedSuperclass
public abstract class TopicDefinition extends UnnamedStreamPipesEntity {

  @RdfProperty(StreamPipes.HAS_ACTUAL_TOPIC_NAME)
  private String actualTopicName;

  public TopicDefinition() {
    super();
  }

  public TopicDefinition(String actualTopicName) {
    super();
    this.actualTopicName = actualTopicName;
  }

  public TopicDefinition(TopicDefinition other) {
    super(other);
    this.actualTopicName = other.getActualTopicName();
  }

  public String getActualTopicName() {
    return actualTopicName;
  }

  public void setActualTopicName(String actualTopicName) {
    this.actualTopicName = actualTopicName;
  }
}
