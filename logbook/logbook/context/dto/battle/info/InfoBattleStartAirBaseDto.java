package logbook.context.dto.battle.info;

import javax.json.JsonObject;

import logbook.context.dto.battle.AbstractInfoBattle;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleType;
import logbook.context.update.data.Data;

public class InfoBattleStartAirBaseDto extends AbstractInfoBattle {

	private final int strikePoint[][] = new int[][] { null, null, null };

	public InfoBattleStartAirBaseDto(Data data, JsonObject json) {
		String sp1 = data.getField("api_strike_point_1");
		String sp2 = data.getField("api_strike_point_2");
		String sp3 = data.getField("api_strike_point_3");
		String[][] pointss = new String[][] {//
				sp1 == null ? null : sp1.split(","),//
				sp2 == null ? null : sp2.split(","),//
				sp3 == null ? null : sp3.split(",")//
		};
		for (int i = 0; i < 3; i++) {
			if (pointss[i] != null) {
				int[] points = new int[pointss[i].length];
				for (int j = 0; j < points.length; j++) {
					points[j] = Integer.parseInt(pointss[i][j]);
				}
				this.strikePoint[i] = points;
			}
		}
	}

	public int[][] getStrikePoint() {
		return this.strikePoint;
	}

	@Override
	public BattleType getBattleType() {
		return BattleType.INFOBATTLE_START_AIR_BASE;
	}

	@Override
	public boolean hasDownArrow(BattleDto pre) {
		return false;
	}
}
