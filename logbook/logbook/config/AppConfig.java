package logbook.config;

public class AppConfig {

	private static AppConfigBean config;

	public static AppConfigBean get() {
		if (config == null) config = new AppConfigBean();
		return config;
	}

	public static void store() {

	}

	public static void load() {

	}

}
