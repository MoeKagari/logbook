package logbook.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerHolder {

	private Logger logger = null;

	public LoggerHolder(Class<?> clazz) {
		if (clazz != null) {
			this.logger = LogManager.getLogger(clazz);
		}
	}

	public LoggerHolder(String name) {
		if (name != null) {
			this.logger = LogManager.getLogger(name);
		}
	}

	public synchronized Logger get() {
		if (this.logger == null) {
			this.logger = LogManager.getLogger("default");
		}
		return this.logger;
	}

}
