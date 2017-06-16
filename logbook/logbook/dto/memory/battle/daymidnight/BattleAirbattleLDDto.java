package logbook.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleDay;
import logbook.update.data.Data;

public class BattleAirbattleLDDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public BattleAirbattleLDDto(Data data, JsonObject json) {
		super(data, json);
	}
}
