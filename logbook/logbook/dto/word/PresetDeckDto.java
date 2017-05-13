package logbook.dto.word;

import javax.json.JsonObject;

import logbook.dto.AbstractWord;
import logbook.util.JsonUtils;

public class PresetDeckDto extends AbstractWord {
	private final int no;
	private final String name;
	private final int[] ships;

	public PresetDeckDto(int no, JsonObject json) {
		this.no = no;
		this.name = json.getString("api_name");
		this.ships = JsonUtils.getIntArray(json, "api_ship");
	}

	public int getNo() {
		return this.no;
	}

	public String getName() {
		return this.name;
	}

	public int[] getShips() {
		return this.ships;
	}
}
