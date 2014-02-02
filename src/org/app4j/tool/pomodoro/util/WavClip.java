package org.app4j.tool.pomodoro.util;

import com.google.common.io.ByteStreams;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author chi
 */
public class WavClip {
	Clip clip;
	byte[] content;

	public WavClip(String resource) {
		try {
			content = ByteStreams.toByteArray(getClass().getResourceAsStream(resource));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void play() {
		try {
			if (clip != null) {
				clip.stop();
			}
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(content)));
			clip.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void fadeOut(final int seconds) {
		play();
		new Thread(new Runnable() {
			@Override
			public void run() {
				FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				float max = control.getMaximum();
				float min = control.getMinimum();


				try {
					TimeUnit.MILLISECONDS.sleep(seconds);
					for (int i = 0; i < 5; i++) {
						control.setValue(max - (max - min) / 10 * i);
						TimeUnit.MILLISECONDS.sleep(500);
					}
				} catch (InterruptedException e) {
				} finally {
					clip.stop();
				}
			}
		}).start();
	}

	public void stop() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}
}
