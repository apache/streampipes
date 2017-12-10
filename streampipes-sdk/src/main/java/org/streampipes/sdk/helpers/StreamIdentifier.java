package org.streampipes.sdk.helpers;

public enum StreamIdentifier {

  Stream0(0), Stream1(1);

  private Integer streamId;

  StreamIdentifier(Integer streamId) {
    this.streamId = streamId;
  }

  public Integer getStreamId() {
    return streamId;
  }
}
