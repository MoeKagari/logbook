package logbook.context.dto.battle;

import javax.json.JsonObject;

import logbook.context.update.data.Data;

public abstract class AbstractInfoBattleStartNext extends AbstractInfoBattle {
	private final String map;
	private final int next;
	private final int nextEventId;
	private final int nextEventKind;
	private final boolean goal;

	public AbstractInfoBattleStartNext(Data data, JsonObject json) {
		this.map = json.getInt("api_maparea_id") + "-" + json.getInt("api_mapinfo_no");
		this.next = json.getInt("api_no");
		this.nextEventId = json.getInt("api_event_id");
		this.nextEventKind = json.getInt("api_event_kind");
		this.goal = json.getInt("api_next") == 0;
	}

	public boolean isGoal() {
		return this.goal;
	}

	public int getNext() {
		return this.next;
	}

	public String getNextType() {
		return BattleDto.getNextPointType(this.nextEventId, this.nextEventKind);
	}

	public String getMap() {
		return this.map;
	}

}
