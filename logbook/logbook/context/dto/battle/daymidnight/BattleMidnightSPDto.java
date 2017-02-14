package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.BattleType;

public class BattleMidnightSPDto extends AbstractBattleMidnight {

	public BattleMidnightSPDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.BATTLE_MIDNIGHT_SP;
	}

}
