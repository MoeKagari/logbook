package logbook.context.dto.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import javax.json.JsonArray;
import javax.json.JsonObject;

import logbook.context.update.data.Data;
import logbook.util.JsonUtils;
import logbook.util.ToolUtils;

public abstract class AbstractBattleDay extends AbstractBattle {
	private ArrayList<BattleDayStage> battleDayStage = new ArrayList<>();

	public AbstractBattleDay(Data data, JsonObject json) {
		super(json);

		if (json.containsKey("api_air_base_injection")) {
			this.battleDayStage.add(new AirbaseInjection(json.getJsonObject("api_air_base_injection")));
		}
		if (json.containsKey("api_injection_kouku")) {
			this.battleDayStage.add(new InjectionKouko(json.getJsonObject("api_injection_kouku")));
		}
		if (json.containsKey("api_air_base_attack")) {
			JsonArray air_base_attack = json.getJsonArray("api_air_base_attack");
			for (int index = 0; index < air_base_attack.size(); index++) {
				this.battleDayStage.add(new AirbaseAttack(index, air_base_attack.getJsonObject(index)));
			}
		}
		if (json.containsKey("api_stage_flag")) {
			this.battleDayStage.add(new Kouko(1, JsonUtils.getIntArray(json, "api_stage_flag"), json.getJsonObject("api_kouku")));
		}
		if (json.containsKey("api_stage_flag2")) {
			this.battleDayStage.add(new Kouko(2, JsonUtils.getIntArray(json, "api_stage_flag2"), json.getJsonObject("api_kouku2")));
		}
		if (json.containsKey("api_support_flag") && json.getInt("api_support_flag") != 0) {
			this.battleDayStage.add(new SupportAttack(json.getInt("api_support_flag"), json.getJsonObject("api_support_info")));
		}
		if (json.containsKey("api_opening_taisen_flag") && json.getInt("api_opening_taisen_flag") == 1) {
			this.battleDayStage.add(new OpeningTaisen(json.getJsonObject("api_opening_taisen")));
		}
		if (json.containsKey("api_opening_flag") && json.getInt("api_opening_flag") == 1) {
			this.battleDayStage.add(new OpeningAttack(json.getJsonObject("api_opening_atack")));
		}
		if (json.containsKey("api_hourai_flag") && this.getRaigekiIndex() != -1) {
			int[] hourai_flags = JsonUtils.getIntArray(json, "api_hourai_flag");
			Runnable[] hougekis = new Runnable[] {//
					() -> this.battleDayStage.add(new Hougeki(1, json.getJsonObject("api_hougeki1"))),//
					() -> this.battleDayStage.add(new Hougeki(2, json.getJsonObject("api_hougeki2"))),//
					() -> this.battleDayStage.add(new Hougeki(3, json.getJsonObject("api_hougeki3")))//
			};
			int raigekiIndex = this.getRaigekiIndex() - 1;
			for (int index = 0; index < raigekiIndex; index++) {
				if (hourai_flags[index] == 1) {
					hougekis[index].run();
				}
			}
			if (hourai_flags[raigekiIndex] == 1) {
				this.battleDayStage.add(new Raigeki(json.getJsonObject("api_raigeki")));
			}
			for (int index = raigekiIndex; index < hougekis.length; index++) {
				if (hourai_flags[index + 1] == 1) {
					hougekis[index].run();
				}
			}
		}
	}

	public ArrayList<BattleDayStage> getBattleDayStage() {
		return this.battleDayStage;
	}

