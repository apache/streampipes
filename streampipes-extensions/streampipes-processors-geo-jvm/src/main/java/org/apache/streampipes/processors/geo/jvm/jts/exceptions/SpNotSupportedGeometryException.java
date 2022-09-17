package org.apache.streampipes.processors.geo.jvm.jts.exceptions;

public class SpNotSupportedGeometryException extends Exception {

  public SpNotSupportedGeometryException() {
  }

  public SpNotSupportedGeometryException(String message) {
    super(message);
  }

  public SpNotSupportedGeometryException(String message, Throwable cause) {
    super(message, cause);
  }

  public SpNotSupportedGeometryException(Throwable cause) {
    super(cause);
  }

  public SpNotSupportedGeometryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}