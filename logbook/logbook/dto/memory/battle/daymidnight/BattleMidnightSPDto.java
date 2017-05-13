package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleMidnight;
import logbook.update.data.Data;

public class BattleMidnightSPDto extends AbstractBattleMidnight {
	public BattleMidnightSPDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isMidnightOnly() {
		return true;
	}
}
