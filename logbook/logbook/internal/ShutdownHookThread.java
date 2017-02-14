package logbook.internal;

import logbook.config.AppConfig;

public class ShutdownHookThread extends Thread {

	@Override
	public void run() {
		AppConfig.store();
	}

}
