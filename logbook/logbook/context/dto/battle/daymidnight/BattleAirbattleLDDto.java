package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class BattleAirbattleLDDto extends AbstractBattleDay {
	public BattleAirbattleLDDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.BATTLE_AIRBATTLE_LD;
	}
}
