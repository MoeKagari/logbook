package logbook.util;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class JsonUtils {

	public static int[] getIntArray(JsonArray array) {
		int len = array.size();
		int[] intArray = new int[len];
		for (int i = 0; i < len; i++) {
			intArray[i] = array.getJsonNumber(i).intValue();
		}
		return intArray;
	}

	public static int[] getIntArray(JsonObject json, String key) {
		return getIntArray(json.getJsonArray(key));
	}

	public static double[] getDoubleArray(JsonArray array) {
		int len = array.size();
		double[] doublearray = new double[len];
		for (int i = 0; i < len; i++) {
			doublearray[i] = array.getJsonNumber(i).doubleValue();
		}
		return doublearray;
	}

	public static double[] getDoubleArray(JsonObject json, String key) {
		return getDoubleArray(json.getJsonArray(key));
	}

	public static String[] getStringArray(JsonArray array) {
		int len = array.size();
		String[] stringarray = new String[len];
		for (int i = 0; i < len; i++) {
			stringarray[i] = array.getString(i);
		}
		return stringarray;
	}

	public static String[] getStringArray(JsonObject json, String key) {
		return getStringArray(json.getJsonArray(key));
	}

}
