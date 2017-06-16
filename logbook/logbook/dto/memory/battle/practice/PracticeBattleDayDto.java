package logbook.dto.memory.battle.practice;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleDay;
import logbook.update.data.Data;

public class PracticeBattleDayDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public PracticeBattleDayDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 4;
	}

	@Override
	public boolean isPractice() {
		return true;
	}
}
