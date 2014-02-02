package org.app4j.tool.pomodoro.util;

import org.junit.Test;

/**
 * @author chi
 */
public class WavClipTest {
	@Test
	public void play() throws InterruptedException {
		WavClip wavClip = new WavClip("/resources/ticktock.wav");
		wavClip.play();
		Thread.sleep(12 * 1000);
		wavClip.stop();
		wavClip.play();
		Thread.sleep(10 * 1000);
	}

	@Test
	public void fadeout() throws InterruptedException {
		WavClip wavClip = new WavClip("/resources/ticktock.wav");
		wavClip.fadeOut(10);
		Thread.sleep(10 * 1000);
	}
}
