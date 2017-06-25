package logbook.dto.word;

import javax.json.JsonArray;
import javax.json.JsonObject;

import logbook.dto.AbstractWord;
import logbook.dto.translator.MasterDataTranslator;

/**
 * 演习对象
 * @author MoeKagari
 */
public class PracticeEnemyDto extends AbstractWord {
	private final PracticeEnemyShip[] ships;

	public PracticeEnemyDto(JsonObject json) {
		JsonArray array = json.getJsonObject("api_deck").getJsonArray("api_ships");
		int len = array.size();
		this.ships = new PracticeEnemyShip[len];
		for (int i = 0; i < len; i++) {
			this.ships[i] = new PracticeEnemyShip(array.getJsonObject(i));
		}
	}

	public PracticeEnemyShip[] getShips() {
		return this.ships;
	}

	public class PracticeEnemyShip {
		private final int id;
		private final int lv;
		private final String name;

		public PracticeEnemyShip(JsonObject json) {
			this.id = json.getInt("api_id");
			this.lv = json.getInt("api_level", 0);
			this.name = this.exist() ? MasterDataTranslator.getShipName(json.getInt("api_ship_id")) : "";
		}

		public boolean exist() {
			return this.id != -1;
		}

		public int getLv() {
			return this.lv;
		}

		public String getName() {
			return this.name;
		}
	}
}
