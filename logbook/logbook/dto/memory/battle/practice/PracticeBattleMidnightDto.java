package logbook.dto.memory.battle.practice;

import javax.json.JsonObject;

import logbook.dto.memory.battle.AbstractBattleMidnight;
import logbook.update.data.Data;

public class PracticeBattleMidnightDto extends AbstractBattleMidnight {
	private static final long serialVersionUID = 1L;

	public PracticeBattleMidnightDto(Data data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isPractice() {
		return true;
	}
}
