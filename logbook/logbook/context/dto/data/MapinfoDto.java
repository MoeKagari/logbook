package logbook.context.dto.data;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * 地图详情
 * @author MoeKagari
 *
 */
public class MapinfoDto {
	private ArrayList<OneMap> maps = new ArrayList<>();

	public MapinfoDto(JsonArray array) {
		array.forEach(value -> this.maps.add(new OneMap((JsonObject) value)));
	}

	public class OneMap {
		private final int area;
		private final int no;
		private final boolean clear;
		private final boolean exboss;
		private final int defeatCount;
		private final int airBaseDeckCount;
		private final EventMapInfo eventMapInfo;

		public OneMap(JsonObject json) {
			this.area = json.getInt("api_id") / 10;
			this.no = json.getInt("api_id") % 10;
			this.clear = json.getInt("api_cleared") == 1;
			this.exboss = json.getInt("api_exboss_flag") == 1;
			this.defeatCount = json.containsKey("api_defeat_count") ? json.getInt("api_defeat_count") : -1;
			this.airBaseDeckCount = json.containsKey("api_air_base_decks") ? json.getInt("api_air_base_decks") : -1;
			this.eventMapInfo = json.containsKey("api_eventmap") ? new EventMapInfo(json.getJsonObject("api_eventmap")) : null;
		}

		public int getArea() {
			return this.area;
		}

		public int getNo() {
			return this.no;
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

		public EventMapInfo getEventMapInfo() {
			return this.eventMapInfo;
		}
	}

	public class EventMapInfo {
		private final int nowhp;
		private final int maxhp;
		private final int state;
		private final int rank;
		private final int hptype;

		public EventMapInfo(JsonObject json) {
			this.nowhp = json.getInt("");
			this.maxhp = json.getInt("");
			this.state = json.getInt("");
			this.rank = json.getInt("");
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
				case 1:
					return "丙";
				case 2:
					return "乙";
				case 3:
					return "甲";
				default:
					return null;
			}
		}

		public String getHptype() {
			switch (this.hptype) {
				case 2:
					return "HP血条";
				case 3:
					return "TP血条";
				default:
					return null;
			}
		}
	}

}
