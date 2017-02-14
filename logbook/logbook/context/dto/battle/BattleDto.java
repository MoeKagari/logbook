package logbook.context.dto.battle;

import java.awt.Point;
import java.util.ArrayList;

/**
 * 出击之后到回港之前所有dto的超类
 * @author MoeKagari
 */
public interface BattleDto {

	public static String getSearch(int id) {
		switch (id) {
			case 1:
			case 2://有未归还
			case 3://全部未归还
				return "成功";
			case 4:
				return "失败";
			case 5://没有舰载机
				return "成功";
			case 6://没有舰载机
				return "失败";
			default:
				return Integer.toString(id);
		}
	}

	public static String getHangxiang(int id) {
		switch (id) {
			case 1:
				return "同航战";
			case 2:
				return "反航战";
			case 3:
				return "T有利";
			case 4:
				return "T不利";
			default:
				return Integer.toString(id);
		}
	}

	public static String getZhenxin(int id) {
		switch (id) {
			case 1:
				return "单纵阵";
			case 2:
				return "复纵阵";
			case 3:
				return "轮型阵";
			case 4:
				return "梯形阵";
			case 5:
				return "单横阵";
			default:
				return Integer.toString(id);
		}
	}

	public static String getSeiku(int id) {
		switch (id) {
			case 0:
				return "制空均衡";
			case 1:
				return "制空确保";
			case 2:
				return "制空优势";
			case 3:
				return "制空劣势";
			case 4:
				return "制空丧失";
			default:
				return Integer.toString(id);
		}
	}

	public static String getNextPointType(int nextEventId, int nextEventKind) {
		switch (nextEventId) {
			case 2:
				switch (nextEventKind) {
					case 0:
						return "资源获得";
				}
				break;
			case 3:
				switch (nextEventKind) {
					case 0:
						return "渦潮";
				}
				break;
			case 4:
				switch (nextEventKind) {
					case 1:
						return "通常战斗";
					case 2:
						return "夜战";
					case 4:
						return "空袭战";
					case 6:
						return "长距离空袭战";
				}
				break;
			case 5:
				switch (nextEventKind) {
					case 1:
						return "BOSS";
					case 5:
						return "联合舰队BOSS";
				}
				break;
			case 6:
				switch (nextEventKind) {
					case 0:
						return "気のせいだった";
					case 1:
						return "敵影を見ず";
					case 2:
						return "能动分歧";
					case 3:
						return "穏やかな海です";
				}
				break;
			case 7:
				switch (nextEventKind) {
					case 0:
						return "航空侦察";
					case 4:
						return "航空战";
				}
				break;
			case 8:
				switch (nextEventKind) {
					case 0:
						return "船团护卫成功";
				}
				break;
			case 9:
				switch (nextEventKind) {
					case 0:
						return "扬陆地点";
				}
				break;
		}

		return nextEventId + "-" + nextEventKind;
	};

	public static String getRank(String rank) {
		switch (rank) {
			case "S":
				return "S胜利";
			case "A":
				return "A胜利";
			case "B":
				return "B战术的胜利";
			case "C":
				return "C战术的败北";
			case "D":
				return "D败北";
			case "E":
				return "E败北";
			default:
				return rank;
		}
	}

	public static String getSupportType(int type) {
		switch (type) {
			case 1:
				return "航空支援";
			case 2:
				return "炮击支援";
			case 3:
				return "雷击支援";
			default:
				return "新支援类型:" + type;
		}
	}

	/*-----------------------------------------------------------------------------*/

	public BattleType getBattleType();

	/**
	 * 战斗时的舰队的信息
	 * @author MoeKagari
	 */
	public class BattleDeck {
		private ArrayList<Integer> escapes = new ArrayList<>();
		private final boolean isCombine;
		private final boolean isEnemy;
		private final int[] nowhps;
		private final int[] maxhps;

		/**
		 * 长度为6
		 */
		public BattleDeck(boolean isCombine, boolean isEnemy, int[] nowhps, int[] maxhps) {
			this.isCombine = isCombine;
			this.isEnemy = isEnemy;
			this.nowhps = nowhps;
			this.maxhps = maxhps;
		}

		public boolean exist() {
			for (int nowhp : nowhps) {
				if (nowhp != -1) {
					return true;
				}
			}
			return false;
		}

		public ArrayList<Integer> getEscapes() {
			return escapes;
		}

		public int getShipCount() {
			int count = 0;
			for (int i = 0; i < 6; i++) {
				if (nowhps[i] != -1) {
					count++;
				}
			}
			return count;
		}

		public int[] getNowhp() {
			return this.nowhps;
		}

		public int[] getMaxhp() {
			return this.maxhps;
		}

		public boolean isEnemy() {
			return this.isEnemy;
		}

		public boolean isCombine() {
			return this.isCombine;
		}
	}

