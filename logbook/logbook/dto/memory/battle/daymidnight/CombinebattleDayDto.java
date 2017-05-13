package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleDay;
import logbook.update.data.Data;

public class CombinebattleDayDto extends AbstractBattleDay {
	public CombinebattleDayDto(Data data, JsonObject json) {
		super(data, json);

	}

	@Override
	protected int getRaigekiIndex() {
		return 2;
	}
}
