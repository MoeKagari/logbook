package logbook.config;

import java.io.File;
import java.text.SimpleDateFormat;

public class AppConstants {

	public static final String MAINWINDOWNAME = "航海日志";
	public static final String[] DEFAULT_FLEET_NAME = { "第一舰队", "第二舰队", "第三舰队", "第四舰队" };

	public static final SimpleDateFormat DECK_NDOCK_COMPLETE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat TABLE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat CONSOLE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	public static final String MASTERDATAFILEPATH = new File("MasterData.json").getAbsolutePath();

	/** 航海日志的图标 */
	public static final String LOGO = "/resources/logo.png";

}
