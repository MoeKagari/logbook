package logbook.update.room;

import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.dto.memory.battle.daymidnight.BattleAirbattleDto;
import logbook.dto.memory.battle.daymidnight.BattleAirbattleLDDto;
import logbook.dto.memory.battle.daymidnight.BattleDayDto;
import logbook.dto.memory.battle.daymidnight.BattleMidnightDto;
import logbook.dto.memory.battle.daymidnight.BattleMidnightSPDto;
import logbook.dto.memory.battle.daymidnight.CombineBattleAirbattleDto;
import logbook.dto.memory.battle.daymidnight.CombineBattleAirbattleLDDto;
import logbook.dto.memory.battle.daymidnight.CombineBattleEachDayDto;
import logbook.dto.memory.battle.daymidnight.CombineBattleEachDayWaterDto;
import logbook.dto.memory.battle.daymidnight.CombinebattleDayDto;
import logbook.dto.memory.battle.daymidnight.CombinebattleDayWaterDto;
import logbook.dto.memory.battle.daymidnight.CombinebattleECDayDto;
import logbook.dto.memory.battle.daymidnight.CombinebattleECMidnightDto;
import logbook.dto.memory.battle.daymidnight.CombinebattleMidnightDto;
import logbook.dto.memory.battle.daymidnight.CombinebattleMidnightSPDto;
import logbook.dto.memory.battle.info.InfoBattleGobackPortDto;
import logbook.dto.memory.battle.info.InfoBattleNextDto;
import logbook.dto.memory.battle.info.InfoBattleResultDto;
import logbook.dto.memory.battle.info.InfoBattleShipdeckDto;
import logbook.dto.memory.battle.info.InfoBattleStartAirBaseDto;
import logbook.dto.memory.battle.info.InfoBattleStartDto;
import logbook.dto.memory.battle.info.InfoCombinebattleResultDto;
import logbook.update.GlobalContext;
import logbook.update.data.Data;
import logbook.util.ToolUtils;

public class BattleRoom extends ApiRoom {

	public void doBattleStart(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new InfoBattleStartDto(GlobalContext.isCombined(), data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleStart" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleDay(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new BattleDayDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleDay" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleMidnight(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new BattleMidnightDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleMidnight" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleAirbattle(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new BattleAirbattleDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleAirbattle" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleAirbattleLD(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new BattleAirbattleLDDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleAirbattleLD" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleMidnightSP(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new BattleMidnightSPDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleMidnight_SP" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleResult(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new InfoBattleResultDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleResult" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleShipdeck(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;

			jo.getJsonArray("api_ship_data").forEach(GlobalContext::addNewShip);
			ToolUtils.forEach(GlobalContext.deckRoom, dr -> dr.doDeck(data, jo.get("api_deck_data")));
			GlobalContext.getMemorylist().add(new InfoBattleShipdeckDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleShipdeck" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleNext(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new InfoBattleNextDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleNext" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doBattleStartAirBase(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new InfoBattleStartAirBaseDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doBattleStartAirBase" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	/*---------------------------------------------------------------*/

	public void doCombinebattleAirbattle(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombineBattleAirbattleDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleAirbattle" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleAirbattleLD(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombineBattleAirbattleLDDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleAirbattleLD" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleDay(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombinebattleDayDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleDay" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleDayWater(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombinebattleDayWaterDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleDayWater" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleMidnight(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombinebattleMidnightDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleMidnight" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleECDay(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombinebattleECDayDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleECDay" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleEachDay(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombineBattleEachDayDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleEachDay" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleEachDayWater(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombineBattleEachDayWaterDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleEachDayWater" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleECMidnight(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombinebattleECMidnightDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleECMidnight" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleMidnightSP(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new CombinebattleMidnightSPDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleMidnightSP" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleResult(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new InfoCombinebattleResultDto(data, (JsonObject) json));
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleECResult" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doCombinebattleGobackPort(Data data, JsonValue json) {
		try {
			GlobalContext.getMemorylist().add(new InfoBattleGobackPortDto());
		} catch (Exception e) {
			this.getLog().get().warn("doCombinebattleECResult" + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

}
