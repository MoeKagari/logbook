package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleMidnight;
import logbook.update.data.Data;

public class CombinebattleMidnightDto extends AbstractBattleMidnight {
	public CombinebattleMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}
}
