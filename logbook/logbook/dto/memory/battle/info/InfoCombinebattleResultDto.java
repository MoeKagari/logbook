package logbook.dto.memory.battle.info;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractInfoBattleResult;
import logbook.update.data.Data;

public class InfoCombinebattleResultDto extends AbstractInfoBattleResult {
	public InfoCombinebattleResultDto(Data data, JsonObject json) {
		super(data, json);
	}
}
