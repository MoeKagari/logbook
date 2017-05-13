package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleDay;
import logbook.update.data.Data;

public class BattleAirbattleDto extends AbstractBattleDay {
	public BattleAirbattleDto(Data data, JsonObject json) {
		super(data, json);
	}
}
