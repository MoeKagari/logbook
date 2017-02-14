package logbook.context.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.GlobalContext;
import logbook.context.data.Data;
import logbook.context.dto.data.ShipDto;
import logbook.util.JsonUtils;

public class HokyoRoom extends Room {

	public void doCharge(Data data, JsonValue json) {
		try {
			if (json instanceof JsonObject) {
				JsonObject jo = (JsonObject) json;

				this.updateShip(jo.get("api_ship"));
				this.updateMaterial(jo.get("api_material"));
			}
		} catch (Exception e) {
			this.getLog().get().warn("doCharge" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	/*---------------------------------------------------------------------------------------------------------*/

	public void updateShip(JsonValue jsonValue) {
		((JsonArray) jsonValue).forEach(value -> {
			JsonObject jo = (JsonObject) value;
			ShipDto ship = GlobalContext.getShipmap().get(jo.getInt("api_id"));
			if (ship != null) ship.updateWhenCharge(jo);
		});
	}

	public void updateMaterial(JsonValue jsonValue) {
		int[] mm = JsonUtils.getIntArray((JsonArray) jsonValue);
		GlobalContext.setMaterial(mm);
	}

}
