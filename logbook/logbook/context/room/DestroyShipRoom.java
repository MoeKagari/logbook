package logbook.context.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.GlobalContext;
import logbook.context.data.Data;
import logbook.gui.logic.TimeString;
import logbook.util.JsonUtils;

public class DestroyShipRoom extends Room {

	public void doDestroyShip(Data data, JsonValue json) {
		try {
			int id = Integer.parseInt(data.getField("api_ship_id"));
			long time = TimeString.getCurrentTime();
			GlobalContext.destroyShip(time, "工厂解体", id);

			int[] mm = JsonUtils.getIntArray((JsonObject) json, "api_material");
			GlobalContext.setMaterial(mm);
		} catch (Exception e) {
			this.getLog().get().warn("doDestroyShip" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
