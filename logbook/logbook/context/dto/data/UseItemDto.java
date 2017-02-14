package logbook.context.dto.data;

import javax.json.JsonObject;

public class UseItemDto {

	private int id;
	private int count;

	public UseItemDto(int id, int count) {
		this.id = id;
		this.count = count;
	}

	public UseItemDto(JsonObject json) {
		this.id = json.getInt("api_id");
		this.count = json.getInt("api_count");
	}

	public int getId() {
		return this.id;
	}

	public int getCount() {
		return this.count;
	}

}
