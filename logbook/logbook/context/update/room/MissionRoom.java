package logbook.context.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.record.MissionResultDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.gui.logic.TimeString;

public class MissionRoom extends Room {

	public void doMissionResulut(Data data, JsonValue json) {
		try {
			GlobalContext.getMissionlist().add(new MissionResultDto(Integer.parseInt(data.getField("api_deck_id")), (JsonObject) json, TimeString.getCurrentTime()));
		} catch (Exception e) {
			this.getLog().get().warn("doMissionResulut" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
