package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class BattleMidnightDto extends AbstractBattleMidnight {

	public BattleMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.BATTLE_MIDNIGHT;
	}

}
