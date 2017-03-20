package logbook.context.dto.battle;

import java.util.Arrays;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import logbook.config.AppConstants;
import logbook.context.dto.battle.info.InfoBattleStartAirBaseDto;
import logbook.context.dto.translator.DeckDtoTranslator;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.util.JsonUtils;
import logbook.util.ToolUtils;

public abstract class AbstractBattle extends BattleDto {
	private BattleDeck fDeck = null;
	private BattleDeck fDeckCombine = null;
	private BattleDeck eDeck = null;
	private BattleDeck eDeckCombine = null;

	private final int[] formation;
	private final int[] search;

	public AbstractBattle(JsonObject json) {
		//BattleDeck的初始化
		{
			int[] nowhps = JsonUtils.getIntArray(json, "api_nowhps");
			int[] maxhps = JsonUtils.getIntArray(json, "api_maxhps");
			this.fDeck = new BattleDeck(false, false, Arrays.copyOfRange(nowhps, 1, 7), Arrays.copyOfRange(maxhps, 1, 7));
			this.eDeck = new BattleDeck(false, true, Arrays.copyOfRange(nowhps, 7, 13), Arrays.copyOfRange(maxhps, 7, 13));
		}
		if (json.containsKey("api_nowhps_combined") && json.containsKey("api_maxhps_combined")) {
			int[] nowhps_combined = JsonUtils.getIntArray(json, "api_nowhps_combined");
			int[] maxhps_combined = JsonUtils.getIntArray(json, "api_maxhps_combined");
			switch (nowhps_combined.length) {
				case 1 + 12:
					this.eDeckCombine = new BattleDeck(true, true, Arrays.copyOfRange(nowhps_combined, 7, 13), Arrays.copyOfRange(maxhps_combined, 7, 13));
				case 1 + 6:
					this.fDeckCombine = new BattleDeck(true, false, Arrays.copyOfRange(nowhps_combined, 1, 7), Arrays.copyOfRange(maxhps_combined, 1, 7));
			}
		}

		//BattleDeck的舰娘名
		if (this.fDeckCombine != null) {//我方为联合舰队
			this.fDeck.setNames(DeckDtoTranslator.getShipNames(1));
			this.fDeckCombine.setNames(DeckDtoTranslator.getShipNames(2));
		} else {
			if (json.containsKey("api_deck_id")) {
				this.fDeck.setNames(DeckDtoTranslator.getShipNames(dissociateInt(json.get("api_deck_id"), 1)));
			} else if (json.containsKey("api_dock_id")) {
				this.fDeck.setNames(DeckDtoTranslator.getShipNames(dissociateInt(json.get("api_dock_id"), 1)));
			} else {
				this.fDeck.setNames(AppConstants.EMPTY_NAMES);
			}
		}
		{//敌方
			int[] ids = dissociateIntarray(json, "api_ship_ke");
			this.eDeck.setNames(ToolUtils.toStringArray(Arrays.copyOfRange(ids, 1, 7), ShipDtoTranslator::getName));
		}
		if (json.containsKey("api_ship_ke_combined")) {
			int[] ids = dissociateIntarray(json, "api_ship_ke_combined");
			this.eDeckCombine.setNames(ToolUtils.toStringArray(Arrays.copyOfRange(ids, 1, 7), ShipDtoTranslator::getName));
		}

		//索敌
		this.search = dissociateIntarray(json, "api_search");
		//阵型和航向,[自-阵型,敌-阵型,航向]
		this.formation = dissociateIntarray(json, "api_formation");

		//退避
		if (json.containsKey("api_escape_idx")) {
			ToolUtils.forEach(JsonUtils.getIntArray(json, "api_escape_idx"), index -> this.fDeck.escapes.add(index - 1));
		}
		if (json.containsKey("api_escape_idx_combined")) {
			ToolUtils.forEach(JsonUtils.getIntArray(json, "api_escape_idx_combined"), index -> this.fDeckCombine.escapes.add(index - 1));
		}
	}

	@Override
	public boolean hasDownArrow(BattleDto pre) {
		return pre != null && ((pre instanceof AbstractInfoBattle) || (pre instanceof InfoBattleStartAirBaseDto));
	}

	public boolean isMidnight() {
		return false;
	}

	public String[] getZhenxin() {
		return this.formation == null ? null
				: new String[] {//
						BattleDto.getZhenxin(this.formation[0]),//
						BattleDto.getZhenxin(this.formation[1])//
				};
	}

	public String getHangxiang() {
		return this.formation == null ? null : BattleDto.getHangxiang(this.formation[2]);
	}

	public String[] getSearch() {
		return this.search == null ? null
				: new String[] {//
						BattleDto.getSearch(this.search[0]),//
						BattleDto.getSearch(this.search[1])//
				};
	}

	public BattleDeck getfDeck() {
		return this.fDeck;
	}

	public BattleDeck getfDeckCombine() {
		return this.fDeckCombine;
	}

	public BattleDeck geteDeck() {
		return this.eDeck;
	}

	public BattleDeck geteDeckCombine() {
		return this.eDeckCombine;
	}

	protected static int[] dissociateIntarray(JsonObject json, String key) {
		int[] intArray = null;
		if (json.containsKey(key)) {
			JsonArray array = json.getJsonArray(key);
			intArray = new int[array.size()];
			for (int i = 0; i < array.size(); i++) {
				intArray[i] = dissociateInt(array.get(i), 0);
			}
		}
		return intArray;
	}

	protected static int dissociateInt(JsonValue value, int defaultValue) {
		switch (value.getValueType()) {
			case STRING:
				return Integer.parseInt(((JsonString) value).getString());
			case NUMBER:
				return ((JsonNumber) value).intValue();
			default:
				return defaultValue;
		}
	}

}
