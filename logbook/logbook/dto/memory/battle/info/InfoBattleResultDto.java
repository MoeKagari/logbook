package logbook.dto.memory.battle.info;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractInfoBattleResult;
import logbook.update.data.Data;

public class InfoBattleResultDto extends AbstractInfoBattleResult {
	public InfoBattleResultDto(Data data, JsonObject json) {
		super(data, json);
	}
}
