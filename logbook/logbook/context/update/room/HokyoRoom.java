package logbook.context.update.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.util.JsonUtils;

public class HokyoRoom extends Room {

	public void doCharge(Data data, JsonValue json) {
		try {
			if (json instanceof JsonObject) {
				JsonObject jo = (JsonObject) json;

				this.updateShip(jo.getJsonArray("api_ship"));
				GlobalContext.setMaterial(JsonUtils.getIntArray(jo.getJsonArray("api_material")));
			}
		} catch (Exception e) {
			this.getLog().get().warn("doCharge" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	/*---------------------------------------------------------------------------------------------------------*/

	public void updateShip(JsonArray jsonArray) {
		jsonArray.forEach(value -> {
			JsonObject jo = (JsonObject) value;
			ShipDto ship = GlobalContext.getShipmap().get(jo.getInt("api_id"));
			if (ship != null) ship.updateWhenCharge(jo);
		});
	}

}
