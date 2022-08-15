package org.apache.streampipes.commons.exceptions;

public class SpConfigurationException extends Exception {

  /**
   * Creates a new Exception with the given message and null as the cause.
   *
   * @param message The exception message
   */
  public SpConfigurationException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with a null message and the given cause.
   *
   * @param cause The exception that caused this exception
   */
  public SpConfigurationException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception with the given message and cause
   *
   * @param message The exception message
   * @param cause The exception that caused this exception
   */
  public SpConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
