package logbook.context.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.gui.logic.TimeString;
import logbook.util.JsonUtils;
import logbook.util.ToolUtils;

public class KaisouRoom extends Room {

	public void doPowerup(Data data, JsonValue json) {
		try {
			String[] ids = data.getField("api_id_items").trim().split(",");
			JsonObject jo = (JsonObject) json;
			long time = TimeString.getCurrentTime();
			//boolean success = jo.getInt("api_powerup_flag") == 1;
			//ShipDto oldship = GlobalContext.getShip(Integer.parseInt(data.getField("api_id")));

			ToolUtils.forEach(ids, id -> GlobalContext.destroyShip(time, "近代化改修", Integer.parseInt(id)));//remove 舰娘和其身上的装备
			GlobalContext.addNewShip(jo.getJsonObject("api_ship"));
			ToolUtils.forEach(GlobalContext.deckRoom, dr -> dr.doDeck(data, jo.get("api_deck")));//更新deck
		} catch (Exception e) {
			this.getLog().get().warn("doPowerup" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doSlotItemLock(Data data, JsonValue json) {
		try {
			int id = Integer.parseInt(data.getField("api_slotitem_id"));
			ToolUtils.notNullThenHandle(GlobalContext.getItem(id), item -> item.slotItemLock(((JsonObject) json).getInt("api_locked") == 1));
		} catch (Exception e) {
			this.getLog().get().warn("doSlotItemLock" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doShip3(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			jo.getJsonArray("api_ship_data").forEach(GlobalContext::addNewShip);
			ToolUtils.forEach(GlobalContext.deckRoom, dr -> dr.doDeck(data, jo.get("api_deck_data")));
		} catch (Exception e) {
			this.getLog().get().warn("doShip3" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doOpenSlotex(Data data, JsonValue json) {
		try {
			GlobalContext.updateShip(Integer.parseInt(data.getField("api_id")), ShipDto::openSlotex);
		} catch (Exception e) {
			this.getLog().get().warn("doOpenSlotex" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doSlotExchange(Data data, JsonValue json) {
		try {
			int id = Integer.parseInt(data.getField("api_id"));
			int[] newSlots = JsonUtils.getIntArray(((JsonObject) json), "api_slot");
			GlobalContext.updateShip(id, ship -> ship.slotExchange(newSlots));
		} catch (Exception e) {
			this.getLog().get().warn("doSlotExchange" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doSlotDeprive(Data data, JsonValue json) {
		try {
			JsonObject jo = ((JsonObject) json).getJsonObject("api_ship_data");
			//更新两艘船的information
			GlobalContext.addNewShip(jo.getJsonObject("api_set_ship"));
			GlobalContext.addNewShip(jo.getJsonObject("api_unset_ship"));
		} catch (Exception e) {
			this.getLog().get().warn("doSlotDeprive" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
