package logbook.context.update.room;

import java.util.function.Supplier;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.AirbaseDto;
import logbook.context.dto.data.BasicDto;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.MapinfoDto;
import logbook.context.dto.data.MasterDataDto;
import logbook.context.dto.data.MaterialDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.GlobalContext.FleetAkashiTimer;
import logbook.context.update.data.Data;
import logbook.gui.logic.TimeString;
import logbook.util.ToolUtils;

public class MainRoom extends Room {
	/** 最后一次返回母港 */
	private long lastUpdateTime = -1;

	private Supplier<int[]> getConds = () -> {
		DeckDto deck = GlobalContext.deckRoom[0].getDeck();
		if (deck != null) {
			int[] conds = new int[6];
			int[] ships = deck.getShips();
			for (int i = 0; i < 6; i++) {
				ShipDto ship = GlobalContext.getShip(ships[i]);
				conds[i] = (ship != null && ship.isNeedForPLUpdate()) ? ship.getCond() : -1;
			}
			return conds;
		}
		return null;
	};

	public void doPort(Data data, JsonValue json) {
		try {
			JsonObject apidata = (JsonObject) json;
			this.doBasic(data, apidata.get("api_basic"));
			this.doMaterial(data, apidata.get("api_material"));
			if (apidata.containsKey("api_combined_flag")) GlobalContext.setCombined(apidata.getInt("api_combined_flag") > 0);

			int[] oldconds = this.getConds.get();//第一舰队的疲劳(旧)
			long oldtime = this.lastUpdateTime;
			{
				this.lastUpdateTime = TimeString.getCurrentTime();
				GlobalContext.getShipMap().clear();
				apidata.getJsonArray("api_ship").forEach(GlobalContext::addNewShip);
				ToolUtils.forEach(GlobalContext.nyukyoRoom, nr -> nr.doNdock(data, apidata.get("api_ndock")));
				ToolUtils.forEach(GlobalContext.deckRoom, dr -> dr.doDeck(data, apidata.get("api_deck_port")));
			}
			int[] newconds = this.getConds.get();//第一舰队的疲劳(新)
			long newtime = this.lastUpdateTime;
			GlobalContext.updatePLTIME(oldtime, oldconds, newtime, newconds);

			ToolUtils.notNullThenHandle(GlobalContext.getAkashiTimer(), FleetAkashiTimer::resetWhenPort);
		} catch (Exception e) {
			this.getLog().get().warn("doPort" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doRequireInfo(Data data, JsonValue json) {
		try {
			JsonObject apidata = (JsonObject) json;
			this.doSlotItem(data, apidata.get("api_slot_item"));
			ToolUtils.forEach(GlobalContext.createShipRoom, csr -> csr.doKdock(data, apidata.get("api_kdock")));
			this.doUseitem(data, apidata.get("api_useitem"));
			//  api_unsetslot
		} catch (Exception e) {
			this.getLog().get().warn("doRequireInfo" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doMaterial(Data data, JsonValue json) {
		try {
			int[] mm = new int[8];
			((JsonArray) json).forEach(value -> {
				JsonObject jo = (JsonObject) value;
				mm[jo.getInt("api_id") - 1] = jo.getInt("api_value");
			});
			GlobalContext.setCurrentMaterial(new MaterialDto(mm));
		} catch (Exception e) {
			this.getLog().get().warn("doMaterial" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBasic(Data data, JsonValue json) {
		try {
			GlobalContext.setBasicInformation(new BasicDto((JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBasic" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doSlotItem(Data data, JsonValue json) {
		try {
			GlobalContext.getItemMap().clear();
			((JsonArray) json).forEach(GlobalContext::addNewItem);
		} catch (Exception e) {
			this.getLog().get().warn("doSlotItem" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doUseitem(Data data, JsonValue json) {
		try {
			GlobalContext.getUseitemMap().clear();
			((JsonArray) json).forEach(GlobalContext::addNewUseItem);
		} catch (Exception e) {
			this.getLog().get().warn("doUseitem" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doMapinfo(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;
			GlobalContext.setMapinfo(new MapinfoDto(jo.getJsonArray("api_map_info")));//地图详情
			GlobalContext.setAirbase(new AirbaseDto(jo.getJsonArray("api_air_base")));//路基详情
		} catch (Exception e) {
			this.getLog().get().warn("doMapinfo" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doMasterData(Data data, JsonValue json) {
		try {
			GlobalContext.setMasterData(new MasterDataDto((JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doMasterData" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doShipLock(Data data, JsonValue json) {
		try {
			int id = Integer.parseInt(data.getField("api_ship_id"));
			int lock_value = ((JsonObject) json).getInt("api_locked");
			GlobalContext.updateShip(id, ship -> ship.setLocked(lock_value == 1));
		} catch (Exception e) {
			this.getLog().get().warn("doShipLock" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doShip2(Data data, JsonValue json) {
		try {
			//TODO
		} catch (Exception e) {
			this.getLog().get().warn("doShip2" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
