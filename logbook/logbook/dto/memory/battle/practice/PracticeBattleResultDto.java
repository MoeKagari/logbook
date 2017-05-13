package logbook.dto.memory.battle.practice;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractInfoBattleResult;
import logbook.update.data.Data;

public class PracticeBattleResultDto extends AbstractInfoBattleResult {
	public PracticeBattleResultDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isPractice() {
		return true;
	}
}
