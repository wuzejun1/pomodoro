package org.app4j.tool.pomodoro;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author chi
 */
public class StorageTest {
	@Test
	public void maxLines() {
		LogStorage storage = new LogStorage();
		for (int i = 0; i < 50; i++) {
			storage.lines.add("" + i);
		}

		LogStorage newStorage = new LogStorage();
		newStorage.loadState(storage);
		Assert.assertEquals(LogStorage.MAX_LINES, newStorage.lines.size());
		Assert.assertEquals("49", newStorage.lines.get(newStorage.lines.size() - 1));
	}
}
