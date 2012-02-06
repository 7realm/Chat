package my.chat.commons;

import java.util.concurrent.atomic.AtomicBoolean;

import my.chat.exceptions.ChatException;

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
		try {
			while (!isStopped.get()) {
				execute();
			}
		} catch (ChatException e) {
			handleException(e);
			isStopped.set(true);
		}
	}

	protected abstract void handleException(ChatException e);
	
	protected abstract void execute() throws ChatException;
}
