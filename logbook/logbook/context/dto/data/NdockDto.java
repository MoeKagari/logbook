package logbook.context.dto.data;

import javax.json.JsonObject;

/**
 * 渠位
 * @author MoeKagari
 */
public class NdockDto {

	private final JsonObject json;

	public NdockDto(JsonObject json) {
		this.json = json;
	}

	public int getState() {
		return this.json.getInt("api_state");
	}

	public int getShipId() {
		return this.json.getInt("api_ship_id");
	}

	public long getTime() {
		return this.json.getJsonNumber("api_complete_time").longValue();
	}

}
