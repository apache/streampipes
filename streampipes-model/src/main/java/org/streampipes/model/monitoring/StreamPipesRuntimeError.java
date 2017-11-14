package org.streampipes.model.monitoring;

/**
 * Created by riemer on 30.01.2017.
 */
public class StreamPipesRuntimeError {

  private long timestamp;
  private String title;
  private String message;

  private String stackTrace;

  public StreamPipesRuntimeError(long timestamp, String title, String message, String stackTrace) {
    this.timestamp = timestamp;
    this.title = title;
    this.message = message;
    this.stackTrace = stackTrace;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public String getStackTrace() {
    return stackTrace;
  }
}
