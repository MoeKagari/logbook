package logbook.gui.logic;

public class HPMessage {
	public static final String ESCAPE_STRING = "退避";

	public static String get(double percent) {
		if (percent == 1.00) {
			return "完好";
		}

		if (percent < 1.00 && percent > 0.75) {
			return "擦伤";
		}

		if (percent <= 0.75 && percent > 0.50) {
			return "小破";
		}

		if (percent <= 0.50 && percent > 0.25) {
			return "中破";
		}

		if (percent <= 0.25 && percent > 0.00) {
			return "大破";
		}

		return "击沉";
	}

}
