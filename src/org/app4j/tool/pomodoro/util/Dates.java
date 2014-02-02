package org.app4j.tool.pomodoro.util;

import com.google.common.collect.ImmutableList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author chi
 */
public abstract class Dates {
	public static int secondsOfDay(String format) {
		if (!Pattern.compile("[\\d]{2}:[\\d]{2}:[\\d]{2}").matcher(format).matches()) {
			throw new IllegalArgumentException("format must be HH:mm:ss, " + format);
		}
		String[] parts = format.split(":");
		return Integer.parseInt(parts[0]) * 60 * 60 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
	}

	public static int minuteOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR) * 60 + calendar.get(Calendar.MINUTE);
	}

	public static boolean inDateRange(Date date, Date start, Date end) {
		long millionSeconds = millionSecondsOf(date);
		return millionSeconds >= millionSecondsOf(start) && millionSeconds <= millionSecondsOf(end);
	}

	public static long millionSecondsOf(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();
	}

	public static boolean isTheSameDay(Date date1, Date date2) {
		return millionSecondsOf(date1) == millionSecondsOf(date2);
	}

	public static String formatTime(int seconds) {
		int min = seconds / 60;
		int sec = seconds % 60;
		return String.format("%02d", min) + ":" + String.format("%02d", sec);
	}

	public static List<Date> daysOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		ImmutableList.Builder<Date> builder = ImmutableList.builder();
		builder.add(calendar.getTime());

		for (int i = 1; i <= 7; i++) {
			calendar.add(Calendar.DATE, 1);
			builder.add(calendar.getTime());
		}

		return builder.build();
	}

	public static int weekOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
}
