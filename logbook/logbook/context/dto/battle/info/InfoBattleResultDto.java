package logbook.context.dto.battle.info;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.BattleType;

public class InfoBattleResultDto extends AbstractInfoBattleResult {

	public InfoBattleResultDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_RESULT;
	}

}
