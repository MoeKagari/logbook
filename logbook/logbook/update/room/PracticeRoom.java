package logbook.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.dto.memory.battle.practice.PracticeBattleDayDto;
import logbook.dto.memory.battle.practice.PracticeBattleMidnightDto;
import logbook.dto.memory.battle.practice.PracticeBattleResultDto;
import logbook.dto.word.PracticeEnemyDto;
import logbook.update.GlobalContext;
import logbook.update.data.Data;

public class PracticeRoom extends ApiRoom {
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
			GlobalContext.getMemorylist().add(new PracticeBattleDayDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeBattleDay" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPracticeBattleMidnight(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new PracticeBattleMidnightDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeBattleMidnight" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPracticeBattleResult(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new PracticeBattleResultDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doPracticeBattleResult" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}
}
