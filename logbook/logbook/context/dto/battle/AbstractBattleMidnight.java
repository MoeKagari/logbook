package logbook.context.dto.battle;

import java.util.ArrayList;
import java.util.function.BiFunction;

import javax.json.JsonArray;
import javax.json.JsonObject;

import logbook.context.update.data.Data;
import logbook.util.JsonUtils;

public abstract class AbstractBattleMidnight extends AbstractBattle {
	private final int[] touchPlane;
	private final int[] flare;
	private final BattleMidnightStage battleMidnightStage;
	private int[] activeDeck = { 1, 1 };

	public AbstractBattleMidnight(Data data, JsonObject json) {
		super(json);

		this.flare = AbstractBattle.dissociateIntarray(json, "api_flare_pos");
		this.touchPlane = AbstractBattle.dissociateIntarray(json, "api_touch_plane");
		if (json.containsKey("api_active_deck")) this.activeDeck = AbstractBattle.dissociateIntarray(json, "api_active_deck");
		this.battleMidnightStage = new BattleMidnightStage(json.getJsonObject("api_hougeki"));
	}

	@Override
	public boolean isMidnight() {
		return true;
	}

	public BattleMidnightStage getBattleMidnightStage() {
		return this.battleMidnightStage;
	}

	public BattleDeck[] getActiveDeck() {
		BiFunction<Integer, BattleDeck[], BattleDeck> get = (index, bds) -> index == 1 ? bds[0] : (index == 2 ? bds[1] : null);
		return new BattleDeck[] {//
				get.apply(this.activeDeck[0], new BattleDeck[] { this.getfDeck(), this.getfDeckCombine() }),//
				get.apply(this.activeDeck[1], new BattleDeck[] { this.geteDeck(), this.geteDeckCombine() })//
		};
	}

	public boolean[] getTouchPlane() {
		return new boolean[] { this.touchPlane[0] > 0, this.touchPlane[1] > 0 };
	}

	public boolean[] getFlare() {
		return new boolean[] { this.flare[0] > 0, this.flare[1] > 0 };
	}

	public class BattleMidnightStage {
		private ArrayList<BattleOneAttack> battleAttacks = new ArrayList<>();
		private final BattleDeckAttackDamage fAttackDamage = new BattleDeckAttackDamage();
		private final BattleDeckAttackDamage eAttackDamage = new BattleDeckAttackDamage();

		public BattleMidnightStage(JsonObject json) {
			JsonArray at_list = json.getJsonArray("api_at_list");
			JsonArray df_list = json.getJsonArray("api_df_list");
			JsonArray damage = json.getJsonArray("api_damage");
			JsonArray sp_list = json.getJsonArray("api_sp_list");
			for (int x = 1; x < at_list.size(); x++) {
				int at_index = at_list.getInt(x);
				int[] df_index = JsonUtils.getIntArray(df_list.getJsonArray(x));
				int[] da = JsonUtils.getIntArray(damage.getJsonArray(x));
				int sp = sp_list.getInt(x);
				this.battleAttacks.add(new BattleOneAttack(at_index, df_index, da, sp));
			}

			BattleOneAttackSimulator boas = new BattleOneAttackSimulator();
			this.battleAttacks.forEach(boa -> boas.accept(boa, false));
			this.accept(boas);
		}

		private void accept(BattleOneAttackSimulator boas) {
			this.fAttackDamage.getDamage(boas.getFdmg());
			this.fAttackDamage.setAttack(boas.getFatt());
			this.eAttackDamage.getDamage(boas.getEdmg());
			this.eAttackDamage.setAttack(boas.getEatt());
		}

		public BattleDeckAttackDamage getfAttackDamage() {
			return this.fAttackDamage;
		}

		public BattleDeckAttackDamage geteAttackDamage() {
			return this.eAttackDamage;
		}

		public ArrayList<BattleOneAttack> getBattleAttacks() {
			return this.battleAttacks;
		}
	}

}
