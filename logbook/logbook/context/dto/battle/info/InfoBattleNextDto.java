package logbook.context.dto.battle.info;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractInfoBattleStartNext;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class InfoBattleNextDto extends AbstractInfoBattleStartNext {

	public InfoBattleNextDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_NEXT;
	}

}
