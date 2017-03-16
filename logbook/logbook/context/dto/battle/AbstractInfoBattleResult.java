package logbook.context.dto.battle;

import javax.json.JsonObject;

import logbook.context.update.data.Data;

public abstract class AbstractInfoBattleResult extends AbstractInfoBattle {
	private final String rank;

	private final String questName;
	private final String deckName;
	private final BattleResult_GetShip getShip;

	private final int mvp;
	private final int mvpCombined;

	public AbstractInfoBattleResult(Data data, JsonObject json) {
		this.rank = json.getString("api_win_rank");
		this.questName = json.getString("api_quest_name", null);
		this.deckName = json.getJsonObject("api_enemy_info").getString("api_deck_name");
		this.getShip = json.containsKey("api_get_ship") ? (new BattleResult_GetShip(json.getJsonObject("api_get_ship"))) : null;
		this.mvp = json.getInt("api_mvp");
		this.mvpCombined = json.containsKey("api_mvp_combined") ? json.getInt("api_mvp_combined") : -1;
	}

	@Override
	public boolean hasDownArrow(BattleDto pre) {
		return false;
	}

	public int getMvp() {
		return this.mvp;
	}

	public int getMvpCombined() {
		return this.mvpCombined;
	}

	public String getRank() {
		return BattleDto.getRank(this.rank);
	}

	public String getQuestName() {
		return this.questName;
	}

	public String getDeckName() {
		return this.deckName;
	}

	public boolean haveNewShip() {
		return this.getShip != null;
	}

	public BattleResult_GetShip getNewShip() {
		return this.getShip;
	}

	public class BattleResult_GetShip {
		private final int id;
		private final String type;
		private final String name;

		public BattleResult_GetShip(JsonObject json) {
			this.id = json.getInt("api_ship_id");
			this.type = json.getString("api_ship_type");
			this.name = json.getString("api_ship_name");
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getType() {
			return this.type;
		}
	}

}
