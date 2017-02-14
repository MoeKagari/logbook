package logbook.context.dto.data;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;

import logbook.util.JsonUtils;

public class MasterDataDto {
	private final Map<Integer, MasterShipDataDto> masterShipDataMap = new HashMap<>();
	private final Map<Integer, MasterShipDataDto> masterEnemyDataMap = new HashMap<>();
	private final Map<Integer, MasterSlotitemDataDto> masterSlotitemDataMap = new HashMap<>();

	public MasterDataDto(JsonObject json) {
		JsonArray array;

		array = json.getJsonArray("api_mst_ship");
		array.forEach(obj -> {
			MasterShipDataDto ship = new MasterShipDataDto((JsonObject) obj);
			if (ship.isEnemy()) {
				this.masterEnemyDataMap.put(ship.getId(), ship);
			} else {
				this.masterShipDataMap.put(ship.getId(), ship);
			}
		});

		array = json.getJsonArray("api_mst_slotitem");
		array.forEach(obj -> {
			MasterSlotitemDataDto item = new MasterSlotitemDataDto((JsonObject) obj);
			this.masterSlotitemDataMap.put(item.getId(), item);
		});
	}

	public Map<Integer, MasterShipDataDto> getMasterShipDataMap() {
		return this.masterShipDataMap;
	}

	public Map<Integer, MasterShipDataDto> getMasterEnemyDataMap() {
		return this.masterEnemyDataMap;
	}

	public Map<Integer, MasterSlotitemDataDto> getMasterSlotitemDataMap() {
		return this.masterSlotitemDataMap;
	}

	public class MasterShipDataDto {
		private final JsonObject json;

		public MasterShipDataDto(JsonObject json) {
			this.json = json;
		}

		public int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}

		public int getGaizhaoLv() {
			return this.json.getInt("api_afterlv");
		}

		public int getGaizhaoAfterId() {
			return this.json.getInt("api_aftershipid");
		}

		public int[] getTaik() {
			return JsonUtils.getIntArray(this.json, "api_taik");
		}

		public int[] getSouk() {
			return JsonUtils.getIntArray(this.json, "api_souk");
		}

		public int[] getHoug() {
			return JsonUtils.getIntArray(this.json, "api_houg");
		}

		public int[] getRaig() {
			return JsonUtils.getIntArray(this.json, "api_raig");
		}

		public int[] getTyku() {
			return JsonUtils.getIntArray(this.json, "api_tyku");
		}

		public int[] getLuck() {
			return JsonUtils.getIntArray(this.json, "api_luck");
		}

		public int[] getMaxeq() {
			return JsonUtils.getIntArray(this.json, "api_maxeq");
		}

		public int getFuelMax() {
			return this.json.getInt("api_fuel_max");
		}

		public int getBullMax() {
			return this.json.getInt("api_bull_max");
		}

		public boolean isEnemy() {
			return this.json.size() <= 6;
		}

	}

	public class MasterSlotitemDataDto {
		private final JsonObject json;

		public MasterSlotitemDataDto(JsonObject json) {
			this.json = json;
		}

		public int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}

	}

}
