package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.BattleType;

public class CombinebattleECMidnightDto extends AbstractBattleMidnight {

	public CombinebattleECMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.COMBINEBATTLE_EC_MIDNIGHT;
	}

}
