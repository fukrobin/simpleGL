package org.robin.gl;

/**
 * @author fkrobin
 */
public class ShaderException extends RuntimeException {


  private static final long serialVersionUID = 4709680040980468733L;

  /**
   * Construct a new ShaderException with no other information.
   */
  public ShaderException() {
    super();
  }


  /**
   * Construct a new ShaderException for the specified message.
   *
   * @param message Message describing this exception
   */
  public ShaderException(String message) {
    super(message);
  }


  /**
   * Construct a new ShaderException for the specified throwable.
   *
   * @param throwable Throwable that caused this exception
   */
  public ShaderException(Throwable throwable) {
    super(throwable);
  }


  /**
   * Construct a new ShaderException for the specified message and throwable.
   *
   * @param message   Message describing this exception
   * @param throwable Throwable that caused this exception
   */
  public ShaderException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
