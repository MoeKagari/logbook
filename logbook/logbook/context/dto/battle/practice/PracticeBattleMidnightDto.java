package logbook.context.dto.battle.practice;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.BattleType;

public class PracticeBattleMidnightDto extends AbstractBattleMidnight {

	public PracticeBattleMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.PRACTICE_MIDNIGHT;
	}

	@Override
	public boolean isPracticeBattle() {
		return true;
	}

}
