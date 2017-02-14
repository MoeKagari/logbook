package logbook.context.room;

import logbook.internal.LoggerHolder;

public class Room {
	private LoggerHolder Log = new LoggerHolder(this.getClass());

	public LoggerHolder getLog() {
		return this.Log;
	}
}
