package logbook.gui.logic;

import java.util.Calendar;
import java.util.Date;

import logbook.config.AppConstants;

/**
 * 時間を計算する
 *
 */
public class TimeString {

	private static final int ONE_DAY = 24 * 60 * 60;
	private static final int ONE_MINUTES = 60;
	private static final int ONE_HOUR = 60 * 60;

	public static String toDateRestString(long rest) {
		if (rest < 0) return "";

		long d, h, m, s;
		String[] se;

		boolean showComplete = false;
		boolean showDay = false;
		int timeStyle = 0;

		if (showDay) {
			d = rest / ONE_DAY;
			h = (rest % ONE_DAY) / ONE_HOUR;
		} else {
			d = 0;
			h = rest / ONE_HOUR;
		}
		m = (rest % ONE_HOUR) / ONE_MINUTES;
		s = rest % ONE_HOUR % ONE_MINUTES;

		switch (timeStyle) {
			case 0:
				se = new String[] { "天", "时", "分", "秒" };
				break;
			case 1:
				se = new String[] { ":", ":", ":", " " };
				break;
			case 2:
				se = new String[] { "∶", "∶", "∶", " " };
				break;
			default:
				se = new String[] { "∶", "∶", "∶", " " };
				break;
		}

		if (showComplete) {
			if (showDay) return d + se[0] + String.format("%02d", h) + se[1] + String.format("%02d", m) + se[2] + String.format("%02d", s) + se[3];
			else return h + se[1] + String.format("%02d", m) + se[2] + String.format("%02d", s) + se[3];
		}

		if (d != 0) {
			return d + se[0] + String.format("%02d", h) + se[1] + String.format("%02d", m) + se[2] + String.format("%02d", s) + se[3];
		}
		if (h != 0) {
			return h + se[1] + String.format("%02d", m) + se[2] + String.format("%02d", s) + se[3];
		}
		if (m != 0) {
			return m + se[2] + String.format("%02d", s) + se[3];
		}
		return s + se[3];
	}

	public static String toDateRestString(long rest, String defaultString) {
		if (rest <= 0) return defaultString;
		return toDateRestString(rest);
	}

	public static long getCurrentTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

	public static String timeToStringForTable(long time) {
		return AppConstants.TABLE_TIME_FORMAT.format(new Date(time));
	}

}
