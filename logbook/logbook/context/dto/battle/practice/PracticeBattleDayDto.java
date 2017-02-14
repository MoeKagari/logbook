package logbook.context.dto.battle.practice;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.BattleType;

public class PracticeBattleDayDto extends AbstractBattleDay {

	public PracticeBattleDayDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 4;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.PRACTICE_DAY;
	}

	@Override
	public boolean isPracticeBattle() {
		return true;
	}

}
