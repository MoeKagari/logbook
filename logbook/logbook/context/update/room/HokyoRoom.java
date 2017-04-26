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

			GlobalContext.setMaterial(JsonUtils.getIntArray(jo.getJsonArray("api_material")));
			jo.getJsonArray("api_ship").forEach(value -> {
				JsonObject temp = (JsonObject) value;
				GlobalContext.updateShip(temp.getInt("api_id"), ship -> ship.updateWhenCharge(temp));
			});
		} catch (Exception e) {
			this.getLog().get().warn("doCharge" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}
}
