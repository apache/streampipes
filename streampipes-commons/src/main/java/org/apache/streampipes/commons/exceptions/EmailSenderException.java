package org.apache.streampipes.commons.exceptions;

public class EmailSenderException extends SpException{
  public EmailSenderException(String message) {
    super(message);
  }

  public EmailSenderException(Throwable cause) {
    super(cause);
  }

  public EmailSenderException(String message, Throwable cause) {
    super(message, cause);
  }
}
