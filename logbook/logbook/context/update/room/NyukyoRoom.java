package logbook.context.update.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.NdockDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;

public class NyukyoRoom extends Room {
	private final int id;
	private NdockDto ndock;

	public NyukyoRoom(int id) {
		this.id = id;
	}

	/*---------------------------------------------------------------------------------------------------------*/

	public void doNdock(Data data, JsonValue json) {
		try {
			for (JsonValue value : (JsonArray) json) {
				JsonObject jo = (JsonObject) value;
				if (jo.getInt("api_id") == this.id) {
					NdockDto old = this.ndock;
					this.ndock = new NdockDto(jo);
					if (old != null && old.getShipId() > 0 && old.getShipId() != this.ndock.getShipId()) {
						ShipDto ship = GlobalContext.getShipMap().get(old.getShipId());
						if (ship != null) ship.nyukyoEnd();
					}
					break;
				}
			}
		} catch (Exception e) {
			this.getLog().get().warn("doNdock" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doNyukyoStart(Data data, JsonValue json) {
		try {
			if (Integer.parseInt(data.getField("api_ndock_id")) != this.id) return;

			int shipId = Integer.parseInt(data.getField("api_ship_id"));
			boolean highspeed = Integer.parseInt(data.getField("api_highspeed")) == 1;
			ShipDto ship = GlobalContext.getShipMap().get(shipId);

			if (ship != null) GlobalContext.reduceMaterial(ship.getNyukyoCost());
			if (highspeed) {//使用高速修复,后无ndock
				GlobalContext.updateShip(shipId, ShipDto::nyukyoEnd);
				GlobalContext.reduceMaterial(new int[] { 0, 0, 0, 0, 0, 1, 0, 0 });
				this.ndock = null;
			} else {
				//不使用高速修复,后接ndock ,无需处理
				//即使想处理,response也没有数据
			}
		} catch (Exception e) {
			this.getLog().get().warn("doNyukyoStart" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doNyukyoSpeedchange(Data data, JsonValue json) {
		try {
			if (Integer.parseInt(data.getField("api_ndock_id")) != this.id) return;

			if (this.ndock != null) {
				GlobalContext.updateShip(this.ndock.getShipId(), ShipDto::nyukyoEnd);
				GlobalContext.reduceMaterial(new int[] { 0, 0, 0, 0, 0, 1, 0, 0 });
			}

			this.ndock = null;
		} catch (Exception e) {
			this.getLog().get().warn("doNyukyoSpeedchange" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	/*---------------------------------------------------------------------------------------------------------*/

	public NdockDto getNdock() {
		return this.ndock;
	}

}
