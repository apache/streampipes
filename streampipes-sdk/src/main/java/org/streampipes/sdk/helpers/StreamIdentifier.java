package org.streampipes.sdk.helpers;

public enum StreamIdentifier {

  Stream0(0), Stream1(1);

  private Integer streamId;

  StreamIdentifier(Integer streamId) {
    this.streamId = streamId;
  }

  /**
   * @deprecated Use {@link org.streampipes.sdk.builder.AbstractProcessingElementBuilder} to add stream requirements.
   * @return the stream ID
   */
  public Integer getStreamId() {
    return streamId;
  }
}
