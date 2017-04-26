package logbook.context.dto.battle.practice;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class PracticeBattleResultDto extends AbstractInfoBattleResult {

	public PracticeBattleResultDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isPractice() {
		return true;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.PRACTICE_RESULT;
	}

}
