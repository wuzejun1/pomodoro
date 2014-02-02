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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

@State(name = "PomodoroSettings", storages = {@Storage(id = "other", file = "$APP_CONFIG$/pomodoro.settings.xml")})
public class Settings implements PersistentStateComponent<Settings> {
	public int pomodoroSeconds = 25 * 60;
	public int breakSeconds = 5 * 60;
	public int deactivateSeconds = 2 * 60;
	public boolean ticking = false;

	@Override
	public Settings getState() {
		return this;
	}

	@Override
	public void loadState(Settings settings) {
		pomodoroSeconds = settings.pomodoroSeconds;
		breakSeconds = settings.breakSeconds;
		deactivateSeconds = settings.deactivateSeconds;
		ticking = settings.ticking;
	}
}
