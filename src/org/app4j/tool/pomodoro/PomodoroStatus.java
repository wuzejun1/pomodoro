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

import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import org.app4j.tool.pomodoro.util.Dates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PomodoroStatus implements TickListener, StatusBarWidget.Multiframe, CustomStatusBarWidget {
	static final String ID = "PomodoroStatus";
	final JLabel label;

	public PomodoroStatus(Settings settings) {
		label = new JLabel();
		label.setBorder(WidgetBorder.INSTANCE);
		label.setIcon(Pomodoro.ICON_WAIT);
		label.setText(Dates.formatTime(settings.pomodoroSeconds));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Pomodoro pomodoro = PomodoroComponent.pomodoro();
				switch (pomodoro.getState()) {
					case RUNNING:
					case BREAK:
						PomodoroComponent.pomodoro().stop();
						break;
					case WAIT:
						PomodoroComponent.pomodoro().start();
						break;
				}
			}
		});
	}

	@Override
	public JComponent getComponent() {
		return label;
	}

	@Override
	public StatusBarWidget copy() {
		return null;
	}

	@NotNull
	@Override
	public String ID() {
		return ID;
	}

	@Nullable
	@Override
	public WidgetPresentation getPresentation(@NotNull PlatformType type) {
		return null;
	}

	@Override
	public void install(@NotNull StatusBar statusBar) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void tick(final Pomodoro pomodoro) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				label.setIcon(Pomodoro.ICON_WAIT);
				label.setText(Dates.formatTime(pomodoro.settings.pomodoroSeconds));
				switch (pomodoro.getState()) {
					case RUNNING:
						label.setIcon(Pomodoro.ICON_RUNNING);
						label.setText(Dates.formatTime(pomodoro.getRemainSeconds()));
						break;
					case BREAK:
						label.setIcon(Pomodoro.ICON_BREAK);
						label.setText(Dates.formatTime(pomodoro.getRemainSeconds()));
						break;
					case WAIT:
					default:
						label.setIcon(Pomodoro.ICON_WAIT);
						label.setText(Dates.formatTime(pomodoro.getRemainSeconds()));
				}
			}
		});

	}
}
