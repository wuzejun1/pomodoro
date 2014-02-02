package org.app4j.tool.pomodoro;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.app4j.tool.pomodoro.util.Dates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author chi
 */
public class Logger {
	static final String DATE_FORMAT = "yyyy/MM/dd";
	static final String TIME_FORMAT = "HH:mm:ss";
	final LogStorage storage;

	public Logger(LogStorage storage) {
		this.storage = storage;
	}

	public void start(Date start) {
		LogEntry logEntry = lastEntry();
		if (logEntry == null) {
			storage.append(new SimpleDateFormat(DATE_FORMAT).format(start) + " [" + new SimpleDateFormat(TIME_FORMAT).format(start));
		} else {
			if (!logEntry.isComplete()) {
				interrupt(start);
			}
			if (Dates.isTheSameDay(logEntry.getDate(), start)) {
				storage.append(" [" + new SimpleDateFormat(TIME_FORMAT).format(start));
			} else {
				storage.add(new SimpleDateFormat(DATE_FORMAT).format(start) + " [" + new SimpleDateFormat(TIME_FORMAT).format(start));
			}
		}
	}

	public void end(Date end) {
		LogEntry logEntry = lastEntry();
		if (logEntry != null && !logEntry.isComplete()) {
			storage.append("~" + new SimpleDateFormat(TIME_FORMAT).format(end) + "]");
		}
	}

	public void interrupt(Date end) {
		LogEntry logEntry = lastEntry();
		if (logEntry != null && !logEntry.isComplete()) {
			storage.append("^" + new SimpleDateFormat(TIME_FORMAT).format(end) + "]");
		}
	}

	public List<LogEntry> list(Date start, Date end) {
		if (storage.lines.isEmpty()) return Lists.newArrayList();
		ImmutableList.Builder<LogEntry> builder = ImmutableList.builder();
		for (String line : storage.lines) {
			LogEntry logEntry = new LogEntry(line);

			if (Dates.inDateRange(logEntry.getDate(), start, end)) {
				builder.add(logEntry);
			}
		}
		return builder.build();
	}

	public LogEntry lastEntry() {
		if (storage.lines.isEmpty()) return null;
		return new LogEntry(storage.getLastLine());
	}

	public static class LogEntry {
		final String line;
		Date date;
		int totalSeconds;
		int totalPomodoroSeconds;

		public LogEntry(String line) {
			this.line = line;
			int state = 0;

			int lastIndex = 0;
			int start = 0;

			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);

				if (state == 0 && Character.isWhitespace(c)) {
					try {
						date = new SimpleDateFormat(DATE_FORMAT).parse(line.substring(0, i));
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
					state = 1;
				} else if (state == 1 && c == '[') {
					lastIndex = i + 1;
					state = 2;
				} else if (state > 2 && c == ']') {
					int end = Dates.secondsOfDay(line.substring(lastIndex, i));
					if (state == 3) {
						totalPomodoroSeconds += end - start;
					}

					totalSeconds += end - start;
					state = 1;
				} else if (state == 2 && c == '~') {
					start = Dates.secondsOfDay(line.substring(lastIndex, i));
					lastIndex = i + 1;
					state = 3;
				} else if (state == 2 && c == '^') {
					start = Dates.secondsOfDay(line.substring(lastIndex, i));
					lastIndex = i + 1;
					state = 4;
				}
			}
		}

		public boolean isComplete() {
			return this.line.length() == DATE_FORMAT.length() || this.line.endsWith("]");
		}

		public Date getDate() {
			return date;
		}

		public int totalSeconds() {
			return totalSeconds;
		}

		public int lastStartSeconds() {
			int p = line.lastIndexOf("[");

			if (p > 0) {
				return Dates.secondsOfDay(line.substring(p + 1));
			} else {
				return -1;
			}
		}

		public int totalPomodoroSeconds() {
			return totalPomodoroSeconds;
		}

		public String toString() {
			return line;
		}
	}
}
