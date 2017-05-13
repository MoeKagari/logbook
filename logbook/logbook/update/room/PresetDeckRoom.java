package logbook.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.dto.word.PresetDeckDto;
import logbook.update.GlobalContext;
import logbook.update.data.Data;

public class PresetDeckRoom extends ApiRoom {
	public void doPresetDeck(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			GlobalContext.getPresetdecklist().setMax(jo.getInt("api_max_num"));
			GlobalContext.getPresetdecklist().init(jo.getJsonObject("api_deck"));
		} catch (Exception e) {
			this.getLog().get().warn("doPresetDeck" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPresetRegister(Data data, JsonValue json) {
		try {
			int no = Integer.parseInt(data.getField("api_preset_no"));
			GlobalContext.getPresetdecklist().add(new PresetDeckDto(no, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPresetRegister" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPresetDelete(Data data, JsonValue json) {
		try {
			int no = Integer.parseInt(data.getField("api_preset_no"));
			GlobalContext.getPresetdecklist().remove(no);
		} catch (Exception e) {
			this.getLog().get().warn("doPresetDelete" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}
}
