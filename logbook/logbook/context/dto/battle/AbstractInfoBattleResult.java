package logbook.context.dto.battle;

import javax.json.JsonObject;

import logbook.context.update.data.Data;

public abstract class AbstractInfoBattleResult extends AbstractInfoBattle {
	private final String rank;

	private final String questName;
	private final String deckName;

	private final boolean haveNewShip;
	private final int newShipId;
	private final String newShipTypeName;

	public AbstractInfoBattleResult(Data data, JsonObject json) {
		this.rank = json.getString("api_win_rank");

		this.questName = json.getString("api_quest_name", null);
		this.deckName = json.getJsonObject("api_enemy_info").getString("api_deck_name");

		if (json.containsKey("api_get_ship")) {
			JsonObject get_ship = json.getJsonObject("api_get_ship");
			this.haveNewShip = true;
			this.newShipId = get_ship.getInt("api_ship_id");
			this.newShipTypeName = get_ship.getString("api_ship_type") + "-" + get_ship.getString("api_ship_name");
		} else {
			this.haveNewShip = false;
			this.newShipId = -1;
			this.newShipTypeName = null;
		}
	}

	@Override
	public boolean hasDownArrow(BattleDto pre) {
		return false;
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
		return this.haveNewShip;
	}

	public int getNewShipId() {
		return this.newShipId;
	}

	public String getNewShipTypeName() {
		return this.newShipTypeName;
	}

}
