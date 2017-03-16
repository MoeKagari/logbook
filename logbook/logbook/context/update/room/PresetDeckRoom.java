package logbook.context.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.PresetDeckDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;

/**
 * 编成记录
 * @author MoeKagari
 */
public class PresetDeckRoom extends Room {

	private int max = -1;

	public int getMax() {
		return this.max;
	}

	public void doPresetDeck(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			this.max = jo.getInt("api_max_num");

			GlobalContext.getPresetdecklist().clear();
			jo.getJsonObject("api_deck").forEach((no, value) -> GlobalContext.getPresetdecklist().add(new PresetDeckDto(Integer.parseInt(no), (JsonObject) value)));
		} catch (Exception e) {
			this.getLog().get().warn("doPresetDeck" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPresetRegister(Data data, JsonValue json) {
		try {
			final int no = Integer.parseInt(data.getField("api_preset_no"));
			GlobalContext.getPresetdecklist().removeIf(pdd -> pdd.getNo() == no);
			GlobalContext.getPresetdecklist().add(new PresetDeckDto(no, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPresetRegister" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPresetDelete(Data data, JsonValue json) {
		try {
			final int no = Integer.parseInt(data.getField("api_preset_no"));
			GlobalContext.getPresetdecklist().removeIf(pdd -> pdd.getNo() == no);
		} catch (Exception e) {
			this.getLog().get().warn("doPresetDelete" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
