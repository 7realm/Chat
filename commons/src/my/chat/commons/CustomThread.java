package my.chat.commons;

import java.util.concurrent.atomic.AtomicBoolean;

import my.chat.exceptions.ChatException;

@Deprecated
public abstract class CustomThread implements Runnable {
	private final Thread thread;
	private final AtomicBoolean isStopped;

	public CustomThread() {
		thread = new Thread(this);
		isStopped = new AtomicBoolean(true);
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		isStopped.set(true);
	}

	@Override
	public void run() {
		isStopped.set(false);

		while (!isStopped.get()) {
			try {
				execute();
			} catch (ChatException e) {
				// try to handle exception
				if (!handleException(e)) {
					break;
				}
			}
		}

		isStopped.set(true);
	}
	
	

	protected abstract boolean handleException(ChatException e);

	protected abstract void execute() throws ChatException;
}
