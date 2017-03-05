package logbook.context.dto.data;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * 地图详情
 * @author MoeKagari
 */
public class MapinfoDto {
	private ArrayList<OneMap> maps = new ArrayList<>();

	public MapinfoDto(JsonArray array) {
		array.forEach(value -> this.maps.add(new OneMap((JsonObject) value)));
	}

	public ArrayList<OneMap> getMaps() {
		return this.maps;
	}

	public class OneMap {
		private final int id;
		private final boolean clear;
		private final boolean exboss;
		private final int defeatCount;
		private final int airBaseDeckCount;
		private final EventMap eventMapInfo;

		public OneMap(JsonObject json) {
			this.id = json.getInt("api_id");
			this.clear = json.getInt("api_cleared") == 1;
			this.exboss = json.getInt("api_exboss_flag") == 1;
			this.defeatCount = json.containsKey("api_defeat_count") ? json.getInt("api_defeat_count") : -1;
			this.airBaseDeckCount = json.containsKey("api_air_base_decks") ? json.getInt("api_air_base_decks") : -1;
			this.eventMapInfo = json.containsKey("api_eventmap") ? new EventMap(json.getJsonObject("api_eventmap")) : null;
		}

		public int getArea() {
			return this.id / 10;
		}

		public int getNo() {
			return this.id % 10;
		}

		public int getMaxCount() {
			switch (this.id) {
				case 62:
					return 3;
				case 15:
				case 25:
				case 35:
				case 44:
				case 52:
				case 63:
					return 4;
				case 45:
				case 53:
				case 54:
				case 55:
				case 64:
					return 5;
				case 65:
					return 6;
				case 16:
					return 7;
				default:
					return 1;
			}
		}

		public int getDefeatCount() {
			return this.defeatCount;
		}

		public boolean isClear() {
			return this.clear;
		}

		public boolean isExboss() {
			return this.exboss;
		}

		public int getAirBaseDeckCount() {
			return this.airBaseDeckCount;
		}

		public boolean isEventMap() {
			return this.eventMapInfo != null;
		}

		public EventMap getEventMap() {
			return this.eventMapInfo;
		}
	}

	public class EventMap {
		private final int nowhp;
		private final int maxhp;
		private final int state;
		private final int rank;
		private final int hptype;

		public EventMap(JsonObject json) {
			this.nowhp = json.getInt("api_now_maphp");
			this.maxhp = json.getInt("api_max_maphp");
			this.state = json.getInt("api_state");
			this.rank = json.getInt("api_selected_rank");
			this.hptype = json.containsKey("api_gauge_type") ? json.getInt("api_gauge_type") : -1;
		}

		public int getNowhp() {
			return this.nowhp;
		}

		public int getMaxhp() {
			return this.maxhp;
		}

		public int getState() {
			return this.state;
		}

		public String getRank() {
			switch (this.rank) {
				case 0:
					return "未选择";
				case 1:
					return "丙";
				case 2:
					return "乙";
				case 3:
					return "甲";
				default:
					return "";
			}
		}

		public String getHptype() {
			switch (this.hptype) {
				case 2:
					return "HP血条";
				case 3:
					return "TP血条";
				default:
					return "";
			}
		}
	}

}
