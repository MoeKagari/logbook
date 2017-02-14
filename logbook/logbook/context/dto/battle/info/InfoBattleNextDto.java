package logbook.context.dto.battle.info;

import javax.json.JsonObject;

import logbook.context.data.Data;
import logbook.context.dto.battle.AbstractInfoBattleNext;
import logbook.context.dto.battle.BattleType;

public class InfoBattleNextDto extends AbstractInfoBattleNext {

	public InfoBattleNextDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_NEXT;
	}

}
