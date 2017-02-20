package logbook.context.dto.battle.daymidnight;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class CombineBattleEachDayWaterDto extends AbstractBattleDay {

	public CombineBattleEachDayWaterDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 4;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.COMBINEBATTLE_EACH_DAY_WATER;
	}
}
