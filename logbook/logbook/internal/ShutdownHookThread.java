package logbook.internal;

import logbook.config.AppConfig;
import logbook.context.update.GlobalContext;

public class ShutdownHookThread extends Thread {

	@Override
	public void run() {
		AppConfig.store();
		GlobalContext.store();
	}

}
