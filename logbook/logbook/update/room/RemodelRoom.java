package logbook.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.dto.word.ItemDto;
import logbook.dto.word.MaterialDto;
import logbook.gui.logic.TimeString;
import logbook.update.GlobalContext;
import logbook.update.data.Data;
import logbook.utils.JsonUtils;
import logbook.utils.ToolUtils;

public class RemodelRoom extends ApiRoom {
	public void doRemodelSlot(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			//boolean certain = Integer.parseInt(data.getField("api_certain_flag")) == 1;
			boolean success = jo.getInt("api_remodel_flag") == 1;
			int slotId = Integer.parseInt(data.getField("api_slot_id"));
			ItemDto item = GlobalContext.getItem(slotId);
			if (success && item != null) {
				//item.remodelSlot();
				//由api_after_slot刷新
				//无需手动刷新
			}
			if (jo.containsKey("api_after_slot")) {
				GlobalContext.getItemMap().remove(slotId);
				GlobalContext.addNewItem(jo.getJsonObject("api_after_slot"));
			}
			if (jo.containsKey("api_use_slot_id")) {
				int[] useSlotIds = JsonUtils.getIntArray(jo, "api_use_slot_id");
				long time = TimeString.getCurrentTime();
				int group = useSlotIds.length;
				ToolUtils.forEach(useSlotIds, id -> GlobalContext.destroyItem(time, "改修消耗", id, group));
			}
			GlobalContext.setCurrentMaterial(new MaterialDto(JsonUtils.getIntArray(jo, "api_after_material")));
		} catch (Exception e) {
			this.getLog().get().warn("doRemodelSlot" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}
}
