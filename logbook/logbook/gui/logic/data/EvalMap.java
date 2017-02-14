package logbook.gui.logic.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class EvalMap {

	private static final Map<String, Double> EVALMAP = new LinkedHashMap<>();

	static {
		EVALMAP.put("S完全勝利", 1.2d);
		EVALMAP.put("S勝利", 1.2d);
		EVALMAP.put("A勝利", 1.0d);
		EVALMAP.put("B戦術的勝利", 1.0d);
		EVALMAP.put("C戦術的敗北", 0.8d);
		EVALMAP.put("D敗北", 0.7d);
	}

	public static double get(String eval) {
		return EVALMAP.get(eval);
	}

	public static Map<String, Double> get() {
		return EVALMAP;
	}

}
