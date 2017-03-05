package logbook.context.update.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.AirbaseDto;
import logbook.context.dto.data.BasicDto;
import logbook.context.dto.data.MapinfoDto;
import logbook.context.dto.data.MasterDataDto;
import logbook.context.dto.data.MaterialDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.util.ToolUtils;

public class MainRoom extends Room {

	public void doPort(Data data, JsonValue json) {
		try {
			JsonObject apidata = (JsonObject) json;
			this.doBasic(data, apidata.get("api_basic"));
			this.doPort_Ship(apidata.getJsonArray("api_ship"));
			this.doMaterial(data, apidata.get("api_material"));
			ToolUtils.forEach(GlobalContext.getNyukyoRoom(), nr -> nr.doNdock(data, apidata.get("api_ndock")));
			ToolUtils.forEach(GlobalContext.getDeckRoom(), dr -> dr.doDeck(data, apidata.get("api_deck_port")));
		} catch (Exception e) {
			this.getLog().get().warn("doPort" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doRequireInfo(Data data, JsonValue json) {
		try {
			JsonObject apidata = (JsonObject) json;
			this.doSlotItem(data, apidata.get("api_slot_item"));
			ToolUtils.forEach(GlobalContext.getCreateShipRoom(), csr -> csr.doKdock(data, apidata.get("api_kdock")));
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
			((JsonArray) json).forEach(value -> GlobalContext.addNewItem((JsonObject) value));
		} catch (Exception e) {
			this.getLog().get().warn("doSlotItem" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doUseitem(Data data, JsonValue json) {
		try {
			GlobalContext.getUseitemmap().clear();
			((JsonArray) json).forEach(value -> GlobalContext.addNewUseItem((JsonObject) value));
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
			GlobalContext.updateShip(Integer.parseInt(data.getField("api_ship_id")), ship -> ship.setLocked(((JsonObject) json).getInt("api_locked") == 1));
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

	/*---------------------------------------------------------------------------------------------------------*/

	private void doPort_Ship(JsonArray array) {
		GlobalContext.getShipMap().clear();
		array.forEach(value -> GlobalContext.addNewShip((JsonObject) value));
	}

}
