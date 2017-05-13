package logbook.dto.memory.battle.info;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractInfoBattleStartNext;
import logbook.update.data.Data;

public class InfoBattleNextDto extends AbstractInfoBattleStartNext {
	public InfoBattleNextDto(Data data, JsonObject json) {
		super(data, json);
	}
}
