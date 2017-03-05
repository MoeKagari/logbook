package logbook.context.dto.data;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import logbook.util.JsonUtils;

public class MasterDataDto {
	private final JsonObject json;
	private final Map<Integer, MasterShipDataDto> masterShipDataMap = new HashMap<>();
	private final Map<Integer, MasterSlotitemDataDto> masterSlotitemDataMap = new HashMap<>();
	private final Map<Integer, MasterMissionDataDto> masterMissionDataMap = new HashMap<>();
	private final Map<Integer, MasterUserItemDto> masterUserItemDtoMap = new HashMap<>();

	public MasterDataDto(JsonObject json) {
		this.json = json;
		json.getJsonArray("api_mst_ship").forEach(obj -> {
			MasterShipDataDto ship = new MasterShipDataDto((JsonObject) obj);
			this.masterShipDataMap.put(ship.getId(), ship);
		});
		json.getJsonArray("api_mst_slotitem").forEach(obj -> {
			MasterSlotitemDataDto item = new MasterSlotitemDataDto((JsonObject) obj);
			this.masterSlotitemDataMap.put(item.getId(), item);
		});
		json.getJsonArray("api_mst_mission").forEach(obj -> {
			MasterMissionDataDto item = new MasterMissionDataDto((JsonObject) obj);
			this.masterMissionDataMap.put(item.getId(), item);
		});
		json.getJsonArray("api_mst_useitem").forEach(obj -> {
			MasterUserItemDto item = new MasterUserItemDto((JsonObject) obj);
			this.masterUserItemDtoMap.put(item.getId(), item);
		});
	}

	public JsonObject getJson() {
		return this.json;
	}

	public Map<Integer, MasterShipDataDto> getMasterShipDataMap() {
		return this.masterShipDataMap;
	}

	public Map<Integer, MasterSlotitemDataDto> getMasterSlotitemDataMap() {
		return this.masterSlotitemDataMap;
	}

	public Map<Integer, MasterMissionDataDto> getMasterMissionDataMap() {
		return this.masterMissionDataMap;
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
			return Integer.parseInt(this.json.getString("api_aftershipid"));
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

	public class MasterMissionDataDto {
		private final JsonObject json;

		public MasterMissionDataDto(JsonObject json) {
			this.json = json;
		}

		public int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}
	}

	public class MasterUserItemDto {
		private final JsonObject json;

		public MasterUserItemDto(JsonObject json) {
			this.json = json;
		}

		private int getId() {
			return this.json.getInt("api_id");
		}

		public String getName() {
			return this.json.getString("api_name");
		}
	}

}
