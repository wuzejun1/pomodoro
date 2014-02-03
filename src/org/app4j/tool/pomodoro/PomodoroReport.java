package org.app4j.tool.pomodoro;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.XChartPanel;
import org.app4j.tool.pomodoro.util.Dates;

import javax.swing.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class PomodoroReport implements TickListener {
	static final String ID = "Pomodoro";
	final Logger logger;
	public final XChartPanel xChartPanel;
	final Chart chart;
	Date base = new Date();
	Date lastUpdateTime = new Date();
	Pomodoro.State lastState = Pomodoro.State.WAIT;

	public PomodoroReport(Logger logger) {
		this.logger = logger;
		chart = new ChartBuilder().chartType(StyleManager.ChartType.Area).width(800).height(600).yAxisTitle("Minutes").build();
		chart.addSeries("IDEA Time", Lists.newArrayList(new Date()), Lists.newArrayList(0));
		chart.addSeries("Pomodoro Time", Lists.newArrayList(new Date()), Lists.newArrayList(0));
		chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.InsideNW);
		chart.getStyleManager().setAxisTitlesVisible(true);
		chart.getStyleManager().setDatePattern("MM/dd");
		xChartPanel = new XChartPanel(chart);


		showCurrentWeek();
	}

	public void showCurrentWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(base);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		base = calendar.getTime();
		show(base);
	}

	public void show(Date start) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(base);
		calendar.add(Calendar.DATE, 7);
		Date end = calendar.getTime();

		final List<Date> weekDays = Dates.daysOfWeek(start);
		List<Logger.LogEntry> entries = logger.list(start, end);

		Map<Date, Logger.LogEntry> map = Maps.newHashMap();


		for (Logger.LogEntry logEntry : entries) {
			map.put(logEntry.getDate(), logEntry);
		}

		final List<Integer> totalTime = Lists.newArrayList();
		final List<Integer> totalPomodoroTime = Lists.newArrayList();

		for (Date date : weekDays) {
			if (map.containsKey(date)) {
				Logger.LogEntry entry = map.get(date);
				totalTime.add(entry.totalSeconds() / 60);
				totalPomodoroTime.add(entry.totalPomodoroSeconds() / 60);
			} else {
				totalTime.add(0);
				totalPomodoroTime.add(0);
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				xChartPanel.updateSeries("IDEA Time", weekDays, totalTime);
				xChartPanel.updateSeries("Pomodoro Time", weekDays, totalPomodoroTime);
				xChartPanel.repaint();

				int totalSeconds = 0;

				for (int seconds : totalTime) {
					totalSeconds += seconds;
				}

				int totalPomodoroSeconds = 0;
				for (int seconds : totalPomodoroTime) {
					totalPomodoroSeconds += seconds;
				}

				for (Project project : ProjectManager.getInstance().getOpenProjects()) {
					ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
					ToolWindow toolWindow = toolWindowManager.getToolWindow(ID);
					if (toolWindow != null) {
						Content content = toolWindow.getContentManager().getSelectedContent();
						if (content != null) {
							content.setDisplayName("Week " + Dates.weekOfYear(new Date()) + ", IDEA " + totalSeconds + " minutes, Pomodoro " + totalPomodoroSeconds + " minutes");
						}
					}
				}
			}
		});

	}

	@Override
	public void tick(Pomodoro pomodoro) {
		if (lastState != pomodoro.getState() || System.currentTimeMillis() - lastUpdateTime.getTime() > 10 * 1000) {
			lastUpdateTime = new Date();
			lastState = pomodoro.getState();
			showCurrentWeek();
		}
	}
}
