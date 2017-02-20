package logbook.context.dto.battle.info;

import logbook.context.dto.battle.AbstractInfoBattle;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleType;

public class InfoBattleGobackPortDto extends AbstractInfoBattle {

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_GOBACKPORT;
	}

	@Override
	public boolean hasDownArrow(BattleDto pre) {
		return false;
	}
}
