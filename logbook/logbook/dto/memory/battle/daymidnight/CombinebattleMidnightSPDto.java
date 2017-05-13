package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleMidnight;
import logbook.update.data.Data;

public class CombinebattleMidnightSPDto extends AbstractBattleMidnight {
	public CombinebattleMidnightSPDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isMidnightOnly() {
		return true;
	}
}
