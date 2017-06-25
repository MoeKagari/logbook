package logbook.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.update.GlobalContext;
import logbook.update.data.Data;
import logbook.utils.JsonUtils;

public class HokyoRoom extends ApiRoom {
	public void doCharge(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			jo.getJsonArray("api_ship").forEach(value -> {
				JsonObject temp = (JsonObject) value;
				GlobalContext.updateShip(temp.getInt("api_id"), ship -> ship.updateWhenCharge(temp));
			});
			GlobalContext.setMaterial(JsonUtils.getIntArray(jo.getJsonArray("api_material")));
		} catch (Exception e) {
			this.getLog().get().warn("doCharge" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}
}