	/**
	 * 攻击方index,防御方indexs,伤害dmgs
	 * @author MoeKagari
	 */
	public class BattleOneAttack {
		/** 敌联合舰队时存在(因为有混战)  */
		private final Boolean enemyAttack;
		private final int attackIndex;//攻击方位置(1-12),enemyAttack所代表的两只舰队,非联合舰队时,自方舰队在前,联合舰队时,第一舰队在前
		private final int[] defenseIndexs;//attackIndex的对方
		private final int[] dmgs;//此次造成的伤害,与defenseIndexs长度相同
		private final int attackType;//攻击类型
		private boolean isMidnight = false;

		/**
		 * 夜战用
		 */
		public BattleOneAttack(int attackIndex, int[] defenseIndexs, int[] dmgs, int attackType) {
			this(null, true, attackIndex, defenseIndexs, dmgs, attackType);
		}

		/**
		 * 昼战用
		 */
		public BattleOneAttack(Boolean enemyAttack, boolean isMidnight, int attackIndex, int[] defenseIndexs, int[] dmgs, int attackType) {
			this.enemyAttack = enemyAttack;
			this.attackIndex = attackIndex;
			this.defenseIndexs = defenseIndexs;
			this.dmgs = dmgs;
			this.attackType = attackType;
			this.isMidnight = isMidnight;
		}

		public Boolean isEnemyAttack() {
			return enemyAttack;
		}

		public int getAttackType() {
			return attackType;
		}

		public boolean isMidnight() {
			return isMidnight;
		}

		public int getAttackIndex() {
			return attackIndex;
		}

		public int[] getDefenseIndexs() {
			return defenseIndexs;
		}

		public int[] getDamages() {
			return dmgs;
		}
	}

	/**
	 * 接收{@link BattleOneAttack}进行模拟
	 * @author MoeKagari
	 */
	public class BattleOneAttackSimulator {
		private final int[] fdmg = new int[6];
		private final int[] fatt = new int[6];
		private final int[] edmg = new int[6];
		private final int[] eatt = new int[6];
		private final int[] fdmgco = new int[6];
		private final int[] fattco = new int[6];
		private final int[] edmgco = new int[6];
		private final int[] eattco = new int[6];

		/**
		 * 
		 * @param boa
		 * @param fcombine 自方参战deck是否是联合舰队
		 */
		public void accept(BattleOneAttack boa, Boolean fcombine) {
			Boolean enemyAttack = boa.isEnemyAttack();
			int attackIndex = boa.getAttackIndex();
			int[] defenseIndexs = boa.getDefenseIndexs();
			int[] damages = boa.getDamages();
			for (int i = 0; i < damages.length; i++) {
				int da = damages[i] < 0 ? 0 : damages[i];
				Point p1 = new Point((attackIndex - 1) / 6, (attackIndex - 1) % 6);
				Point p2 = new Point((defenseIndexs[i] - 1) / 6, (defenseIndexs[i] - 1) % 6);
				if (enemyAttack == null && fcombine != null) {//敌方非联合舰队
					new int[][] { fcombine ? fattco : fatt, eatt }[p1.x][p1.y] += da;
					new int[][] { fcombine ? fdmgco : fdmg, edmg }[p2.x][p2.y] += da;
				} else if (enemyAttack == false) {//敌联合舰队,我方攻击
					new int[][] { fatt, fattco }[p1.x][p1.y] += da;
					new int[][] { edmg, edmgco }[p2.x][p2.y] += da;
				} else if (enemyAttack == true) {//敌联合舰队,敌方攻击
					new int[][] { eatt, eattco }[p1.x][p1.y] += da;
					new int[][] { fdmg, fdmgco }[p2.x][p2.y] += da;
				} else {
					System.out.println("enemyAttack == null && fcombine == null");
				}
			}
		}

		public int[] getFdmg() {
			return fdmg;
		}

		public int[] getFatt() {
			return fatt;
		}

		public int[] getEdmg() {
			return edmg;
		}

		public int[] getEatt() {
			return eatt;
		}

		public int[] getFdmgco() {
			return fdmgco;
		}

		public int[] getFattco() {
			return fattco;
		}

		public int[] getEdmgco() {
			return edmgco;
		}

		public int[] getEattco() {
			return eattco;
		}
	}

	/**
	 * BattleDeck 的attack和damage<br>
	 * 供每个 BattleStage 用
	 * @author MoeKagari
	 */
	public class BattleDeckAttackDamage {
		private final int[] dmg = new int[6];
		private final int[] attack = new int[6];

		public int[] getDamage() {
			return this.dmg;
		}

		public int[] getAttack() {
			return this.attack;
		}

		/**
		 * 受到伤害
		 */
		public void getDamage(int[] gd) {
			for (int i = 0; i < 6; i++) {
				dmg[i] += gd[i];
			}
		}

		/**
		 * 攻击输出
		 */
		public void setAttack(int[] sa) {
			for (int i = 0; i < 6; i++) {
				attack[i] += sa[i];
			}
		}

		public BattleDeckAttackDamage add(BattleDeckAttackDamage next) {
			for (int i = 0; i < 6; i++) {
				dmg[i] = dmg[i] + next.dmg[i];
				attack[i] = attack[i] + next.attack[i];
			}
			return this;
		}
	}

}
