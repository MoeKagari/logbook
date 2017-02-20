package logbook.context.update.room;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.util.JsonUtils;
import logbook.util.ToolUtils;

public class KaisouRoom extends Room {
	public void doPowerup(Data data, JsonValue json) {
		try {
			String[] ids = data.getField("api_id_items").trim().split(",");
			ArrayList<String> message = new ArrayList<>();
			JsonObject jo = (JsonObject) json;
			ShipDto oldship = GlobalContext.getShipmap().get(Integer.parseInt(data.getField("api_id")));
			ShipDto newship = new ShipDto(jo.getJsonObject("api_ship"));

			boolean success = jo.getInt("api_powerup_flag") == 1;
			message.add("近代化改修" + (success ? "成功" : "失败"));
			if (oldship != null && success) {
				//添加console输出的message
			}

			long time = TimeString.getCurrentTime();
			ToolUtils.forEach(ids, id -> GlobalContext.destroyShip(time, "近代化改修", Integer.parseInt(id)));//remove 舰娘和其身上的装备
			message.forEach(mes -> ApplicationMain.main.logPrint(mes));
			GlobalContext.getShipmap().put(newship.getId(), newship);// add 舰娘,oldship和newship的getId()相同(理论上来说)
			ToolUtils.forEach(GlobalContext.getDeckRoom(), dr -> dr.doDeck(data, jo.get("api_deck")));//更新deck
		} catch (Exception e) {
			this.getLog().get().warn("doPowerup" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doSlotItemLock(Data data, JsonValue json) {
		try {
			int id = Integer.parseInt(data.getField("api_slotitem_id"));
			ItemDto item = GlobalContext.getItemMap().get(id);
			if (item != null) item.slotItemLock(((JsonObject) json).getInt("api_locked") == 1);
		} catch (Exception e) {
			this.getLog().get().warn("doSlotItemLock" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doShip3(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			JsonArray array = jo.getJsonArray("api_ship_data");
			array.forEach(value -> {
				ShipDto ship = new ShipDto((JsonObject) value);
				GlobalContext.getShipmap().put(ship.getId(), ship);
			});

			ToolUtils.forEach(GlobalContext.getDeckRoom(), dr -> dr.doDeck(data, jo.get("api_deck_data")));
		} catch (Exception e) {
			this.getLog().get().warn("doShip3" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doOpenSlotex(Data data, JsonValue json) {
		try {
			int shipId = Integer.parseInt(data.getField("api_id"));
			ShipDto ship = GlobalContext.getShipmap().get(shipId);
			if (ship != null) ship.openSlotex();
		} catch (Exception e) {
			this.getLog().get().warn("doOpenSlotex" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doSlotExchange(Data data, JsonValue json) {
		try {
			int shipId = Integer.parseInt(data.getField("api_id"));
			ShipDto ship = GlobalContext.getShipmap().get(shipId);
			if (ship != null) ship.slotExchange(JsonUtils.getIntArray(((JsonObject) json), "api_slot"));
		} catch (Exception e) {
			this.getLog().get().warn("doSlotExchange" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doSlotDeprive(Data data, JsonValue json) {
		try {
			JsonObject jo = ((JsonObject) json).getJsonObject("api_ship_data");
			ShipDto ship;
			//更新两艘船的information
			ship = new ShipDto(jo.getJsonObject("api_set_ship"));
			GlobalContext.getShipmap().put(ship.getId(), ship);
			ship = new ShipDto(jo.getJsonObject("api_unset_ship"));
			GlobalContext.getShipmap().put(ship.getId(), ship);
		} catch (Exception e) {
			this.getLog().get().warn("doSlotDeprive" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
