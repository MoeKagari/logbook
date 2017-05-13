package logbook.update.room;

import logbook.internal.LoggerHolder;

public class ApiRoom {
	private LoggerHolder Log = new LoggerHolder(this.getClass());

	public LoggerHolder getLog() {
		return this.Log;
	}
}
