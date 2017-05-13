package logbook.dto.memory.battle.info;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractInfoBattleStartNext;
import logbook.update.data.Data;

public class InfoBattleStartDto extends AbstractInfoBattleStartNext {
	private final boolean combined;//是否是联合舰队出击
	private final int deckId;//出击舰队,联合舰队时为1
	private final int start;//出击点

	public InfoBattleStartDto(boolean combined, Data data, JsonObject json) {
		super(data, json);
		this.combined = combined;
		this.deckId = Integer.parseInt(data.getField("api_deck_id"));
		this.start = json.getInt("api_from_no");
	}

	@Override
	public boolean isStart() {
		return true;
	}

	public boolean isCombined() {
		return this.combined;
	}

	public int getStart() {
		return this.start;
	}

	public int getDeckId() {
		return this.deckId;
	}
}
