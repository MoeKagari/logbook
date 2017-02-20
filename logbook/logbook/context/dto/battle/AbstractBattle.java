package logbook.context.dto.battle;

import java.util.Arrays;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

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

		//索敌
		this.search = dissociateIntarray(json, "api_search");
		//阵型和航向,[自-阵型,敌-阵型,航向]
		this.formation = dissociateIntarray(json, "api_formation");

		//退避
		if (json.containsKey("api_escape_idx")) {
			ToolUtils.forEach(JsonUtils.getIntArray(json, "api_escape_idx"), index -> this.fDeck.getEscapes().add(index - 1));
		}
		if (json.containsKey("api_escape_idx_combined")) {
			ToolUtils.forEach(JsonUtils.getIntArray(json, "api_escape_idx_combined"), index -> this.fDeckCombine.getEscapes().add(index - 1));
		}
	}

	@Override
	public boolean hasDownArrow(BattleDto pre) {
		return pre != null && (pre instanceof AbstractInfoBattle);
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

	public static int[] dissociateIntarray(JsonObject json, String key) {
		int[] intArray = null;
		if (json.containsKey(key)) {
			JsonArray array = json.getJsonArray(key);
			intArray = new int[array.size()];
			for (int i = 0; i < array.size(); i++) {
				JsonValue value = array.get(i);
				switch (value.getValueType()) {
					case STRING:
						intArray[i] = Integer.parseInt(((JsonString) value).getString());
						break;
					case NUMBER:
						intArray[i] = ((JsonNumber) value).intValue();
						break;
					default:
						break;
				}
			}
		}
		return intArray;
	}

}
