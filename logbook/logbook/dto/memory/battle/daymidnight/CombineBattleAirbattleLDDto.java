package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleDay;
import logbook.update.data.Data;

public class CombineBattleAirbattleLDDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public CombineBattleAirbattleLDDto(Data data, JsonObject json) {
		super(data, json);
	}
}
