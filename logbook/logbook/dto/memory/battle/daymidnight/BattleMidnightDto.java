package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleMidnight;
import logbook.update.data.Data;

public class BattleMidnightDto extends AbstractBattleMidnight {
	private static final long serialVersionUID = 1L;

	public BattleMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}
}
