package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class BattleAirbattleDto extends AbstractBattleDay {
	public BattleAirbattleDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.BATTLE_AIRBATTLE;
	}
}
