package logbook.config;

import java.text.SimpleDateFormat;

public class AppConstants {

	public static final String MAINWINDOWNAME = "航海日志";
	public static final String[] DEFAULT_FLEET_NAME = { "第一舰队", "第二舰队", "第三舰队", "第四舰队" };

	public static final SimpleDateFormat DECK_NDOCK_COMPLETE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat TABLE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat CONSOLE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	/** 航海日志的图标 */
	public static final String LOGO = "/resources/logo.png";

	/** /resources/icon/add.png */
	public static final String R_ICON_ADD = "/resources/icon/add.png";
	/** /resources/icon/delete.png */
	public static final String R_ICON_DELETE = "/resources/icon/delete.png";
	/** /resources/icon/error.png */
	public static final String R_ICON_ERROR = "/resources/icon/error.png";
	/** /resources/icon/error_mono.png */
	public static final String R_ICON_ERROR_MONO = "/resources/icon/error_mono.png";
	/** /resources/icon/exclamation.png */
	public static final String R_ICON_EXCLAMATION = "/resources/icon/exclamation.png";
	/** /resources/icon/exclamation_mono.png */
	public static final String R_ICON_EXCLAMATION_MONO = "/resources/icon/exclamation_mono.png";
	/** /resources/icon/folder.png */
	public static final String R_ICON_FOLDER = "/resources/icon/folder.png";
	/** /resources/icon/star.png */
	public static final String R_ICON_STAR = "/resources/icon/star.png";
	/** /resources/icon/heart.png */
	public static final String R_ICON_LOCKED = "/resources/icon/heart.png";
	/** /resources/icon/arrow-left.png */
	public static final String R_ICON_LEFT = "/resources/icon/arrow-left.png";
	/** /resources/icon/arrow-right.png */
	public static final String R_ICON_RIGHT = "/resources/icon/arrow-right.png";

}
