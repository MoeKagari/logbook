package logbook.context.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.util.JsonUtils;

public class HokyoRoom extends Room {

	public void doCharge(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			jo.getJsonArray("api_ship").forEach(value -> GlobalContext.updateShip(((JsonObject) value).getInt("api_id"), ship -> ship.updateWhenCharge((JsonObject) value)));
			GlobalContext.setMaterial(JsonUtils.getIntArray(jo.getJsonArray("api_material")));
		} catch (Exception e) {
			this.getLog().get().warn("doCharge" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
