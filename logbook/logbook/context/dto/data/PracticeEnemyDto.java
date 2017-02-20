package logbook.context.dto.data;

import javax.json.JsonArray;
import javax.json.JsonObject;

import logbook.context.dto.translator.ShipDtoTranslator;

/**
 * 演习对象
 * @author MoeKagari
 */
public class PracticeEnemyDto {

	private PracticeEnemyShip[] ships;

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
			if (this.exist()) {
				this.name = ShipDtoTranslator.getName(json.getInt("api_ship_id"));
			} else {
				this.name = "";
			}
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
