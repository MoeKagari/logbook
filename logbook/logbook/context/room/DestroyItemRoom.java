package logbook.context.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.GlobalContext;
import logbook.context.data.Data;
import logbook.gui.logic.TimeString;
import logbook.util.JsonUtils;
import logbook.util.ToolUtils;

public class DestroyItemRoom extends Room {

	public void doDestroyItem(Data data, JsonValue json) {
		try {
			long time = TimeString.getCurrentTime();
			String[] ids = data.getField("api_slotitem_ids").trim().split(",");
			ToolUtils.forEach(ids, id -> GlobalContext.destroyItem(time, "工厂废弃", Integer.valueOf(id), ids.length));

			int[] mm = JsonUtils.getIntArray((JsonObject) json, "api_get_material");
			GlobalContext.addMaterial(mm);
		} catch (Exception e) {
			this.getLog().get().warn("doDestroyItem" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
