package logbook.context.dto.battle.info;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractInfoBattleStartNext;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class InfoBattleStartDto extends AbstractInfoBattleStartNext {

	private final int deckId;
	private final int start;

	public InfoBattleStartDto(Data data, JsonObject json) {
		super(data, json);
		this.deckId = Integer.parseInt(data.getField("api_deck_id"));
		this.start = json.getInt("api_from_no");
	}

	public int getStart() {
		return this.start;
	}

	public int getDeckId() {
		return this.deckId;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_START;
	}

	@Override
	public boolean hasDownArrow(BattleDto pre) {
		return false;
	}
}
