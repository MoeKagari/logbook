package logbook.context.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.GlobalContext;
import logbook.context.data.Data;
import logbook.context.dto.battle.practice.PracticeBattleDayDto;
import logbook.context.dto.battle.practice.PracticeBattleMidnightDto;
import logbook.context.dto.battle.practice.PracticeBattleResultDto;

public class BattlePracticeRoom extends Room {

	public void doPracticeBattleDay(Data data, JsonValue json) {
		try {
			GlobalContext.getBattlelist().add(new PracticeBattleDayDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeBattleDay" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPracticeBattleMidnight(Data data, JsonValue json) {
		try {
			GlobalContext.getBattlelist().add(new PracticeBattleMidnightDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeBattleMidnight" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPracticeBattleResult(Data data, JsonValue json) {
		try {
			GlobalContext.getBattlelist().add(new PracticeBattleResultDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeBattleResult" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
