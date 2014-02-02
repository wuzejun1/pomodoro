package org.app4j.tool.pomodoro;

public class TickThread extends Thread {
	final Pomodoro pomodoro;
	volatile boolean stopped;

	public TickThread(Pomodoro pomodoro) {
		this.pomodoro = pomodoro;
		setDaemon(true);
	}

	@Override
	public void run() {
		while (!stopped) {
			pomodoro.tick();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public void stopTicking() {
		stopped = true;
	}
}
