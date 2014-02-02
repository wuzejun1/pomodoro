/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.app4j.tool.pomodoro;

import com.google.common.collect.ImmutableList;
import org.app4j.tool.pomodoro.util.WavClip;

import javax.swing.*;
import java.util.Date;
import java.util.List;

public class Pomodoro {
	static final ImageIcon ICON_WAIT = new ImageIcon(PomodoroStatus.class.getResource("/resources/pomodoroStopped.png"));
	static final ImageIcon ICON_RUNNING = new ImageIcon(PomodoroStatus.class.getResource("/resources/pomodoro.png"));
	static final ImageIcon ICON_BREAK = new ImageIcon(PomodoroStatus.class.getResource("/resources/pomodoroBreak.png"));

	static final WavClip SOUND_TICK = new WavClip("/resources/ticktock.wav");
	static final WavClip SOUND_ALARM = new WavClip("/resources/alarm.wav");

	public final Settings settings;
	public final Logger logger;
	final List<TickListener> tickListeners;

	Date baseTime = new Date();
	volatile State state;

	public Pomodoro(Settings settings, Logger logger, TickListener... tickListeners) {
		this.settings = settings;
		this.logger = logger;
		state = State.WAIT;
		this.tickListeners = ImmutableList.copyOf(tickListeners);
	}

	public void activate() {
		logger.start(new Date());
	}

	public int getRemainSeconds() {
		switch (state) {
			case RUNNING:
				int pomodoroSeconds = settings.pomodoroSeconds - 1 - (int) ((System.currentTimeMillis() - baseTime.getTime()) / 1000);
				return pomodoroSeconds > 0 ? pomodoroSeconds : 0;
			case BREAK:
				int breakSeconds = settings.breakSeconds - 1 - (int) ((System.currentTimeMillis() - baseTime.getTime()) / 1000);
				return breakSeconds > 0 ? breakSeconds : 0;
			default:
				return settings.pomodoroSeconds;
		}
	}

	public void deactivate(Date date) {
		logger.interrupt(date);
		state = State.WAIT;
	}

	public void deactivate() {
		logger.interrupt(new Date());
		state = State.WAIT;
	}

	public void start() {
		logger.start(new Date());
		state = State.RUNNING;
		baseTime = new Date();
		SOUND_TICK.fadeOut(15);
	}

	public void stop() {
		logger.interrupt(new Date());
		state = State.WAIT;
		SOUND_TICK.stop();
		activate();
	}

	public void finish() {
		logger.end(new Date());
		state = State.WAIT;
		baseTime = new Date();
		SOUND_ALARM.play();
	}

	public State getState() {
		return state;
	}

	public void tick() {
		switch (state) {
			case RUNNING:
				if (getRemainSeconds() == 0) {
					finish();
				}

			case BREAK:
				if (getRemainSeconds() == 0) {
					finish();
				}
		}

		for (TickListener tickListener : tickListeners) {
			tickListener.tick(this);
		}
	}


	public static enum State {
		WAIT, RUNNING, BREAK
	}
}
