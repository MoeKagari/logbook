package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class CombineBattleEachDayDto extends AbstractBattleDay {
	public CombineBattleEachDayDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 3;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.COMBINEBATTLE_EACH_DAY;
	}
}