	public BattleDeckAttackDamage getfDeckAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.getfAttackDamage());
	}

	public BattleDeckAttackDamage getfDeckCombineAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.getfAttackDamagecombine());
	}

	public BattleDeckAttackDamage geteDeckAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.geteAttackDamage());
	}

	public BattleDeckAttackDamage geteDeckCombineAttackDamage() {
		return this.getBattleDeckAttackDamage(bds -> bds.geteAttackDamagecombine());
	}

	private BattleDeckAttackDamage getBattleDeckAttackDamage(Function<BattleDayStage, BattleDeckAttackDamage> mapper) {
		return this.battleDayStage.stream().map(mapper).reduce(new BattleDeckAttackDamage(), (orin, next) -> orin.add(next));
	}

	/*---------------------------------添加------------------------------------------*/

	/**
	 * 雷击战的index,1,2,3,4<br>
	 * 插入三次炮击战哪个位置<br>
	 * 默认-1为无三次炮击战以及雷击战
	 */
	protected int getRaigekiIndex() {
		return -1;
	}

	/*---------------------------------昼战的各个战斗阶段--------------------------------------------*/

	public abstract class BattleDayStage {
		private final BattleDeckAttackDamage fAttackDamage = new BattleDeckAttackDamage();
		private final BattleDeckAttackDamage eAttackDamage = new BattleDeckAttackDamage();
		private final BattleDeckAttackDamage fAttackDamageco = new BattleDeckAttackDamage();
		private final BattleDeckAttackDamage eAttackDamageco = new BattleDeckAttackDamage();
		private ArrayList<BattleOneAttack> battleAttacks = new ArrayList<>();

		public ArrayList<BattleOneAttack> getBattleAttacks() {
			return this.battleAttacks;
		}

		public BattleDeckAttackDamage getfAttackDamage() {
			return this.fAttackDamage;
		}

		public BattleDeckAttackDamage geteAttackDamage() {
			return this.eAttackDamage;
		}

		public BattleDeckAttackDamage getfAttackDamagecombine() {
			return this.fAttackDamageco;
		}

		public BattleDeckAttackDamage geteAttackDamagecombine() {
			return this.eAttackDamageco;
		}

		public String getStageName() {
			return this.getType().getName();
		}

		public void accept(BattleOneAttackSimulator boas) {
			this.fAttackDamage.getDamage(boas.getFdmg());
			this.fAttackDamage.setAttack(boas.getFatt());
			this.eAttackDamage.getDamage(boas.getEdmg());
			this.eAttackDamage.setAttack(boas.getEatt());
			this.fAttackDamageco.getDamage(boas.getFdmgco());
			this.fAttackDamageco.setAttack(boas.getFattco());
			this.eAttackDamageco.getDamage(boas.getEdmgco());
			this.eAttackDamageco.setAttack(boas.getEattco());
		}

		public abstract BattleDayStageType getType();
	}

	private enum BattleDayStageType {
		AIRBASEINJECTION("基地航空队-喷气机"),
		INJECTIONKOUKO("喷气机航空战"),
		AIRBASEATTACK("基地航空队"),
		KOUKO("航空战"),
		SUPPORTATTACK("支援舰队"),
		OPENINGTAISEN("开幕对潜"),
		OPENINGATTACK("开幕雷击"),
		HOUGEKI("炮击战"),
		RAIGEKI("雷击战");

		private final String name;

		BattleDayStageType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public class AirbaseInjection extends BattleDayStage {

		public AirbaseInjection(JsonObject json) {}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.AIRBASEINJECTION;
		}
	}

	public class InjectionKouko extends BattleDayStage {
		private final int[][] planeLostStage1 = new int[][] { null, null };
		private final int[][] planeLostStage2 = new int[][] { null, null };

		public InjectionKouko(JsonObject json) {
			{
				JsonObject stage1 = json.getJsonObject("api_stage1");
				this.planeLostStage1[0] = new int[] { stage1.getInt("api_f_count"), stage1.getInt("api_f_lostcount") };
				this.planeLostStage1[1] = new int[] { stage1.getInt("api_e_count"), stage1.getInt("api_e_lostcount") };
			}
			{
				JsonObject stage2 = json.getJsonObject("api_stage2");
				this.planeLostStage2[0] = new int[] { stage2.getInt("api_f_count"), stage2.getInt("api_f_lostcount") };
				this.planeLostStage2[1] = new int[] { stage2.getInt("api_e_count"), stage2.getInt("api_e_lostcount") };
			}

			int[] fdmg = new int[6];
			int[] edmg = new int[6];
			int[] fdmgco = new int[6];
			int[] edmgco = new int[6];
			{
				JsonObject stage3 = json.getJsonObject("api_stage3");
				double[] fdam = JsonUtils.getDoubleArray(stage3, "api_fdam");
				double[] edam = JsonUtils.getDoubleArray(stage3, "api_edam");
				for (int i = 1; i <= 6; i++) {
					fdmg[i - 1] += Math.floor(fdam[i]);
					edmg[i - 1] += Math.floor(edam[i]);
				}
			}
			if (json.containsKey("api_stage3_combined")) {
				JsonObject stage3_combined = json.getJsonObject("api_stage3_combined");
				if (stage3_combined.containsKey("api_fdam")) {
					double[] fdam = JsonUtils.getDoubleArray(stage3_combined, "api_fdam");
					for (int i = 1; i <= 6; i++) {
						fdmgco[i - 1] += Math.floor(fdam[i]);
					}
				}
				if (stage3_combined.containsKey("api_edam")) {
					double[] edam = JsonUtils.getDoubleArray(stage3_combined, "api_edam");
					for (int i = 1; i <= 6; i++) {
						edmgco[i - 1] += Math.floor(edam[i]);
					}
				}
			}
			this.getfAttackDamage().getDamage(fdmg);
			this.geteAttackDamage().getDamage(edmg);
			this.getfAttackDamagecombine().getDamage(fdmgco);
			this.geteAttackDamagecombine().getDamage(edmgco);
		}

		public int[][] getPlaneLostStage1() {
			return this.planeLostStage1;
		}

		public int[][] getPlaneLostStage2() {
			return this.planeLostStage2;
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.INJECTIONKOUKO;
		}
	}

	public class AirbaseAttack extends BattleDayStage {
		private final int index;
		private final int[][] planeLostStage1 = new int[][] { null, null };
		private final int[][] planeLostStage2 = new int[][] { null, null };

		public AirbaseAttack(int index, JsonObject json) {
			this.index = index;

			int[] stages = JsonUtils.getIntArray(json, "api_stage_flag");
			if (stages[0] == 1) {
				JsonObject stage1 = json.getJsonObject("api_stage1");
				this.planeLostStage1[0] = new int[] { stage1.getInt("api_f_count"), stage1.getInt("api_f_lostcount") };
				this.planeLostStage1[1] = new int[] { stage1.getInt("api_e_count"), stage1.getInt("api_e_lostcount") };
			}
			if (stages[1] == 1) {
				JsonObject stage2 = json.getJsonObject("api_stage2");
				this.planeLostStage2[0] = new int[] { stage2.getInt("api_f_count"), stage2.getInt("api_f_lostcount") };
				this.planeLostStage2[1] = new int[] { stage2.getInt("api_e_count"), stage2.getInt("api_e_lostcount") };
			}

			int[] edmg = new int[6];
			int[] edmgco = new int[6];
			if (stages[2] == 1) {
				JsonObject stage3 = json.getJsonObject("api_stage3");
				double[] edam = JsonUtils.getDoubleArray(stage3, "api_edam");
				for (int i = 1; i <= 6; i++) {
					edmg[i - 1] += Math.floor(edam[i]);
				}
			}
			if (stages[2] == 1 && json.containsKey("api_stage3_combined")) {
				JsonObject stage3_combined = json.getJsonObject("api_stage3_combined");
				double[] edam = JsonUtils.getDoubleArray(stage3_combined, "api_edam");
				for (int i = 1; i <= 6; i++) {
					edmgco[i - 1] += Math.floor(edam[i]);
				}
			}
			this.geteAttackDamage().getDamage(edmg);
			this.geteAttackDamagecombine().getDamage(edmgco);
		}

		public int getIndex() {
			return this.index;
		}

		public int[][] getPlaneLostStage1() {
			return this.planeLostStage1;
		}

		public int[][] getPlaneLostStage2() {
			return this.planeLostStage2;
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.AIRBASEATTACK;
		}
	}

	public class Kouko extends BattleDayStage {
		private final int index;
		private boolean[] stages = null;
		private Integer seiku = null;
		private int[] touch = null;
		private int[][] planeLostStage1 = new int[][] { null, null };
		private int[][] planeLostStage2 = new int[][] { null, null };

		public Kouko(int index, int[] flags, JsonObject json) {
			this.index = index;
			this.stages = new boolean[] { flags[0] == 1, flags[1] == 1, flags[2] == 1 };
			if (this.stages[0]) {
				JsonObject stage1 = json.getJsonObject("api_stage1");
				this.seiku = stage1.getInt("api_disp_seiku");
				this.touch = JsonUtils.getIntArray(stage1, "api_touch_plane");
				this.planeLostStage1[0] = new int[] { stage1.getInt("api_f_count"), stage1.getInt("api_f_lostcount") };
				this.planeLostStage1[1] = new int[] { stage1.getInt("api_e_count"), stage1.getInt("api_e_lostcount") };
			}
			if (this.stages[1]) {
				JsonObject stage2 = json.getJsonObject("api_stage2");
				this.planeLostStage2[0] = new int[] { stage2.getInt("api_f_count"), stage2.getInt("api_f_lostcount") };
				this.planeLostStage2[1] = new int[] { stage2.getInt("api_e_count"), stage2.getInt("api_e_lostcount") };
				//对空CI信息,在此
			}

			int[] fdmg = new int[6];
			int[] edmg = new int[6];
			int[] fdmgco = new int[6];
			int[] edmgco = new int[6];
			if (this.stages[2]) {
				JsonObject stage3 = json.getJsonObject("api_stage3");
				double[] fdam = JsonUtils.getDoubleArray(stage3, "api_fdam");
				double[] edam = JsonUtils.getDoubleArray(stage3, "api_edam");
				for (int i = 1; i <= 6; i++) {
					fdmg[i - 1] += Math.floor(fdam[i]);
					edmg[i - 1] += Math.floor(edam[i]);
				}
			}
			if (this.stages[2] && json.containsKey("api_stage3_combined")) {
				JsonObject stage3_combined = json.getJsonObject("api_stage3_combined");
				if (stage3_combined.containsKey("api_fdam")) {
					double[] fdam = JsonUtils.getDoubleArray(stage3_combined, "api_fdam");
					for (int i = 1; i <= 6; i++) {
						fdmgco[i - 1] += Math.floor(fdam[i]);
					}
				}
				if (stage3_combined.containsKey("api_edam")) {
					double[] edam = JsonUtils.getDoubleArray(stage3_combined, "api_edam");
					for (int i = 1; i <= 6; i++) {
						edmgco[i - 1] += Math.floor(edam[i]);
					}
				}
			}
			this.getfAttackDamage().getDamage(fdmg);
			this.geteAttackDamage().getDamage(edmg);
			this.getfAttackDamagecombine().getDamage(fdmgco);
			this.geteAttackDamagecombine().getDamage(edmgco);
		}

		public int getIndex() {
			return this.index;
		}

		public boolean[] getStages() {
			return this.stages;
		}

		public String getSeiku() {
			return this.seiku == null ? null : BattleDto.getSeiku(this.seiku);
		}

		public boolean[] getTouchPlane() {
			if (this.touch == null) return null;
			return new boolean[] { this.touch[0] > 0, this.touch[1] > 0 };
		}

		public int[][] getPlaneLostStage1() {
			return this.planeLostStage1;
		}

		public int[][] getPlaneLostStage2() {
			return this.planeLostStage2;
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.KOUKO;
		}
	}

	public class SupportAttack extends BattleDayStage {
		private final int type;
		//type = 1时(航空支援)
		private boolean[] stages;
		private int[][] planeLostStage1 = new int[][] { null, null };
		private int[] planeLostStage2 = null;//只有自方有损失

		public SupportAttack(int type, JsonObject json) {
			this.type = type;

			int[] damage = null;
			if (type == 1) {//航空支援
				JsonObject airattack = json.getJsonObject("api_support_airatack");
				int[] flags = JsonUtils.getIntArray(airattack, "api_stage_flag");
				this.stages = new boolean[] { flags[0] == 1, flags[1] == 1, flags[2] == 1 };
				if (this.stages[0]) {
					JsonObject stage1 = airattack.getJsonObject("api_stage1");
					this.planeLostStage1[0] = new int[] { stage1.getInt("api_f_count"), stage1.getInt("api_f_lostcount") };
					this.planeLostStage1[1] = new int[] { stage1.getInt("api_e_count"), stage1.getInt("api_e_lostcount") };
				}
				if (this.stages[1]) {
					JsonObject stage2 = airattack.getJsonObject("api_stage2");
					this.planeLostStage2 = new int[] { stage2.getInt("api_f_count"), stage2.getInt("api_f_lostcount") };
				}
				if (this.stages[2]) {
					JsonObject stage3 = airattack.getJsonObject("api_stage3");
					damage = ToolUtils.doubleToInteger(JsonUtils.getDoubleArray(stage3, "api_edam"), d -> (int) Math.floor(d));
				}
			} else if (type == 2 || type == 3) {//炮击支援或雷击支援
				JsonObject hourai = json.getJsonObject("api_support_hourai");
				damage = ToolUtils.doubleToInteger(JsonUtils.getDoubleArray(hourai, "api_damage"), d -> (int) Math.floor(d));
			}
			if (damage != null) {
				switch (damage.length) {
					case 1 + 12:
						this.geteAttackDamagecombine().getDamage(Arrays.copyOfRange(damage, 7, 13));
					case 1 + 6:
						this.geteAttackDamage().getDamage(Arrays.copyOfRange(damage, 1, 7));
				}
			}
		}

		public String getSupportType() {
			return BattleDto.getSupportType(this.type);
		}

		public boolean[] getStages() {
			return this.stages;
		}

		public int[][] getPlaneLostStage1() {
			return this.planeLostStage1;
		}

		public int[] getPlaneLostStage2() {
			return this.planeLostStage2;
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.SUPPORTATTACK;
		}
	}

	public class OpeningTaisen extends BattleDayStage {
		/**
		 *  0,开幕对潜<br>
		 *  1,第一轮炮击战<br>
		 *  2,第二轮炮击战<br>
		 *  3,第三轮炮击战
		 */
		private final int step;

		public OpeningTaisen(JsonObject json) {//开幕反潜用
			this(0, json);
		}

		public OpeningTaisen(int index, JsonObject json) {//炮击战用
			this.step = index;
			/** 敌联合舰队时存在(因为有混战)  */
			JsonArray at_eflag = json.containsKey("api_at_eflag") ? json.getJsonArray("api_at_eflag") : null;
			JsonArray at_list = json.getJsonArray("api_at_list");
			JsonArray at_type = json.getJsonArray("api_at_type");
			JsonArray df_list = json.getJsonArray("api_df_list");
			JsonArray damage = json.getJsonArray("api_damage");
			for (int x = 1; x < at_list.size(); x++) {
				Boolean enemyAttack = at_eflag == null ? null : (at_eflag.getInt(x) == 1);
				int attackIndex = at_list.getInt(x);
				int[] defenseIndexs = JsonUtils.getIntArray(df_list.getJsonArray(x));
				int[] damages = ToolUtils.doubleToInteger(JsonUtils.getDoubleArray(damage.getJsonArray(x)), d -> (int) Math.floor(d));
				int type = at_type.getInt(x);
				this.getBattleAttacks().add(new BattleOneAttack(enemyAttack, false, attackIndex, defenseIndexs, damages, type));
			}

			BattleOneAttackSimulator boas = new BattleOneAttackSimulator();
			this.getBattleAttacks().forEach(boa -> boas.accept(boa, this.getSimulatorObject(boa.isEnemyAttack())));
			this.accept(boas);
		}

		private Boolean getSimulatorObject(Boolean enemyAttack) {
			BattleType bt = AbstractBattleDay.this.getBattleType();
			if (enemyAttack == null) {//敌方非联合舰队
				if (bt == BattleType.BATTLE_DAY || bt == BattleType.PRACTICE_DAY) {//6v6
					return false;
				} else if (this.getStep() == 0) {//12v6,开幕对潜
					return true;
				} else if (bt == BattleType.COMBINEBATTLE_DAY_WATER) {
					switch (this.getStep()) {
						case 1:
						case 2:
							return false;
						case 3:
							return true;
					}
				} else if (bt == BattleType.COMBINEBATTLE_DAY) {
					switch (this.getStep()) {
						case 1:
							return true;
						case 2:
						case 3:
							return false;
					}
				} else {
					System.out.println(AbstractBattleDay.this.getBattleType());
					System.out.println(this.getStep());
				}
			}
			return null;//敌联合舰队时,在BattleOneAttackSimulator内部判断
		}

		public int getStep() {
			return this.step;
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.OPENINGTAISEN;
		}
	}

	public class OpeningAttack extends BattleDayStage {
		private int[] frai;
		private int[] erai;
		private int[] fdam;
		private int[] edam;
		private int[] fydam;
		private int[] eydam;

		public OpeningAttack(JsonObject json) {
			//-1开头,长度1+(6or12)
			this.frai = JsonUtils.getIntArray(json, "api_frai");//目标
			this.erai = JsonUtils.getIntArray(json, "api_erai");
			this.fdam = JsonUtils.getIntArray(json, "api_fdam");//受到的伤害
			this.edam = JsonUtils.getIntArray(json, "api_edam");
			this.fydam = JsonUtils.getIntArray(json, "api_fydam");//攻击
			this.eydam = JsonUtils.getIntArray(json, "api_eydam");

			switch (this.fdam.length) {//自方受伤
				case 1 + 12:
					this.getfAttackDamage().getDamage(Arrays.copyOfRange(this.fdam, 1, 7));
					this.getfAttackDamagecombine().getDamage(Arrays.copyOfRange(this.fdam, 7, 13));
					break;
				case 1 + 6:
					if (AbstractBattleDay.this.getfDeckCombine() == null) {//自方非联合舰队
						this.getfAttackDamage().getDamage(Arrays.copyOfRange(this.fdam, 1, 7));
					} else {//自方联合舰队,2队受到雷击
						this.getfAttackDamagecombine().getDamage(Arrays.copyOfRange(this.fdam, 1, 7));
					}
					break;
			}
			switch (this.edam.length) {//敌方受伤
				case 1 + 12:
					this.geteAttackDamage().getDamage(Arrays.copyOfRange(this.edam, 1, 7));
					this.geteAttackDamagecombine().getDamage(Arrays.copyOfRange(this.edam, 7, 13));
					break;
				case 1 + 6:
					if (AbstractBattleDay.this.geteDeckCombine() == null) {//敌方非联合舰队
						this.geteAttackDamage().getDamage(Arrays.copyOfRange(this.edam, 1, 7));
					} else {
						this.geteAttackDamagecombine().getDamage(Arrays.copyOfRange(this.edam, 1, 7));
					}
					break;
			}
			switch (this.fydam.length) {//自方攻击
				case 1 + 12:
					this.getfAttackDamage().setAttack(Arrays.copyOfRange(this.fydam, 1, 7));
					this.getfAttackDamagecombine().setAttack(Arrays.copyOfRange(this.fydam, 7, 13));
					break;
				case 1 + 6:
					if (AbstractBattleDay.this.getfDeckCombine() == null) {//自方非联合舰队
						this.getfAttackDamage().setAttack(Arrays.copyOfRange(this.fydam, 1, 7));
					} else {//自方联合舰队
						this.getfAttackDamagecombine().setAttack(Arrays.copyOfRange(this.fydam, 1, 7));
					}
					break;
			}
			switch (this.eydam.length) {//敌方攻击
				case 1 + 12:
					this.geteAttackDamage().setAttack(Arrays.copyOfRange(this.eydam, 1, 7));
					this.geteAttackDamagecombine().setAttack(Arrays.copyOfRange(this.eydam, 7, 13));
					break;
				case 1 + 6:
					if (AbstractBattleDay.this.geteDeckCombine() == null) {//敌方非联合舰队
						this.geteAttackDamage().setAttack(Arrays.copyOfRange(this.eydam, 1, 7));
					} else {
						this.geteAttackDamagecombine().setAttack(Arrays.copyOfRange(this.eydam, 1, 7));
					}
					break;
			}
		}

		public int[] getFrai() {
			return this.frai;
		}

		public int[] getErai() {
			return this.erai;
		}

		public int[] getFdam() {
			return this.fdam;
		}

		public int[] getEdam() {
			return this.edam;
		}

		public int[] getFydam() {
			return this.fydam;
		}

		public int[] getEydam() {
			return this.eydam;
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.OPENINGATTACK;
		}
	}

	public class Hougeki extends OpeningTaisen {

		public Hougeki(int index, JsonObject json) {
			super(index, json);
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.HOUGEKI;
		}
	}

	public class Raigeki extends OpeningAttack {

		public Raigeki(JsonObject json) {
			super(json);
		}

		@Override
		public BattleDayStageType getType() {
			return BattleDayStageType.RAIGEKI;
		}
	}

}
