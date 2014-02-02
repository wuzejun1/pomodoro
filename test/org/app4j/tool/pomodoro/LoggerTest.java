package org.app4j.tool.pomodoro;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author chi
 */
public class LoggerTest {
	Logger logger;

	@Before
	public void setup() throws IOException {
		logger = new Logger(new LogStorage());
	}


	@Test
	public void start() throws IOException {
		Date date = new Date();
		logger.start(date);
		Assert.assertEquals(new SimpleDateFormat(Logger.DATE_FORMAT).format(date), new SimpleDateFormat(Logger.DATE_FORMAT).format(logger.lastEntry().getDate()));
	}

	@Test
	public void end() throws IOException {
		Date date = new Date();
		logger.start(date);
		logger.end(date);
		Assert.assertEquals(new SimpleDateFormat(Logger.DATE_FORMAT).format(date) + " [" + new SimpleDateFormat(Logger.TIME_FORMAT).format(date)
				+ "~" + new SimpleDateFormat(Logger.TIME_FORMAT).format(date) + "]", logger.lastEntry().toString());
	}

	@Test
	public void interrupt() throws IOException {
		Date date = new Date();
		logger.start(date);
		logger.interrupt(date);
		Assert.assertEquals(new SimpleDateFormat(Logger.DATE_FORMAT).format(date) + " [" + new SimpleDateFormat(Logger.TIME_FORMAT).format(date)
				+ "^" + new SimpleDateFormat(Logger.TIME_FORMAT).format(date) + "]", logger.lastEntry().toString());
	}

	@Test
	public void list() throws IOException {
		Date date = new Date();
		logger.start(date);
		logger.end(date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		Date next = calendar.getTime();
		logger.start(next);
		logger.end(next);

		List<Logger.LogEntry> entries = logger.list(date, next);
		Assert.assertEquals(2, entries.size());
	}

	@Test
	public void entryFormat() {
		String line = "2014/01/31 [09:12:12~09:12:12] [09:12:12~09:12:12]";
		Logger.LogEntry logEntry = new Logger.LogEntry(line);
		Assert.assertEquals("2014/01/31", new SimpleDateFormat(Logger.DATE_FORMAT).format(logEntry.getDate()));
	}

	@Test
	public void totalMinutes() {
		String line = "2014/01/31 [09:12:12~09:12:13] [09:12:12^09:12:13]";
		Logger.LogEntry logEntry = new Logger.LogEntry(line);
		Assert.assertEquals(2, logEntry.totalSeconds());
		Assert.assertEquals(1, logEntry.totalPomodoroSeconds());
	}

	@Test
	public void isComplete() {
		Assert.assertTrue(new Logger.LogEntry("2014/01/31").isComplete());
		Assert.assertTrue(new Logger.LogEntry("2014/01/31 [09:12:12~09:12:13] [09:12:12^09:12:13]").isComplete());
		Assert.assertFalse(new Logger.LogEntry("2014/01/31 [09:12:12~09:12:13] [09:12:12").isComplete());
	}

	@Test
	public void lastStartMinute() {
		String line = "2014/01/31 [00:01:01";
		Logger.LogEntry logEntry = new Logger.LogEntry(line);
		Assert.assertEquals(61, logEntry.lastStartSeconds());
	}
}
