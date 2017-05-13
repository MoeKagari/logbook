package logbook.dto.memory.battle.info;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractInfoBattle;
import logbook.dto.translator.ShipDtoTranslator;
import logbook.dto.word.ShipDto;
import logbook.update.data.Data;

public class InfoBattleShipdeckDto extends AbstractInfoBattle {
	private final boolean hasDapo;

	public InfoBattleShipdeckDto(Data data, JsonObject json) {
		this.hasDapo = json.getJsonArray("api_ship_data").stream().map(ShipDto::new).anyMatch(ShipDtoTranslator::dapo);
	}

	public boolean hasDapo() {
		return this.hasDapo;
	}
}
