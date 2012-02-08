package my.chat.network;

public interface ExceptionHandler {
	/**
	 * This method should try to handle exception.
	 * <p>
	 * If exception is handled the normal work can continue.
	 * <p>
	 * If exception cannot be handled, then closing stuff should be done automatically.
	 * 
	 * @param e the exception that will be handled
	 * @return true if exception was handled, false otherwise
	 */
	boolean canHandle(Exception e);
}