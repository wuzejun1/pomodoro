package org.app4j.tool.pomodoro;

import com.google.common.collect.Lists;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author chi
 */
@State(name = "PomodoroLogs", storages = {@Storage(id = "other", file = "$APP_CONFIG$/pomodoro.logs.xml")})
public class LogStorage implements PersistentStateComponent<LogStorage> {
	public List<String> lines = Lists.newArrayList();
	static final int MAX_LINES = 30;

	@Nullable
	@Override
	public LogStorage getState() {
		return this;
	}

	@Override
	public void loadState(LogStorage state) {
		if (state.lines.size() > MAX_LINES) {
			lines = state.lines.subList(state.lines.size() - MAX_LINES, state.lines.size());
		} else {
			lines = state.lines;
		}
	}

	public String getLastLine() {
		if (lines.isEmpty()) {
			return null;
		}
		return lines.get(lines.size() - 1);
	}

	public void add(String line) {
		lines.add(line);
	}

	public void append(String str) {
		if (lines.isEmpty()) {
			lines.add(str);
		} else {
			lines.set(lines.size() - 1, lines.get(lines.size() - 1) + str);
		}
	}
}
