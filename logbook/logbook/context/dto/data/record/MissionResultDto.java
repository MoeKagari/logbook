package logbook.context.dto.data.record;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.util.JsonUtils;

/**
 * 远征结果
 * @author MoeKagari
 *
 */
public class MissionResultDto implements RecordDto {

	private final int state;//0=失败,1=成功,2=大成功
	private final String area;
	private final String name;
	private final int[] material;
	private final MissionResultItem[] items = new MissionResultItem[] { null, null };
	private final long time;

	public MissionResultDto(JsonObject json, long time) {
		this.time = time;
		this.state = json.getInt("api_clear_result");
		this.area = json.getString("api_maparea_name");
		this.name = json.getString("api_quest_name");

		JsonValue value = json.get("api_get_material");
		if (value instanceof JsonArray) {
			this.material = JsonUtils.getIntArray((JsonArray) value);
		} else {
			this.material = new int[] { 0, 0, 0, 0 };
		}

		value = json.get("api_useitem_flag");
		if (value instanceof JsonArray) {
			int[] flags = JsonUtils.getIntArray((JsonArray) value);
			ArrayList<MissionResultItem> mri = new ArrayList<>();
			for (int i = 0; i < flags.length; i++) {
				String key = "api_get_item" + (i + 1);
				if (json.containsKey(key)) {
					mri.add(new MissionResultItem(flags[i], (JsonObject) json.get(key)));
				}
			}
			switch (mri.size()) {
				case 3:
				case 2:
					this.items[1] = mri.get(1);
				case 1:
					this.items[0] = mri.get(0);
			}
		}
	}

	public int getState() {
		return this.state;
	}

	public String getStateString() {
		switch (this.state) {
			case 0:
				return "失败";
			case 1:
				return "成功";
			case 2:
				return "大成功";
			default:
				return Integer.toString(this.state);
		}
	}

	public String getArea() {
		return this.area;
	}

	public String getName() {
		return this.name;
	}

	public int[] getMaterial() {
		return this.material;
	}

	public MissionResultItem[] getItems() {
		return this.items;
	}

	public long getTime() {
		return this.time;
	}

	public static class MissionResultItem {
		private final int flag;//0=なし, 1=高速修復材, 2=高速建造材, 3=開発資材, 4=アイテム, 5=家具コイン

		private final int id;
		private final String name;
		private final int count;

		public MissionResultItem(int flag, JsonObject json) {
			this.flag = flag;
			this.id = json.getInt("api_useitem_id");
			this.name = json.getString("api_useitem_name", "");
			this.count = json.getInt("api_useitem_count");
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			switch (this.flag) {
				case 0:
					return "";
				case 1:
					return "高速修复材";
				case 2:
					return "高速建造材";
				case 3:
					return "开发资材";
				case 4:
					return this.name;
				case 5:
					return "家具币";
				default:
					return "";
			}
		}

		public int getCount() {
			return this.count;
		}

	}

}
