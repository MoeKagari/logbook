package logbook.context.dto.battle.info;

import logbook.context.dto.battle.AbstractInfoBattle;
import logbook.context.dto.battle.BattleType;

public class InfoBattleShipDeckDto extends AbstractInfoBattle {

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_SHIPDECK;
	}

}
