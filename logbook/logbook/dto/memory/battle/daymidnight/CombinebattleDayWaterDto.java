package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleDay;
import logbook.update.data.Data;

public class CombinebattleDayWaterDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public CombinebattleDayWaterDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 4;
	}
}
