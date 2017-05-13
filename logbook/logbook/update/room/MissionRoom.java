package logbook.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.dto.memory.MissionResultDto;
import logbook.gui.logic.TimeString;
import logbook.update.GlobalContext;
import logbook.update.data.Data;

public class MissionRoom extends ApiRoom {
	public void doMissionResulut(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new MissionResultDto(Integer.parseInt(data.getField("api_deck_id")), (JsonObject) json, TimeString.getCurrentTime()));
		} catch (Exception e) {
			this.getLog().get().warn("doMissionResulut" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}
}
