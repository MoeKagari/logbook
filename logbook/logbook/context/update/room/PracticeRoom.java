package logbook.context.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.battle.practice.PracticeBattleDayDto;
import logbook.context.dto.battle.practice.PracticeBattleMidnightDto;
import logbook.context.dto.battle.practice.PracticeBattleResultDto;
import logbook.context.dto.data.PracticeEnemyDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;

public class PracticeRoom extends Room {

	public void doPracticeEnemyInfo(Data data, JsonValue json) {
		try {
			GlobalContext.setPracticeEnemy(new PracticeEnemyDto((JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeEnemyInfo" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

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
