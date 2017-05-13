package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleDay;
import logbook.update.data.Data;

public class CombineBattleAirbattleDto extends AbstractBattleDay {
	public CombineBattleAirbattleDto(Data data, JsonObject json) {
		super(data, json);
	}
}
