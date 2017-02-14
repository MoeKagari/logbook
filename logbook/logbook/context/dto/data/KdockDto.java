package logbook.context.dto.data;

import javax.json.JsonObject;

/**
 * 建造位
 * @author MoeKagari
 *
 */
public class KdockDto {

	private final JsonObject json;

	public KdockDto(JsonObject json) {
		this.json = json;
	}

	public int getShipId() {
		return this.json.getInt("api_created_ship_id");
	}

	public long getTime() {
		return this.json.getJsonNumber("api_complete_time").longValue();
	}

	/**
	 * -1=未开启,0=未使用,3=建造完成,2=建造中
	 */
	public int getState() {
		return this.json.getInt("api_state");
	}

	public boolean largeFlag() {
		return this.json.getInt("api_item1") >= 1000;
	}

}
