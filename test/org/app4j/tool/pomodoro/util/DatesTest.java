package org.app4j.tool.pomodoro.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @author chi
 */
public class DatesTest {
	@Test
	public void secondsOfDay() throws Exception {
		String time = "00:01:01";
		Assert.assertEquals(61, Dates.secondsOfDay(time));
	}

	@Test(expected = IllegalArgumentException.class)
	public void illegal() throws Exception {
		Dates.secondsOfDay("1:1");
	}

	@Test
	public void weekDays() {
		Assert.assertEquals(7, Dates.daysOfWeek(new Date()).size());
	}
}
