package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.BattleType;

public class CombinebattleResultDto extends AbstractInfoBattleResult {

	public CombinebattleResultDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.COMBINEBATTLE_RESULT;
	}

}
