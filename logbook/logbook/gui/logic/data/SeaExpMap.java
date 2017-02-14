package logbook.gui.logic.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class SeaExpMap {

	private final static Map<String, Integer> SEAEXPMAP = new LinkedHashMap<>();

	static {
		SEAEXPMAP.put("1-1", 30);
		SEAEXPMAP.put("1-2", 50);
		SEAEXPMAP.put("1-3", 80);
		SEAEXPMAP.put("1-4", 100);
		SEAEXPMAP.put("1-5", 150);
		SEAEXPMAP.put("2-1", 120);
		SEAEXPMAP.put("2-2", 150);
		SEAEXPMAP.put("2-3", 200);
		SEAEXPMAP.put("2-4", 300);
		SEAEXPMAP.put("2-5", 250);
		SEAEXPMAP.put("3-1", 310);
		SEAEXPMAP.put("3-2", 320);
		SEAEXPMAP.put("3-3", 330);
		SEAEXPMAP.put("3-4", 350);
		SEAEXPMAP.put("3-5", 400);
		SEAEXPMAP.put("4-1", 310);
		SEAEXPMAP.put("4-2", 320);
		SEAEXPMAP.put("4-3", 330);
		SEAEXPMAP.put("4-4", 340);
		SEAEXPMAP.put("5-1", 360);
		SEAEXPMAP.put("5-2", 380);
		SEAEXPMAP.put("5-3", 400);
		SEAEXPMAP.put("5-4", 420);
		SEAEXPMAP.put("5-5", 450);
		SEAEXPMAP.put("6-1", 380);
		SEAEXPMAP.put("6-2", 420);
	}

	public static int get(String sea) {
		return SEAEXPMAP.get(sea);
	}

	public static Map<String, Integer> get() {
		return SEAEXPMAP;
	}

}
