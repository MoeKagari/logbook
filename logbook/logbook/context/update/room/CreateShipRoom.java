package logbook.context.update.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.KdockDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.data.record.CreateshipDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.gui.logic.TimeString;
import logbook.util.ToolUtils;

public class CreateShipRoom extends Room {
	private final int id;
	private KdockDto kdock = null;
	private CreateshipDto createshipDto;//当前的建造信息,用于createship-kdock-material的api链

	public CreateShipRoom(int id) {
		this.id = id;
	}

	public void doKdock(Data data, JsonValue json) {
		try {
			JsonArray array = (JsonArray) json;
			for (JsonValue value : array) {
				JsonObject jo = (JsonObject) value;
				if (jo.getInt("api_id") == this.id) {
					this.kdock = new KdockDto(jo);
					break;
				}
			}

			if (this.kdock != null && this.createshipDto != null) {//记录
				this.createshipDto.setShipId(this.kdock.getShipId());
				GlobalContext.getCreateshiplist().add(this.createshipDto);
			}
			this.createshipDto = null;
		} catch (Exception e) {
			this.getLog().get().warn("doKdock" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCreateship(Data data, JsonValue json) {
		try {
			if (Integer.parseInt(data.getField("api_kdock_id")) != this.id) return;

			boolean highspeed = Integer.parseInt(data.getField("api_highspeed")) == 1;
			boolean large_flag = Integer.parseInt(data.getField("api_large_flag")) == 1;
			int[] mm = { //
					Integer.parseInt(data.getField("api_item1")),//
					Integer.parseInt(data.getField("api_item2")),//
					Integer.parseInt(data.getField("api_item3")),//
					Integer.parseInt(data.getField("api_item4")),//
					highspeed ? (large_flag ? 10 : 1) : 0,//高速建造材
					0, Integer.parseInt(data.getField("api_item5")), 0 //
			};

			this.createshipDto = new CreateshipDto(mm, TimeString.getCurrentTime());
			//后接kdock,material,所以无需GlobalContext.reduceMaterial(mm);
		} catch (Exception e) {
			this.getLog().get().warn("doCreateship" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCreateshipSpeedchange(Data data, JsonValue json) {
		try {
			if (Integer.parseInt(data.getField("api_kdock_id")) != this.id) return;
			if (this.kdock == null) return;

			int[] mm = new int[8];
			boolean highspeed = Integer.parseInt(data.getField("api_highspeed")) == 1;
			boolean large_flag = this.kdock.largeFlag();
			mm[4] = highspeed ? (large_flag ? 10 : 1) : 0;//高速建造材

			GlobalContext.reduceMaterial(mm);
		} catch (Exception e) {
			this.getLog().get().warn("doCreateshipSpeedchange" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doGetShip(Data data, JsonValue json) {
		try {
			if (Integer.parseInt(data.getField("api_kdock_id")) != this.id) return;

			JsonObject jo = (JsonObject) json;

			//加入新船到shipmap
			ShipDto ship = new ShipDto(jo.getJsonObject("api_ship"));
			GlobalContext.getShipmap().put(ship.getId(), ship);

			//加入新船的装备到itemmap
			jo.getJsonArray("api_slotitem").forEach(value -> {
				int id = ((JsonObject) value).getInt("api_id");
				int slotitemId = ((JsonObject) value).getInt("api_slotitem_id");
				ItemDto item = new ItemDto(id, slotitemId, false, 0);
				GlobalContext.getItemMap().put(item.getId(), item);
			});

			//刷新kdock状态
			ToolUtils.forEach(GlobalContext.getCreateShipRoom(), csr -> csr.doKdock(data, jo.get("api_kdock")));
		} catch (Exception e) {
			this.getLog().get().warn("doGetShip" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	/*---------------------------------------------------------------------------------------------------------*/

	public KdockDto getKdock() {
		return this.kdock;
	}

}