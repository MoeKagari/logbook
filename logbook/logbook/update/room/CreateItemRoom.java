package logbook.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.dto.memory.CreateItemDto;
import logbook.gui.logic.TimeString;
import logbook.update.GlobalContext;
import logbook.update.data.Data;

public class CreateItemRoom extends ApiRoom {
	public void doCreateitem(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			boolean success = jo.getInt("api_create_flag") == 1;
			int slotitemId = success ? GlobalContext.addNewItem(jo.getJsonObject("api_slot_item")).getSlotitemId() : -1;

			int[] mm = { //
					Integer.parseInt(data.getField("api_item1")),//
					Integer.parseInt(data.getField("api_item2")),//
					Integer.parseInt(data.getField("api_item3")),//
					Integer.parseInt(data.getField("api_item4")),//
					0, 0, success ? 1 : 0, 0//
			};

			GlobalContext.reduceMaterial(mm);
			GlobalContext.getMemorylist().add(new CreateItemDto(TimeString.getCurrentTime(), success, mm, slotitemId));
		} catch (Exception e) {
			this.getLog().get().warn("doCreateitem" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}
}
