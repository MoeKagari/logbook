package logbook.context.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.GlobalContext;
import logbook.context.data.Data;
import logbook.context.dto.data.record.MissionResultDto;
import logbook.gui.logic.TimeString;

public class MissionRoom extends Room {

	public void doMissionResulut(Data data, JsonValue json) {
		try {
			MissionResultDto missionResult = new MissionResultDto((JsonObject) json, TimeString.getCurrentTime());
			GlobalContext.getMissionresulutlist().add(missionResult);
		} catch (Exception e) {
			this.getLog().get().warn("doMissionResulut" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
