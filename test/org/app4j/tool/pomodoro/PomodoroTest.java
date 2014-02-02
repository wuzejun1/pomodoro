package org.app4j.tool.pomodoro;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author chi
 */
public class PomodoroTest {
	Pomodoro pomodoro;

	File file;

	@Before
	public void setup() throws IOException {
		file = File.createTempFile("pomodoro", "log");
		Settings settings = new Settings();
		pomodoro = new Pomodoro(settings, new Logger(new LogStorage()));
	}

	@After
	public void clean() {
		file.deleteOnExit();
	}

	@Test
	public void start() throws IOException {
		pomodoro.start();

		Assert.assertEquals(Pomodoro.State.RUNNING, pomodoro.getState());
		System.out.println(Files.toString(file, Charsets.UTF_8));
	}

}
