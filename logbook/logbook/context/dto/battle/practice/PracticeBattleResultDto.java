package logbook.context.dto.battle.practice;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.BattleType;

public class PracticeBattleResultDto extends AbstractInfoBattleResult {

	public PracticeBattleResultDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.PRACTICE_RESULT;
	}

}
