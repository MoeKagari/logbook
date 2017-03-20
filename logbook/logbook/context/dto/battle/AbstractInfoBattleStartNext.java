package logbook.context.dto.battle;

import java.util.ArrayList;

import javax.json.JsonObject;

import logbook.context.update.data.Data;

public abstract class AbstractInfoBattleStartNext extends AbstractInfoBattle {
	private final int bossCellNo;
	private final int mapareaId;
	private final int mapareaNo;
	private final int next;
	private final int nextEventId;
	private final int nextEventKind;
	private final int nextCount;
	private final ArrayList<BattleStartNext_GetItem> items = new ArrayList<>();

	public AbstractInfoBattleStartNext(Data data, JsonObject json) {
		this.bossCellNo = json.getInt("api_bosscell_no");
		this.mapareaId = json.getInt("api_maparea_id");
		this.mapareaNo = json.getInt("api_mapinfo_no");
		this.next = json.getInt("api_no");
		this.nextEventId = json.getInt("api_event_id");
		this.nextEventKind = json.getInt("api_event_kind");
		this.nextCount = json.getInt("api_next");
		if (json.containsKey("api_itemget")) {
			json.getJsonArray("api_itemget").forEach(value -> this.items.add(new BattleStartNext_GetItem((JsonObject) value)));
		}
	}

	public ArrayList<BattleStartNext_GetItem> getItems() {
		return this.items;
	}

	public boolean isBoss() {
		return this.bossCellNo == this.next;
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

	public String getMap() {
		return this.mapareaId + "-" + this.mapareaNo;
	}

	public class BattleStartNext_GetItem {
		private final int id;
		private final int count;

		public BattleStartNext_GetItem(JsonObject json) {
			this.id = json.getInt("api_id");
			this.count = json.getInt("api_getcount");
		}

		@Override
		public String toString() {
			return this.getItemString() + "-" + this.count;
		}

		private String getItemString() {
			switch (this.id) {
				case 1:
					return "燃";
				case 2:
					return "弹";
				case 3:
					return "钢";
				case 4:
					return "铝";
				case 5:
					return "高速建造材";
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
