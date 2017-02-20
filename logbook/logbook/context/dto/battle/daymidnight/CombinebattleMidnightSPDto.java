package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class CombinebattleMidnightSPDto extends AbstractBattleMidnight {

	public CombinebattleMidnightSPDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.COMBINEBATTLE_MIDNIGHT_SP;
	}

}
