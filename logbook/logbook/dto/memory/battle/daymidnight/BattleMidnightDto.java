package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleMidnight;
import logbook.update.data.Data;

public class BattleMidnightDto extends AbstractBattleMidnight {
	public BattleMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}
}
