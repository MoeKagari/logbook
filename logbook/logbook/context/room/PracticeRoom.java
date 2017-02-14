package logbook.context.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.GlobalContext;
import logbook.context.data.Data;
import logbook.context.dto.data.PracticeEnemyDto;

public class PracticeRoom extends Room {

	public void doPracticeEnemyInfo(Data data, JsonValue json) {
		try {
			GlobalContext.setPracticeEnemy(new PracticeEnemyDto((JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeEnemyInfo" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
