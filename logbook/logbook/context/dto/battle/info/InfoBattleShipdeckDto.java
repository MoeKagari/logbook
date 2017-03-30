package logbook.context.dto.battle.info;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractInfoBattle;
import logbook.context.dto.battle.BattleType;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.data.Data;

public class InfoBattleShipdeckDto extends AbstractInfoBattle {
	private final boolean hasDapo;

	public InfoBattleShipdeckDto(Data data, JsonObject json) {
		this.hasDapo = json.getJsonArray("api_ship_data").stream().map(value -> new ShipDto((JsonObject) value)).anyMatch(ShipDtoTranslator::dapo);
	}

	public boolean hasDapo() {
		return this.hasDapo;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_SHIPDECK;
	}
}
