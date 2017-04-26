package logbook.context.dto.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.update.data.Data;

public abstract class AbstractInfoBattleStartNext extends AbstractInfoBattle {
	private final int mapareaId;
	private final int mapareaNo;
	private final int next;
	private final int nextEventId;
	private final int nextEventKind;
	private final int nextCount;
	private List<BattleStartNext_GetItem> items = null;

	public AbstractInfoBattleStartNext(Data data, JsonObject json) {
		this.mapareaId = json.getInt("api_maparea_id");
		this.mapareaNo = json.getInt("api_mapinfo_no");
		this.next = json.getInt("api_no");
		this.nextEventId = json.getInt("api_event_id");
		this.nextEventKind = json.getInt("api_event_kind");
		this.nextCount = json.getInt("api_next");

		//获得的道具
		if (json.containsKey("api_itemget")) {
			JsonValue api_itemget = json.get("api_itemget");
			if (api_itemget instanceof JsonArray) {//资源点
				this.items = ((JsonArray) api_itemget).stream().map(BattleStartNext_GetItem::new).collect(Collectors.toList());
			} else if (api_itemget instanceof JsonObject) {//航空侦察点
				this.items = new ArrayList<>();
				this.items.add(new BattleStartNext_GetItem(api_itemget));
			}
		}
	}

	public List<BattleStartNext_GetItem> getItems() {
		return this.items;
	}

	public boolean isBoss() {
		return this.nextEventId == 5;
	}

	public boolean isGoal() {
		return this.nextCount == 0;
	}

	public int getNext() {
		return this.next;
	}

	public String getNextType() {
		return BattleDto.getNextPointType(this.nextEventId, this.nextEventKind);
	}

	protected int getMapareaId() {
		return this.mapareaId;
	}

	protected int getMapareaNo() {
		return this.mapareaNo;
	}

	public int getMap() {
		return this.mapareaId * 10 + this.mapareaNo;
	}

	public String getMapString() {
		return this.mapareaId + "-" + this.mapareaNo;
	}

	public class BattleStartNext_GetItem {
		private final int id;
		private final int count;

		public BattleStartNext_GetItem(JsonObject json) {
			this.id = json.getInt("api_id");
			this.count = json.getInt("api_getcount");
		}

		public BattleStartNext_GetItem(JsonValue value) {
			this((JsonObject) value);
		}

		@Override
		public String toString() {
			return this.getItemString() + "-" + this.count;
		}

		private String getItemString() {
			switch (this.id) {
				case 1:
					return "油";
				case 2:
					return "弹";
				case 3:
					return "钢";
				case 4:
					return "铝";
				case 5:
					return "高速建造材";
				case 6:
					return "高速修复材";
				case 7:
					return "开发资材";
				case 11:
					return "家具箱(中)";
				case 12:
					return "家具箱(大)";
				default:
					return String.valueOf(this.id);
			}
		}
	}

}
