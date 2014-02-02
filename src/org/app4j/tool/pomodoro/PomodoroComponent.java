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

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;


public class PomodoroComponent implements ApplicationComponent {
	TickThread controlThread;
	Pomodoro pomodoro;
	PomodoroStatus status;
	PomodoroReport report;
	MessageBusConnection messageBusConnection;

	public static Pomodoro pomodoro() {
		PomodoroComponent pomodoroComponent = ApplicationManager.getApplication().getComponent(PomodoroComponent.class);
		return pomodoroComponent.getPomodoro();
	}

	@Override
	public void initComponent() {
		final Settings settings = ServiceManager.getService(Settings.class);
		final LogStorage storage = ServiceManager.getService(LogStorage.class);

		Logger logger = new Logger(storage);
		status = new PomodoroStatus(settings);
		report = new PomodoroReport(logger);

		pomodoro = new Pomodoro(settings, logger, status, report);
		pomodoro.activate();

		messageBusConnection = ApplicationManagerEx.getApplicationEx().getMessageBus().connect();
		messageBusConnection.subscribe(ApplicationActivationListener.TOPIC, new ApplicationActivationListener() {
			@Override
			public void applicationActivated(IdeFrame ideFrame) {
			}

			@Override
			public void applicationDeactivated(IdeFrame ideFrame) {
			}
		});

		ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter() {
			@Override
			public void projectOpened(Project project) {
				StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
				if (statusBar != null && statusBar.getWidget(PomodoroStatus.ID) == null) {
					statusBar.addWidget(status);
				}

				ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

				if (toolWindowManager.getToolWindow(PomodoroReport.ID) == null) {
					ToolWindow toolWindow = toolWindowManager.registerToolWindow(PomodoroReport.ID, false, ToolWindowAnchor.BOTTOM);
					toolWindow.setIcon(Pomodoro.ICON_RUNNING);
					ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
					Content content = contentFactory.createContent(report.xChartPanel, "", false);
					toolWindow.getContentManager().addContent(content);
				}

			}

			@Override
			public void projectClosed(Project project) {
				StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
				if (statusBar != null && statusBar.getWidget(PomodoroStatus.ID) != null) {
					statusBar.removeWidget(PomodoroStatus.ID);
				}

				ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
				if (toolWindowManager.getToolWindow(PomodoroReport.ID) != null) {
					toolWindowManager.unregisterToolWindow(PomodoroReport.ID);
				}
			}
		});

		controlThread = new TickThread(pomodoro);
		controlThread.start();
	}

	@Override
	public void disposeComponent() {
		controlThread.stopTicking();
		status.dispose();
		pomodoro.deactivate();

		if (messageBusConnection != null) {
			messageBusConnection.disconnect();
		}
	}

	@NotNull
	@Override
	public String getComponentName() {
		return "Pomodoro";
	}

	public Pomodoro getPomodoro() {
		return pomodoro;
	}

}
