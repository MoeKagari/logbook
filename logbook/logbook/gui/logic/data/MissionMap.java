package logbook.gui.logic.data;

import java.util.HashMap;
import java.util.Map;

public final class MissionMap {

	private static final Map<Integer, String> MISSIONMAP = new HashMap<>();
	static {
		MISSIONMAP.put(1, "練習航海");
		MISSIONMAP.put(2, "長距離練習航海");
		MISSIONMAP.put(3, "警備任務");
		MISSIONMAP.put(4, "対潜警戒任務");
		MISSIONMAP.put(5, "海上護衛任務");
		MISSIONMAP.put(6, "防空射撃演習");
		MISSIONMAP.put(7, "観艦式予行");
		MISSIONMAP.put(8, "観艦式");
		MISSIONMAP.put(9, "タンカー護衛任務");
		MISSIONMAP.put(10, "強行偵察任務");
		MISSIONMAP.put(11, "ボーキサイト輸送任務");
		MISSIONMAP.put(12, "資源輸送任務");
		MISSIONMAP.put(13, "鼠輸送作戦");
		MISSIONMAP.put(14, "包囲陸戦隊撤収作戦");
		MISSIONMAP.put(15, "囮機動部隊支援作戦");
		MISSIONMAP.put(16, "艦隊決戦援護作戦");
		MISSIONMAP.put(17, "敵地偵察作戦");
		MISSIONMAP.put(18, "航空機輸送作戦");
		MISSIONMAP.put(19, "北号作戦");
		MISSIONMAP.put(20, "潜水艦哨戒任務");
		MISSIONMAP.put(21, "北方鼠輸送作戦");
		MISSIONMAP.put(22, "艦隊演習");
		MISSIONMAP.put(23, "航空戦艦運用演習");
		MISSIONMAP.put(24, "<UNKNOWN>");
		MISSIONMAP.put(25, "通商破壊作戦");
		MISSIONMAP.put(26, "敵母港空襲作戦");
		MISSIONMAP.put(27, "潜水艦通商破壊作戦");
		MISSIONMAP.put(28, "西方海域封鎖作戦");
		MISSIONMAP.put(29, "潜水艦派遣演習");
		MISSIONMAP.put(30, "潜水艦派遣作戦");
		MISSIONMAP.put(31, "海外艦との接触");
		MISSIONMAP.put(32, "遠洋練習航海");
		MISSIONMAP.put(33, "前衛支援任務");
		MISSIONMAP.put(34, "艦隊決戦支援任務");
		MISSIONMAP.put(35, "ＭＯ作戦");
		MISSIONMAP.put(36, "水上機基地建設");
		MISSIONMAP.put(37, "東京急行");
		MISSIONMAP.put(38, "東京急行(弐)");
		MISSIONMAP.put(39, "遠洋潜水艦作戦");
		MISSIONMAP.put(40, "水上機前線輸送");
	};

	public static String get(int id) {
		return MISSIONMAP.get(id);
	}

}
