package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class CombinebattleMidnightDto extends AbstractBattleMidnight {
	public CombinebattleMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.COMBINEBATTLE_EC_MIDNIGHT;
	}
}
