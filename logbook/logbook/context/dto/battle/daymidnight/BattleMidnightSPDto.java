package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class BattleMidnightSPDto extends AbstractBattleMidnight {

	public BattleMidnightSPDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isMidnightOnly() {
		return true;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.BATTLE_MIDNIGHT_SP;
	}

}
